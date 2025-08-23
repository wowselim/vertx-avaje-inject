package co.selim.vertx_avaje_inject;

import io.avaje.config.Config;
import io.avaje.jsonb.Jsonb;
import io.restassured.RestAssured;
import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperDeserializationContext;
import io.restassured.mapper.ObjectMapperSerializationContext;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class IntegrationTest {

  private final Application application = new Application();
  private final Jsonb jsonb = Jsonb.instance();
  protected final ObjectMapper objectMapper = new ObjectMapper() {
    @Override
    public Object deserialize(ObjectMapperDeserializationContext o) {
      return jsonb.type(o.getType())
        .fromJson(o.getDataToDeserialize().asString());
    }

    @Override
    public Object serialize(ObjectMapperSerializationContext o) {
      return jsonb.toJson(o.getObjectToSerialize());
    }
  };

  @BeforeEach
  public void beforeEach(Vertx vertx) {
    application.start(vertx);
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = Config.getInt("server.port");
  }

  @AfterEach
  public void afterEach(Vertx vertx) {
    vertx.close();
  }
}
