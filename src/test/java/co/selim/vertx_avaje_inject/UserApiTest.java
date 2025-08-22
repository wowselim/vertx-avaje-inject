package co.selim.vertx_avaje_inject;

import co.selim.vertx_avaje_inject.account.NewAccount;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.is;

public class UserApiTest extends IntegrationTest {

  @Test
  public void testGet() {
    RestAssured.when()
      .get("/api/users")
      .then()
      .assertThat()
      .body("size()", is(2));
  }

  @Test
  public void testGetById() {
    RestAssured.when()
      .get("/api/users/2")
      .then()
      .assertThat()
      .body("id", is(2))
      .body("name", is("Jane Doe"));
  }

  @Test
  public void testSave() {
    RestAssured.given()
      .body(new NewAccount("Chuck Norris"), objectMapper)
      .when()
      .post("/api/users")
      .then()
      .assertThat()
      .body("id", is(3))
      .body("name", is("Chuck Norris"));
  }
}
