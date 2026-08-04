package hoge.exp.jose.server.repository;

import hoge.exp.jose.server.model.Account;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class AccountRepository {

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    public void save(Account account) {
        accounts.put(account.getAccountId(), account);
    }

    public Optional<Account> findById(String accountId) {
        return Optional.ofNullable(accounts.get(accountId));
    }

    public List<Account> findAll() {
        return new ArrayList<>(accounts.values());
    }
}
