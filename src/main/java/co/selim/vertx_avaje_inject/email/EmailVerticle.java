package co.selim.vertx_avaje_inject.email;

import co.selim.vertx_avaje_inject.account.Account;
import co.selim.vertx_avaje_inject.account.AccountRepository;
import io.avaje.inject.Prototype;
import io.vertx.core.Future;
import io.vertx.core.VerticleBase;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Prototype
public class EmailVerticle extends VerticleBase {

  private static final Logger LOG = LoggerFactory.getLogger(EmailVerticle.class);
  private final AccountRepository accountRepository;

  @Inject
  public EmailVerticle(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Override
  public Future<?> start() {
    vertx.eventBus()
      .<Long>localConsumer("account.created", message -> {
        long id = message.body();
        Account account = accountRepository.findById(id)
          .orElseThrow(() -> new IllegalStateException("Account with id " + id + " not found"));
        LOG.info("Sending welcome message to new user {} with id {}", account.name(), account.id());
      });

    LOG.info("Listening for email events");
    return Future.succeededFuture();
  }
}
