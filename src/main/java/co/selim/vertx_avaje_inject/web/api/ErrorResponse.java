package co.selim.vertx_avaje_inject.web.api;

import io.avaje.jsonb.Json;

@Json
public record ErrorResponse(
  int status,
  String message
) {
}
