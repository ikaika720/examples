package hoge.exp.springdata.webapp;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AccountController {
    private final AccountRepository ar;
    private final TransactionRepository tr;

    @PostMapping(value = "/account/new", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Account> createAccount(
            @RequestParam("id") long id, @RequestParam("balance") String balance) {
        var act = ar.save(new Account(id, getDecimalValue(balance)));
        return ResponseEntity.status(HttpStatus.CREATED).body(act);
    }

    @GetMapping(value = "/account/list", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Account>> getAllAccounts() {
        var result = ar.findAll(Sort.by(Sort.Order.asc("id")));
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/account/{id}", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Account> getAccount(@PathVariable("id") long id) {
        return ar.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/transaction/list", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        var result = tr.findAll(Sort.by(Sort.Order.asc("account"), Sort.Order.asc("id")));
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/account/{id}/transaction", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Transaction>> getTransactions(@PathVariable("id") long id) {
        var transactions = tr.findByAccount(id, Sort.by(Sort.Direction.ASC, "id"));
        
        if (transactions.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(transactions);
    }

    @GetMapping(value = "/account/totalBalance", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, BigDecimal>> getTotalBalance() {
        BigDecimal totalBalance = ar.getTotalBalance();
        return ResponseEntity.ok(Map.of("totalBalance", totalBalance));
    }

    @PostMapping(value = "/account/transfer", produces = APPLICATION_JSON_VALUE)
    public Map<String, String> transfer(@RequestParam("accountFrom") long accountFrom,
            @RequestParam("accountTo") long accountTo, @RequestParam("amount") String amount,
            @RequestParam("sleep") long sleep) {
        ar.transfer(accountFrom, accountTo, getDecimalValue(amount), sleep);
        return Map.of("message", "Completed successfully.");
    }

    @PostMapping(value = "/clear", produces = APPLICATION_JSON_VALUE)
    public Map<String, String> clear() {
        tr.truncate();
        tr.resetTransactionIdSequence();
        ar.truncate();
        return Map.of("message", "Completed successfully.");
    }

    private BigDecimal getDecimalValue(String value) {
        return new BigDecimal(value).setScale(2, RoundingMode.DOWN);
    }
}
