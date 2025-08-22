package co.selim.vertx_avaje_inject.account;

import jakarta.inject.Singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Singleton
public class AccountRepository {

  private final List<Account> accounts = new ArrayList<>(
    List.of(
      new Account(1, "John Doe"),
      new Account(2, "Jane Doe")
    )
  );

  public Optional<Account> findById(long id) {
    return accounts.stream()
      .filter(p -> p.id() == id)
      .findFirst();
  }

  public List<Account> findAll() {
    return new ArrayList<>(accounts);
  }

  public Account save(NewAccount newAccount) {
    long maxId = accounts.stream()
      .map(Account::id)
      .max(Long::compare)
      .orElse(0L);

    Account account = new Account(maxId + 1, newAccount.name());
    accounts.add(account);
    return account;
  }
}
