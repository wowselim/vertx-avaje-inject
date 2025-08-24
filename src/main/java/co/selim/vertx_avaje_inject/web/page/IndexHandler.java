package co.selim.vertx_avaje_inject.web.page;

import co.selim.vertx_avaje_inject.web.HttpHandler;
import io.vertx.ext.web.Router;
import jakarta.inject.Singleton;

import java.util.Map;

import static co.selim.vertx_avaje_inject.web.RoutingContexts.respondWithHtml;

@Singleton
public class IndexHandler implements HttpHandler.WebHandler {

  @Override
  public void init(Router router) {
    router.get("/")
      .handler(ctx -> {
        respondWithHtml(ctx, "index", Map.of());
      });
  }
}
