package com.demos.aws;

import io.vertx.core.json.JsonObject;
import jakarta.validation.constraints.NotBlank;

public class PolicyResourceResource {

  @NotBlank(message = "Resource entity Id is required.")
  public String entityId;

  @NotBlank(message = "Resource entity type is required.")
  public String entityType;

  public PolicyResourceResource() {

  }

  public JsonObject getJson() {
    return new JsonObject()
        .put("entityType", this.entityType)
        .put("entityId", this.entityId);
  }
}
