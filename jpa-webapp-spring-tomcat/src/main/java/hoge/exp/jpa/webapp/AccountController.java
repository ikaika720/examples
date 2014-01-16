package hoge.exp.jpa.webapp;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
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
    		@RequestParam long id, @RequestParam BigDecimal balance) {
    	Account act = as.createAccount(new Account(id, balance));

    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.TEXT_PLAIN);
    	return new ResponseEntity<String>(act.toString(), headers, HttpStatus.CREATED);
    }

    @RequestMapping(value = "/account/list")
    @ResponseBody
    public ResponseEntity<String> getAllAccounts() {
    	StringBuilder sb = new StringBuilder();    	
    	for (Account a : as.getAllAccounts()) {
    		sb.append(a.toString());
    		sb.append("\r\n");
    	}

    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.TEXT_PLAIN);
    	return new ResponseEntity<String>(sb.toString(), headers, HttpStatus.OK);
    }

    @RequestMapping("/transaction/list")
    @ResponseBody
    public ResponseEntity<String> getAllTransactions() {
        StringBuilder sb = new StringBuilder();
    	for (Transaction t : as.getAllTransactions()) {
    		sb.append(t.toString());
    		sb.append("\r\n");
    	}

    	HttpHeaders headers = new HttpHeaders();
    	headers.setContentType(MediaType.TEXT_PLAIN);
    	return new ResponseEntity<String>(sb.toString(), headers, HttpStatus.OK);
    }

    @RequestMapping("/account/transfer")
    @ResponseBody
    public String transfer(@RequestParam long accountFrom,
            @RequestParam long accountTo, @RequestParam BigDecimal amount,
            @RequestParam long sleep) {
        as.transfer(accountFrom, accountTo, amount, sleep);

        return "Completed successfully.";
    }

    @RequestMapping("/clear")
    @ResponseBody
    public String clear() {
        as.truncateAllTables();

        return "Completed successfully.";
    }
}
