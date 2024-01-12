package com.demos.aws;

import java.util.Iterator;
import java.util.List;

import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheKey;
import io.quarkus.cache.CacheResult;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.verifiedpermissions.VerifiedPermissionsClient;
import software.amazon.awssdk.services.verifiedpermissions.model.ActionIdentifier;
import software.amazon.awssdk.services.verifiedpermissions.model.DeterminingPolicyItem;
import software.amazon.awssdk.services.verifiedpermissions.model.EntityIdentifier;
import software.amazon.awssdk.services.verifiedpermissions.model.EvaluationErrorItem;
import software.amazon.awssdk.services.verifiedpermissions.model.IsAuthorizedRequest;
import software.amazon.awssdk.services.verifiedpermissions.model.IsAuthorizedResponse;

import software.amazon.awssdk.services.verifiedpermissions.model.AccessDeniedException;
import software.amazon.awssdk.services.verifiedpermissions.model.InternalServerException;
import software.amazon.awssdk.services.verifiedpermissions.model.ResourceNotFoundException;
import software.amazon.awssdk.services.verifiedpermissions.model.ThrottlingException;
import software.amazon.awssdk.services.verifiedpermissions.model.ValidationException;
import software.amazon.awssdk.core.exception.SdkClientException;

@ApplicationScoped
public class PermissionsClient {
  private VerifiedPermissionsClient vpc;

  public PermissionsClient() {

  }

  public void buildVerifiedPermissionsClient(String region) {
    this.vpc = VerifiedPermissionsClient
        .builder()
        .region(Region.of(region))
        .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
        .build();
  }

  private IsAuthorizedRequest getAuthorizationRequestFromPayload(JsonObject payload) {
    int i = 0;
    int j = 0;
    int k = 0;
    if (payload.containsKey("principal")) {
      i = 1;
    }
    if (payload.containsKey("action")) {
      j = 1;
    }
    if (payload.containsKey(("resource"))) {
      k = 1;
    }
    int option = 4 * i + 2 * j + 1 * k;

    IsAuthorizedRequest.Builder requestBuilder = IsAuthorizedRequest.builder();
    IsAuthorizedRequest authzRequest = requestBuilder.build();
    switch (option) {
      case 0:
        // Principal, action and resource are not present.
        authzRequest = requestBuilder
            .policyStoreId(payload.getString("policyStoreId"))
            .build();
        break;

      case 1:
        // Only resource is present
        authzRequest = requestBuilder
            .resource(EntityIdentifier
                .builder()
                .entityId(payload.getJsonObject("resource").getString("entityId"))
                .entityType(payload.getJsonObject("resource").getString("entityType"))
                .build())
            .policyStoreId(payload.getString("policyStoreId"))
            .build();
        break;

      case 2:
        // Only action is present
        authzRequest = requestBuilder
            .action(ActionIdentifier
                .builder()
                .actionId(payload.getJsonObject("action").getString("actionId"))
                .actionType(payload.getJsonObject("action").getString("actionType"))
                .build())
            .policyStoreId(payload.getString("policyStoreId"))
            .build();
        break;

      case 3:
        // Action and resource are present
        authzRequest = requestBuilder
            .resource(EntityIdentifier
                .builder()
                .entityId(payload.getJsonObject("resource").getString("entityId"))
                .entityType(payload.getJsonObject("resource").getString("entityType"))
                .build())
            .action(ActionIdentifier
                .builder()
                .actionId(payload.getJsonObject("action").getString("actionId"))
                .actionType(payload.getJsonObject("action").getString("actionType"))
                .build())
            .policyStoreId(payload.getString("policyStoreId"))
            .build();
        break;

      case 4:
        // Only principal is present
        authzRequest = requestBuilder
            .principal(EntityIdentifier
                .builder()
                .entityId(payload.getJsonObject("principal").getString("entityId"))
                .entityType(payload.getJsonObject("principal").getString("entityType"))
                .build())
            .policyStoreId(payload.getString("policyStoreId"))
            .build();
        break;

      case 5:
        // Principal and resource are present
        authzRequest = requestBuilder
            .principal(EntityIdentifier
                .builder()
                .entityId(payload.getJsonObject("principal").getString("entityId"))
                .entityType(payload.getJsonObject("principal").getString("entityType"))
                .build())
            .resource(EntityIdentifier
                .builder()
                .entityId(payload.getJsonObject("resource").getString("entityId"))
                .entityType(payload.getJsonObject("resource").getString("entityType"))
                .build())
            .policyStoreId(payload.getString("policyStoreId"))
            .build();
        break;

      case 6:
        // Only Principal and action are present
        authzRequest = requestBuilder
            .principal(EntityIdentifier
                .builder()
                .entityId(payload.getJsonObject("principal").getString("entityId"))
                .entityType(payload.getJsonObject("principal").getString("entityType"))
                .build())
            .action(ActionIdentifier
                .builder()
                .actionId(payload.getJsonObject("action").getString("actionId"))
                .actionType(payload.getJsonObject("action").getString("actionType"))
                .build())
            .policyStoreId(payload.getString("policyStoreId"))
            .build();
        break;

      case 7:
        // Principal, Action and Resource are present
        authzRequest = requestBuilder
            .principal(EntityIdentifier
                .builder()
                .entityId(payload.getJsonObject("principal").getString("entityId"))
                .entityType(payload.getJsonObject("principal").getString("entityType"))
                .build())
            .action(ActionIdentifier
                .builder()
                .actionId(payload.getJsonObject("action").getString("actionId"))
                .actionType(payload.getJsonObject("action").getString("actionType"))
                .build())
            .resource(EntityIdentifier
                .builder()
                .entityId(payload.getJsonObject("resource").getString("entityId"))
                .entityType(payload.getJsonObject("resource").getString("entityType"))
                .build())
            .policyStoreId(payload.getString("policyStoreId"))
            .build();
        break;

      default:
        break;
    }

    return authzRequest;
  }

  @CacheResult(cacheName = "permissionsCache")
  public JsonObject authzCacheLookUp(@CacheKey String authz) {
    JsonObject authzResponse = new JsonObject();
    try {
      // Invoke API
      IsAuthorizedResponse response = vpc.isAuthorized(getAuthorizationRequestFromPayload(new JsonObject(authz)));

      // Get decision
      authzResponse.put("decision", response.decisionAsString());

      // Build array of determining policies
      JsonArray policyArray = new JsonArray();
      List<DeterminingPolicyItem> d = response.determiningPolicies();
      Iterator<DeterminingPolicyItem> i = d.iterator();
      while (i.hasNext()) {
        policyArray.add(new JsonObject().put("policyId", i.next().policyId()));
      }
      authzResponse.put("determiningPolicies", policyArray);

      // Build array of errors
      // TODO: Is error arrays returned only in case of 200?
      List<EvaluationErrorItem> e = response.errors();
      JsonArray errorArray = new JsonArray();
      Iterator<EvaluationErrorItem> j = e.iterator();
      while (j.hasNext()) {
        errorArray.add(new JsonObject().put("errorDescription", j.next().errorDescription()));
      }
      authzResponse.put("errors", errorArray);

      // Respond with result of authorization check
      return authzResponse;
    } catch (AccessDeniedException ade) {
      return new JsonObject()
          .put("exception", "AccessDeniedException")
          .put("requestId", ade.requestId())
          .put("statusCode", ade.statusCode())
          .put("message", ade.getLocalizedMessage());
    } catch (InternalServerException ise) {
      return new JsonObject()
          .put("exception", "InternalServerException")
          .put("requestId", ise.requestId())
          .put("statusCode", ise.statusCode())
          .put("message", ise.getLocalizedMessage());
    } catch (ResourceNotFoundException rnfe) {
      return new JsonObject()
          .put("exception", "ResourceNotFoundException")
          .put("requestId", rnfe.requestId())
          .put("statusCode", rnfe.statusCode())
          .put("message", rnfe.getLocalizedMessage());
    } catch (ThrottlingException te) {
      return new JsonObject()
          .put("exception", "ThrottlingException")
          .put("requestId", te.requestId())
          .put("statusCode", te.statusCode())
          .put("message", te.getLocalizedMessage());
    } catch (ValidationException ve) {
      return new JsonObject()
          .put("exception", "ValidationException")
          .put("requestId", ve.requestId())
          .put("statusCode", ve.statusCode())
          .put("message", ve.getLocalizedMessage());
    } catch (SdkClientException sdke) {
      return new JsonObject()
          .put("exception", "SdkClientException")
          .put("statusCode", 400)
          .put("message", sdke.getLocalizedMessage());
    }
  }

  @CacheInvalidate(cacheName = "permissionsCache")
  public void deleteCacheItem(@CacheKey String authz) {

  }
}