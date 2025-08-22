package co.selim.vertx_avaje_inject.account;

import co.selim.vertx_avaje_inject.api.ApiHandler;
import io.avaje.inject.Prototype;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.FaviconHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

@Prototype
public class ApiVerticle extends VerticleBase {

  private static final Logger LOG = LoggerFactory.getLogger(ApiVerticle.class);
  private final Set<ApiHandler> apiHandlers;

  @Inject
  public ApiVerticle(Set<ApiHandler> apiHandlers) {
    this.apiHandlers = apiHandlers;
  }

  @Override
  public Future<?> start() {
    Router router = Router.router(vertx);
    router.route()
      .handler(LoggerHandler.create(LoggerFormat.TINY))
      .handler(FaviconHandler.create(vertx));

    Router apiRouter = Router.router(vertx);
    apiHandlers.forEach(handler ->
      handler.init(apiRouter)
    );

    router
      .route("/api/*")
      .subRouter(apiRouter);

    return vertx.createHttpServer()
      .requestHandler(router)
      .listen(8888)
      .onSuccess(http ->
        LOG.info("Listening on port {}", http.actualPort())
      );
  }
}
