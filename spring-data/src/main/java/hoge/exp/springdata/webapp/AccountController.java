package hoge.exp.springdata.webapp;

import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

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

    @PostMapping(value = "/account/new", produces = TEXT_PLAIN_VALUE)
    public ResponseEntity<String> createAccount(
            @RequestParam("id") long id, @RequestParam("balance") String balance) {
        Account act = ar.save(new Account(id, getDecimalValue(balance)));
        return ResponseEntity.status(HttpStatus.CREATED).body(act.toString());
    }

    @GetMapping(value = "/account/list", produces = TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getAllAccounts() {
        String result = ar.findAll(Sort.by(Sort.Order.asc("id")))
                .stream()
                .map(Account::toString)
                .collect(Collectors.joining("\r\n"));
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/account/{id}", produces = TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getAccount(@PathVariable("id") long id) {
        return ar.findById(id)
                .map(account -> ResponseEntity.ok(account.toString()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/transaction/list", produces = TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getAllTransactions() {
        String result = tr.findAll(Sort.by(Sort.Order.asc("account"), Sort.Order.asc("id")))
                .stream()
                .map(Transaction::toString)
                .collect(Collectors.joining("\r\n"));
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/account/{id}/transaction", produces = TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getTransactions(@PathVariable("id") long id) {
        List<Transaction> transactions = tr.findByAccount(id, Sort.by(Sort.Direction.ASC, "id"));
        
        if (transactions.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        String result = transactions.stream()
                .map(Transaction::toString)
                .collect(Collectors.joining("\r\n"));
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/account/totalBalance", produces = TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getTotalBalance() {
        BigDecimal totalBalance = ar.getTotalBalance();
        return ResponseEntity.ok(totalBalance.toString());
    }

    @PostMapping(value = "/account/transfer", produces = TEXT_PLAIN_VALUE)
    public String transfer(@RequestParam("accountFrom") long accountFrom,
            @RequestParam("accountTo") long accountTo, @RequestParam("amount") String amount,
            @RequestParam("sleep") long sleep) {
        ar.transfer(accountFrom, accountTo, getDecimalValue(amount), sleep);
        return "Completed successfully.";
    }

    @PostMapping(value = "/clear", produces = TEXT_PLAIN_VALUE)
    public String clear() {
        tr.truncate();
        tr.resetTransactionIdSequence();
        ar.truncate();
        return "Completed successfully.";
    }

    private BigDecimal getDecimalValue(String value) {
        return new BigDecimal(value).setScale(2, RoundingMode.DOWN);
    }
}
