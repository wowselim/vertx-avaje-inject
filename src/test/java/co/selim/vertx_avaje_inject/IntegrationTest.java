package co.selim.vertx_avaje_inject;

import io.restassured.RestAssured;
import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperDeserializationContext;
import io.restassured.mapper.ObjectMapperSerializationContext;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class IntegrationTest {

  private final WebApplication webApplication = new WebApplication();
  private final Jsonb jsonb = JsonbBuilder.create();
  protected final ObjectMapper objectMapper = new ObjectMapper() {
    @Override
    public Object deserialize(ObjectMapperDeserializationContext objectMapperDeserializationContext) {
      return jsonb.fromJson(objectMapperDeserializationContext.getDataToDeserialize().asString(), objectMapperDeserializationContext.getType());
    }

    @Override
    public Object serialize(ObjectMapperSerializationContext objectMapperSerializationContext) {
      return jsonb.toJson(objectMapperSerializationContext.getObjectToSerialize());
    }
  };

  @BeforeAll
  public static void beforeAll() {
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = 8888;
  }

  @BeforeEach
  public void beforeEach(Vertx vertx) {
    webApplication.start(vertx);
  }

  @AfterEach
  public void afterEach(Vertx vertx) {
    vertx.close();
  }
}
