package hoge.exp.jpa.webapp;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class AccountController {
    @Autowired
    AccountService as;

    @RequestMapping(value = "/account/new")
    @ResponseBody
    public ResponseEntity<String> createAccount(
            @RequestParam long id, @RequestParam String balance) {
        Account act = as.createAccount(new Account(id, as.getDecimalValue(balance)));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        return new ResponseEntity<String>(act.toString(), headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/account/list")
    @ResponseBody
    public ResponseEntity<String> getAllAccounts() {
        StringBuilder sb = new StringBuilder();
        as.getAllAccounts().forEach(a -> {
            sb.append(a.toString());
            sb.append("\r\n");
        });

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        return new ResponseEntity<String>(sb.toString(), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/account/{id}")
    @ResponseBody
    public ResponseEntity<String> getAccount(@PathVariable("id") long id) {
        Account account = as.getAccount(id);
        if (account == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        return new ResponseEntity<String>(account.toString(), headers, HttpStatus.OK);
    }

    @RequestMapping("/transaction/list")
    @ResponseBody
    public ResponseEntity<String> getAllTransactions() {
        StringBuilder sb = new StringBuilder();
        as.getAllTransactions().forEach(t -> {
            sb.append(t.toString());
            sb.append("\r\n");
        });

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        return new ResponseEntity<String>(sb.toString(), headers, HttpStatus.OK);
    }

    @RequestMapping("/account/{id}/transaction")
    @ResponseBody
    public ResponseEntity<String> getTransactions(@PathVariable("id") long id) {
        List<Transaction> transactions = as.getTransactionsByAccountId(id);
        if (transactions == null) {
            return new ResponseEntity<String>(HttpStatus.NOT_FOUND);
        }

        StringBuilder sb = new StringBuilder();
        transactions.forEach(t -> {
            sb.append(t.toString());
            sb.append("\r\n");
        });

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        return new ResponseEntity<String>(sb.toString(), headers, HttpStatus.OK);
    }

    @RequestMapping("/account/totalBalance")
    @ResponseBody
    public ResponseEntity<String> getTotalBalance() {
        BigDecimal totalBalance = as.getTotalBalance();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        return new ResponseEntity<String>(totalBalance.toString(), headers, HttpStatus.OK);
    }

    @RequestMapping("/account/transfer")
    @ResponseBody
    public String transfer(@RequestParam long accountFrom,
            @RequestParam long accountTo, @RequestParam String amount,
            @RequestParam long sleep) {
        as.transfer(accountFrom, accountTo, as.getDecimalValue(amount), sleep);

        return "Completed successfully.";
    }

    @RequestMapping("/clear")
    @ResponseBody
    public String clear() {
        as.truncateAllTables();

        return "Completed successfully.";
    }
}
