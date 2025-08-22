package co.selim.vertx_avaje_inject.api;

import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

public final class RoutingContexts {

  private static final CharSequence APPLICATION_JSON = HttpHeaders.createOptimized("application/json; charset=utf-8");
  private static final Jsonb jsonb = JsonbBuilder.create();

  private RoutingContexts() {
  }

  public static void respondWithJson(RoutingContext routingContext, int statusCode, Object body) {
    routingContext
      .response()
      .setStatusCode(statusCode)
      .putHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
      .end(jsonb.toJson(body));
  }

  public static void respondWithJson(RoutingContext routingContext, Object body) {
    respondWithJson(routingContext, 200, body);
  }

  public static <T> T getBodyAsPojo(RoutingContext routingContext, Class<T> pojoClass) {
    return jsonb.fromJson(routingContext.body().asString(), pojoClass);
  }
}
