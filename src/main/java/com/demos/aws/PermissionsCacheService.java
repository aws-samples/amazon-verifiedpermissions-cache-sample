package com.demos.aws;

import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;

import java.util.Iterator;

import org.jboss.resteasy.reactive.RestQuery;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;

import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response.Status;

@Path("/permissions")
public class PermissionsCacheService {
  @Inject
  AuthorizationRequestQuerySet arqs;

  @Inject
  AuthorizationRequestResource ars;

  @Inject
  PermissionsClient pc;

  @Inject
  Validator validator;

  @Path("/authz")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public RestResponse<JsonObject> authz(
      @NotBlank(message = "Region identifier is required.") @RestQuery("region") String region,
      AuthorizationRequest ar) {
    // Payload validations
    ars.validatePolicyStoreId(ar.policyStoreId);
    ars.validatePolicyPrincipal(ar.principal);
    ars.validatePolicyAction(ar.action);
    ars.validatePolicyResource(ar.resource);

    // Build query set
    arqs.addQuery(region + ar.policyStoreId, ar.getAuthzRequest().put("region", region).encode());

    // Build client, invoke API and save response
    pc.buildVerifiedPermissionsClient(region);
    JsonObject authzResponse = pc.authzCacheLookUp(ar.getAuthzRequest().put("region", region).encode());

    // Build and send response
    return responseBuilder(authzResponse);
  }

  private RestResponse<JsonObject> responseBuilder(JsonObject authzResponse) {
    if (authzResponse.containsKey("exception")) {
      if (authzResponse.getInteger("statusCode").intValue() == 400) {
        return ResponseBuilder.create(Status.BAD_REQUEST, authzResponse).build();
      } else if (authzResponse.getInteger("statusCode").intValue() == 404) {
        return ResponseBuilder.create(Status.NOT_FOUND, authzResponse).build();
      } else if (authzResponse.getInteger("statusCode").intValue() == 500) {
        return ResponseBuilder.create(Status.INTERNAL_SERVER_ERROR, authzResponse).build();
      } else {
        return ResponseBuilder.create(Status.valueOf(authzResponse.getInteger("statusCode").toString()), authzResponse)
            .build();
      }
    } else {
      // TODO: Is this 200?
      return ResponseBuilder.create(Status.OK, authzResponse).build();
    }
  }

  @Path("/flush")
  @DELETE
  public void cacheFlush(@NotBlank(message = "Region identifier is required.") @RestQuery("region") String region,
      @NotBlank(message = "Policy store identifier is required.") @RestQuery("policyStoreId") String policyStoreId) {
    Iterator<String> i = arqs.getQueries(region + policyStoreId).iterator();
    while (i.hasNext()) {
      pc.deleteCacheItem(i.next());
    }
  }
}
