package co.selim.vertx_avaje_inject.api;

import io.vertx.ext.web.Router;

public interface ApiHandler {

  void init(Router router);
}
