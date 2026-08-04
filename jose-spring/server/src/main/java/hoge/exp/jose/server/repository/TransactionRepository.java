package hoge.exp.jose.server.repository;

import hoge.exp.jose.common.model.Transaction;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class TransactionRepository {

    private final List<Transaction> transactions = Collections.synchronizedList(new ArrayList<>());

    public void save(Transaction transaction) {
        transactions.add(transaction);
    }

    public List<Transaction> findAll() {
        return new ArrayList<>(transactions);
    }

    public List<Transaction> findByAccountId(String accountId) {
        return transactions.stream()
                .filter(tx -> accountId.equals(tx.fromAccount()) || accountId.equals(tx.toAccount()))
                .toList();
    }
}
