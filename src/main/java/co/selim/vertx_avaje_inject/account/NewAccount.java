package co.selim.vertx_avaje_inject.account;

import io.avaje.jsonb.Json;

@Json
public record NewAccount(String name) {
}
