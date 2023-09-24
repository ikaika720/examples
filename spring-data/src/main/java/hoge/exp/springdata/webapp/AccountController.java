package hoge.exp.springdata.webapp;

import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AccountController {
    @Autowired
    AccountRepository ar;

    @Autowired
    TransactionRepository tr;

    @RequestMapping(value = "/account/new", produces = TEXT_PLAIN_VALUE)
    @ResponseBody
    public ResponseEntity<String> createAccount(
            @RequestParam long id, @RequestParam String balance) {
        Account act = ar.save(new Account(id, getDecimalValue(balance)));

        return ResponseEntity.status(HttpStatus.CREATED).body(act.toString());
    }

    @RequestMapping(value = "/account/list", produces = TEXT_PLAIN_VALUE)
    @ResponseBody
    public ResponseEntity<String> getAllAccounts() {
        StringBuilder sb = new StringBuilder();

        ar.findAll(Sort.by(Sort.Order.asc("id"))).forEach(a -> {
            sb.append(a.toString());
            sb.append("\r\n");
        });

        return ResponseEntity.ok(sb.toString());
    }

    @RequestMapping(value = "/account/{id}", produces = TEXT_PLAIN_VALUE)
    @ResponseBody
    public ResponseEntity<String> getAccount(@PathVariable("id") long id) {
        Account account = ar.findById(id).get();

        if (account == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }

        return ResponseEntity.ok(account.toString());
    }

    @RequestMapping(value = "/transaction/list", produces = TEXT_PLAIN_VALUE)
    @ResponseBody
    public ResponseEntity<String> getAllTransactions() {
        StringBuilder sb = new StringBuilder();

        tr.findAll(Sort.by(Sort.Order.asc("account"), Sort.Order.asc("id"))).forEach(t -> {
            sb.append(t.toString());
            sb.append("\r\n");
        });

        return ResponseEntity.ok(sb.toString());
    }

    @RequestMapping(value = "/account/{id}/transaction", produces = TEXT_PLAIN_VALUE)
    @ResponseBody
    public ResponseEntity<String> getTransactions(@PathVariable("id") long id) {
        List<Transaction> transactions = tr.findByAccount(id, Sort.by(Direction.ASC, "id"));

        if (transactions == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }

        StringBuilder sb = new StringBuilder();

        transactions.forEach(t -> {
            sb.append(t.toString());
            sb.append("\r\n");
        });

        return ResponseEntity.ok(sb.toString());
    }

    @RequestMapping(value = "/account/totalBalance", produces = TEXT_PLAIN_VALUE)
    @ResponseBody
    public ResponseEntity<String> getTotalBalance() {
        BigDecimal totalBalance = ar.getTotalBalance();

        return ResponseEntity.ok(totalBalance.toString());
    }

    @RequestMapping(value = "/account/transfer", produces = TEXT_PLAIN_VALUE)
    @ResponseBody
    public String transfer(@RequestParam long accountFrom,
            @RequestParam long accountTo, @RequestParam String amount,
            @RequestParam long sleep) {
        ar.transfer(accountFrom, accountTo, getDecimalValue(amount), sleep);

        return "Completed successfully.";
    }

    @RequestMapping(value = "/clear", produces = TEXT_PLAIN_VALUE)
    @ResponseBody
    public String clear() {
        tr.truncate();
        tr.resetTransactionIdSequence();
        ar.truncate();

        return "Completed successfully.";
    }

    public BigDecimal getDecimalValue(String value) {
        return new BigDecimal(value).setScale(2, RoundingMode.DOWN);
    }

}
