package co.selim.vertx_avaje_inject.account;

import co.selim.vertx_avaje_inject.web.HttpHandler;
import io.avaje.validation.Validator;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import static co.selim.vertx_avaje_inject.web.RoutingContexts.getBodyAsPojo;
import static co.selim.vertx_avaje_inject.web.RoutingContexts.respondWithJson;

@Singleton
public class AccountHandler implements HttpHandler.ApiHandler {

  private static final Validator validator = Validator.instance();
  private final AccountRepository accountRepository;

  @Inject
  public AccountHandler(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Override
  public void init(Router router) {
    router.get("/users")
      .handler(this::getAll);
    router.get("/users/:id")
      .handler(this::getById);
    router.post("/users")
      .handler(BodyHandler.create(false))
      .handler(this::save);
  }

  private void getAll(RoutingContext routingContext) {
    respondWithJson(routingContext, accountRepository.findAll());
  }

  private void getById(RoutingContext routingContext) {
    long id = Long.parseLong(routingContext.pathParam("id"));
    accountRepository.findById(id)
      .ifPresentOrElse(
        p -> respondWithJson(routingContext, p),
        () -> routingContext.response().setStatusCode(404).end()
      );
  }

  private void save(RoutingContext routingContext) {
    NewAccount newAccount = getBodyAsPojo(routingContext, NewAccount.class);
    validator.validate(newAccount);
    Account account = accountRepository.save(newAccount);
    routingContext.response().putHeader(HttpHeaders.LOCATION, "/users/" + account.id());
    respondWithJson(routingContext, 201, account);
    routingContext.vertx().eventBus().send("account.created", account.id());
  }
}
