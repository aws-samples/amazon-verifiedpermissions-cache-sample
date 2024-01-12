package com.demos.aws;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.vertx.core.json.JsonObject;

@QuarkusTest
public class PermissionsCacheTest {
  String REGION_ID = System.getenv("REGION_ID");
  String POLICY_STORE_ID = System.getenv("POLICY_STORE_ID");

  private final String CACHE_PATH = "/permissions/authz?region=" + REGION_ID;

  @Test
  @DisplayName(value = "Only valid policy store ID is present.")
  public void authz0() {
    given()
        .contentType(ContentType.JSON)
        .body(new JsonObject()
            .put("policyStoreId", POLICY_STORE_ID)
            .encode())
        .when()
        .post(CACHE_PATH)
        .then()
        .statusCode(200)
        .assertThat()
        .body("decision", equalTo("DENY"));
  }

  @Test
  @DisplayName(value = "Only valid action and policy store ID.")
  public void authz1() {
    given()
        .contentType(ContentType.JSON)
        .body(new JsonObject()
            .put("policyStoreId", POLICY_STORE_ID)
            .put("action", new JsonObject()
                .put("actionId", "ReadBook")
                .put("actionType", "bookStore::Action"))
            .encode())
        .when()
        .post(CACHE_PATH)
        .then()
        .statusCode(200)
        .body("decision", equalTo("DENY"));
  }

  @Test
  @DisplayName(value = "Only valid action and policy store ID.")
  public void authz2() {
    given()
        .contentType(ContentType.JSON)
        .body(new JsonObject()
            .put("policyStoreId", POLICY_STORE_ID)
            .put("action", new JsonObject()
                .put("actionId", "ReadBook")
                .put("actionType", "bookStore::Action"))
            .encode())
        .when()
        .post(CACHE_PATH)
        .then()
        .statusCode(200)
        .body("decision", equalTo("DENY"));
  }

  @Test
  @DisplayName(value = "Only valid action, resource and policy store ID.")
  public void authz3() {
    given()
        .contentType(ContentType.JSON)
        .body(new JsonObject()
            .put("policyStoreId", POLICY_STORE_ID)
            .put("action", new JsonObject()
                .put("actionId", "ReadBook")
                .put("actionType", "bookStore::Action"))
            .put("resource", new JsonObject()
                .put("entityId", "book")
                .put("entityType", "bookStore::Book"))
            .encode())
        .when()
        .post(CACHE_PATH)
        .then()
        .statusCode(200)
        .body("decision", equalTo("DENY"));
  }

  @Test
  @DisplayName(value = "Only valid principal and policy store ID.")
  public void authz4() {
    given()
        .contentType(ContentType.JSON)
        .body(new JsonObject()
            .put("policyStoreId", POLICY_STORE_ID)
            .put("principal", new JsonObject()
                .put("entityId", "user")
                .put("entityType", "bookStore::User"))
            .encode())
        .when()
        .post(CACHE_PATH)
        .then()
        .statusCode(200)
        .body("decision", equalTo("DENY"));
  }

  @Test
  @DisplayName(value = "Only valid principal, resource and policy store ID.")
  public void authz5() {
    given()
        .contentType(ContentType.JSON)
        .body(new JsonObject()
            .put("policyStoreId", POLICY_STORE_ID)
            .put("principal", new JsonObject()
                .put("entityId", "user")
                .put("entityType", "bookStore::User"))
            .put("resource", new JsonObject()
                .put("entityId", "book")
                .put("entityType", "bookStore::Book"))
            .encode())
        .when()
        .post(CACHE_PATH)
        .then()
        .statusCode(200)
        .body("decision", equalTo("DENY"));
  }

  @Test
  @DisplayName(value = "Only valid principal and policy store ID.")
  public void authz6() {
    given()
        .contentType(ContentType.JSON)
        .body(new JsonObject()
            .put("policyStoreId", POLICY_STORE_ID)
            .put("principal", new JsonObject()
                .put("entityId", "user")
                .put("entityType", "bookStore::User"))
            .put("action", new JsonObject()
                .put("actionId", "ReadBook")
                .put("actionType", "bookStore::Action"))
            .encode())
        .when()
        .post(CACHE_PATH)
        .then()
        .statusCode(200)
        .body("decision", equalTo("DENY"));
  }

  @Test
  @DisplayName(value = "Complete valid payload - ALLOW decision.")
  public void authz7() {
    given()
        .contentType(ContentType.JSON)
        .body(new JsonObject()
            .put("policyStoreId", POLICY_STORE_ID)
            .put("principal", new JsonObject()
                .put("entityId", "user")
                .put("entityType", "bookStore::User"))
            .put("action", new JsonObject()
                .put("actionId", "ReadBook")
                .put("actionType", "bookStore::Action"))
            .put("resource", new JsonObject()
                .put("entityId", "book")
                .put("entityType", "bookStore::Book"))
            .encode())
        .when()
        .post(CACHE_PATH)
        .then()
        .statusCode(200)
        .body("decision", equalTo("ALLOW"));
  }

  @Test
  @DisplayName(value = "Complete valid payload - DENY decision.")
  public void authz8() {
    given()
        .contentType(ContentType.JSON)
        .body(new JsonObject()
            .put("policyStoreId", POLICY_STORE_ID)
            .put("principal", new JsonObject()
                .put("entityId", "reader2")
                .put("entityType", "bookStore::User"))
            .put("action", new JsonObject()
                .put("actionId", "ReadBook")
                .put("actionType", "bookStore::Action"))
            .put("resource", new JsonObject()
                .put("entityId", "thriller")
                .put("entityType", "bookStore::Book"))
            .encode())
        .when()
        .post(CACHE_PATH)
        .then()
        .statusCode(200)
        .body("decision", equalTo("DENY"));
  }

  @Test
  @DisplayName(value = "Non-existing policy store ID.")
  public void authz9() {
    given()
        .contentType(ContentType.JSON)
        .body(new JsonObject()
            .put("policyStoreId", "doesNotExist")
            .put("principal", new JsonObject()
                .put("entityId", "reader2")
                .put("entityType", "bookStore::User"))
            .put("action", new JsonObject()
                .put("actionId", "ReadBook")
                .put("actionType", "bookStore::Action"))
            .put("resource", new JsonObject()
                .put("entityId", "thriller")
                .put("entityType", "bookStore::Book"))
            .encode())
        .post(CACHE_PATH)
        .then()
        .statusCode(404);
  }

  @Test
  @DisplayName(value = "Hibernate validation - Missing region ID in query parameter.")
  public void authz10() {
    given()
        .contentType(ContentType.JSON)
        .body(new JsonObject()
            .put("policyStoreId", POLICY_STORE_ID)
            .put("principal", new JsonObject()
                .put("entityId", "reader2")
                .put("entityType", "bookStore::User"))
            .put("action", new JsonObject()
                .put("actionId", "ReadBook")
                .put("actionType", "bookStore::Action"))
            .put("resource", new JsonObject()
                .put("entityId", "thriller")
                .put("entityType", "bookStore::Book"))
            .encode())
        .when()
        .post("/permissions/authz")
        .then()
        .statusCode(400);
  }

  @Test
  @DisplayName(value = "Hibernate validation - Invalid policy store ID.")
  public void authz11() {
    given()
        .contentType(ContentType.JSON)
        .body(new JsonObject()
            .put("policyStoreId", "does not exist")
            .put("principal", new JsonObject()
                .put("entityId", "reader2")
                .put("entityType", "bookStore::User"))
            .put("action", new JsonObject()
                .put("actionId", "ReadBook")
                .put("actionType", "bookStore::Action"))
            .put("resource", new JsonObject()
                .put("entityId", "thriller")
                .put("entityType", "bookStore::Book"))
            .encode())
        .post(CACHE_PATH)
        .then()
        .statusCode(400);
  }

  @Test
  @DisplayName(value = "Hibernate validation - Long policy store ID.")
  public void authz12() {
    given()
        .contentType(ContentType.JSON)
        .body(new JsonObject()
            .put("policyStoreId",
                "LoremipsumdolorsitametcconsecteturadipiscingelitkSedtempusmagnasollicitudincsagittisorcisitametcegestasloremkVestibulumorcitortorcpharetrautquamnecimperdietsuscipitintegerkLoremipsumdolorsitametccosssd")
            .put("principal", new JsonObject()
                .put("entityId", "reader2")
                .put("entityType", "bookStore::User"))
            .put("action", new JsonObject()
                .put("actionId", "ReadBook")
                .put("actionType", "bookStore::Action"))
            .put("resource", new JsonObject()
                .put("entityId", "thriller")
                .put("entityType", "bookStore::Book"))
            .encode())
        .post(CACHE_PATH)
        .then()
        .statusCode(400);
  }

  @Test
  @DisplayName(value = "Hibernate validation - Malformed principal object.")
  public void authz13() {
    given()
        .contentType(ContentType.JSON)
        .body(new JsonObject()
            .put("policyStoreId", POLICY_STORE_ID)
            .put("principal", new JsonObject()
                .put("entityId", "reader2"))
            .put("action", new JsonObject()
                .put("actionId", "ReadBook")
                .put("actionType", "bookStore::Action"))
            .put("resource", new JsonObject()
                .put("entityId", "thriller")
                .put("entityType", "bookStore::Book"))
            .encode())
        .post(CACHE_PATH)
        .then()
        .statusCode(400);
  }

  @Test
  @DisplayName(value = "Hibernate validation - Malformed action object.")
  public void authz14() {
    given()
        .contentType(ContentType.JSON)
        .body(new JsonObject()
            .put("policyStoreId", POLICY_STORE_ID)
            .put("principal", new JsonObject()
                .put("entityId", "reader2")
                .put("entityType", "bookStore::User"))
            .put("action", new JsonObject()
                .put("actionType", "bookStore::Action"))
            .put("resource", new JsonObject()
                .put("entityId", "thriller")
                .put("entityType", "bookStore::Book"))
            .encode())
        .post(CACHE_PATH)
        .then()
        .statusCode(400);
  }

  @Test
  @DisplayName(value = "Hibernate validation - Malformed resource object.")
  public void authz15() {
    given()
        .contentType(ContentType.JSON)
        .body(new JsonObject()
            .put("policyStoreId", POLICY_STORE_ID)
            .put("principal", new JsonObject()
                .put("entityId", "reader2")
                .put("entityType", "bookStore::User"))
            .put("action", new JsonObject()
                .put("actionId", "ReadBook")
                .put("actionType", "bookStore::Action"))
            .put("resource", new JsonObject())
            .encode())
        .post(CACHE_PATH)
        .then()
        .statusCode(400);
  }
}
