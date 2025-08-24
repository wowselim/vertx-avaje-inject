package co.selim.vertx_avaje_inject.web;

import io.vertx.ext.web.Router;

public interface HttpHandler {

  void init(Router router);

  interface ApiHandler extends HttpHandler {
  }

  interface WebHandler extends HttpHandler {
  }
}
