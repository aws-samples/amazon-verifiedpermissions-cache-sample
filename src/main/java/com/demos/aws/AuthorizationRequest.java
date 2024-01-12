package com.demos.aws;

import io.vertx.core.json.JsonObject;
import jakarta.inject.Inject;
import jakarta.validation.Validator;

public class AuthorizationRequest {
  @Inject
  Validator validator;

  public PolicyPrincipalResource principal;
  public PolicyActionResource action;
  public PolicyResourceResource resource;
  public String policyStoreId;

  public AuthorizationRequest() {

  }

  public JsonObject getAuthzRequest() {
    int i = (this.principal == null) ? 1 : 0;
    int j = (this.action == null) ? 1 : 0;
    int k = (this.resource == null) ? 1 : 0;
    int option = 4 * i + 2 * j + 1 * k;

    JsonObject authzRequest = new JsonObject();
    switch (option) {
      case 0:
        // Principal, action and resource are present.
        authzRequest.put("policyStoreId", this.policyStoreId)
            .put("principal", this.principal.getJson())
            .put("action", this.action.getJson())
            .put("resource", this.resource.getJson());
        break;

      case 1:
        // Resource is null; others are present.
        authzRequest.put("policyStoreId", this.policyStoreId)
            .put("principal", this.principal.getJson())
            .put("action", this.action.getJson());
        break;

      case 2:
        // Action is null; others are present.
        authzRequest.put("policyStoreId", this.policyStoreId)
            .put("principal", this.principal.getJson())
            .put("resource", this.resource.getJson());
        break;

      case 3:
        // Action and resource are null.
        authzRequest.put("policyStoreId", this.policyStoreId)
            .put("principal", this.principal.getJson());
        break;

      case 4:
        // Principal is null; others are present.
        authzRequest.put("policyStoreId", this.policyStoreId)
            .put("action", this.action.getJson())
            .put("resource", this.resource.getJson());
        break;

      case 5:
        // Principal and resource are null.
        authzRequest.put("policyStoreId", this.policyStoreId)
            .put("action", this.action.getJson());
        break;

      case 6:
        // Principal and action are null.
        authzRequest.put("policyStoreId", this.policyStoreId)
            .put("resource", this.resource.getJson());
        break;

      case 7:
        // Principal, Action and Resource are null
        authzRequest.put("policyStoreId", this.policyStoreId);
        break;

      default:
        break;
    }

    return authzRequest;
  }

  public String getPolicyStoreId() {
    return this.policyStoreId;
  }
}
