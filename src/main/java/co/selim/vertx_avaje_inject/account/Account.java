package co.selim.vertx_avaje_inject.account;

import io.avaje.jsonb.Json;

@Json
public record Account(long id, String name) {
}
