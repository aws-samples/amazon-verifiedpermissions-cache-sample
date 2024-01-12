package com.demos.aws;

import io.vertx.core.json.JsonObject;
import jakarta.validation.constraints.NotBlank;

public class PolicyActionResource {

  @NotBlank(message = "Action entity Id is required.")
  public String actionId;
  @NotBlank(message = "Action entity type is required.")
  public String actionType;

  public PolicyActionResource() {

  }

  public JsonObject getJson() {
    return new JsonObject()
        .put("actionType", this.actionType)
        .put("actionId", this.actionId);
  }
}
