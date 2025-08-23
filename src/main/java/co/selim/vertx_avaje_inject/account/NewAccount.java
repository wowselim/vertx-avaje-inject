package co.selim.vertx_avaje_inject.account;

import io.avaje.jsonb.Json;
import io.avaje.validation.constraints.NotBlank;
import io.avaje.validation.constraints.Size;
import io.avaje.validation.constraints.Valid;

@Json
@Valid
public record NewAccount(
  @NotBlank @Size(min = 1, max = 255) String name
) {
}
