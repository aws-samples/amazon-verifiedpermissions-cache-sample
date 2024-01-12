package com.demos.aws;

import io.vertx.core.json.JsonObject;
import jakarta.validation.constraints.NotBlank;

public class PolicyPrincipalResource {

  @NotBlank(message = "Principal entity Id is required.")
  public String entityId;

  @NotBlank(message = "Principal entity type is required.")
  public String entityType;

  public PolicyPrincipalResource() {

  }

  public JsonObject getJson() {
    return new JsonObject()
        .put("entityType", this.entityType)
        .put("entityId", this.entityId);
  }
}
