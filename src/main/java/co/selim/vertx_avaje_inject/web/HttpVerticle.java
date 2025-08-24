package co.selim.vertx_avaje_inject.web;

import co.selim.vertx_avaje_inject.web.api.ErrorResponse;
import io.avaje.config.Config;
import io.avaje.inject.Prototype;
import io.avaje.validation.ConstraintViolationException;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.FaviconHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;
import io.vertx.ext.web.handler.StaticHandler;
import jakarta.inject.Inject;

import java.util.Set;

import static co.selim.vertx_avaje_inject.web.RoutingContexts.respondWithJson;

@Prototype
public class HttpVerticle extends VerticleBase {

  private final Set<HttpHandler.ApiHandler> apiHandlers;
  private final Set<HttpHandler.WebHandler> webHandlers;

  @Inject
  public HttpVerticle(
    Set<HttpHandler.ApiHandler> apiHandlers,
    Set<HttpHandler.WebHandler> webHandlers
  ) {
    this.apiHandlers = apiHandlers;
    this.webHandlers = webHandlers;
  }

  @Override
  public Future<?> start() {
    Router router = Router.router(vertx);
    router.route()
      .handler(LoggerHandler.create(LoggerFormat.TINY))
      .failureHandler(ctx -> {
        if (ctx.response().ended()) {
          return;
        }

        Throwable failure = ctx.failure();
        if (failure instanceof ConstraintViolationException cve) {
          respondWithJson(ctx, 400, new ErrorResponse(400, cve.violations().toString()));
        } else {
          respondWithJson(ctx, 500, new ErrorResponse(500, failure.getMessage()));
        }
      });

    router.get()
      .handler(FaviconHandler.create(vertx))
      .handler(StaticHandler.create("styles"));

    Router apiRouter = Router.router(vertx);
    apiHandlers.forEach(handler ->
      handler.init(apiRouter)
    );

    router
      .route("/api/*")
      .subRouter(apiRouter);

    webHandlers.forEach(handler -> {
      handler.init(router);
    });

    return vertx.createHttpServer()
      .requestHandler(router)
      .listen(Config.getInt("server.port"));
  }
}
