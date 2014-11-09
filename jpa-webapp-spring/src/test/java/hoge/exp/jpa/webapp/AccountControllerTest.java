package hoge.exp.jpa.webapp;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.http.ResponseEntity;

public class AccountControllerTest {

    @Test
    public void testGetAllAccounts() {
        AccountController ac = new AccountController();
        AccountService as = mock(AccountService.class);

        List<Account> accounts = new ArrayList<Account>();
        Account account0 = new Account();
        account0.setBalance(BigDecimal.valueOf(50, 2));
        account0.setId(123L);
        account0.setLastUpdated(new Date(0L));
        accounts.add(account0);

        Account account1 = new Account();
        account1.setBalance(BigDecimal.valueOf(100, 2));
        account1.setId(456L);
        account1.setLastUpdated(new Date(86400000L));
        accounts.add(account1);

        when(as.getAllAccounts()).thenReturn(accounts);
        ac.as = as;

        ResponseEntity<String> response = ac.getAllAccounts();
        assertTrue(response.getStatusCode().is2xxSuccessful());
        String body = response.getBody();
        System.out.println(body);
        String[] lines = body.split("\r\n");
        assertTrue(lines[0].contains("123"));
        assertTrue(lines[0].contains("0.50"));
        assertTrue(lines[1].contains("456"));
        assertTrue(lines[1].contains("1.00"));
    }

    @Test
    public void testGetAllTransactions() {
        AccountController ac = new AccountController();
        AccountService as = mock(AccountService.class);

        List<Transaction> transactions = new ArrayList<Transaction>();
        Transaction trans0 = new Transaction();
        trans0.setAccount(111L);
        trans0.setAmount(BigDecimal.valueOf(50, 2));
        trans0.setDate(new Date(0L));
        trans0.setId(222L);
        trans0.setRunningBalance(BigDecimal.valueOf(100, 2));
        transactions.add(trans0);

        Transaction trans1 = new Transaction();
        trans1.setAccount(333L);
        trans1.setAmount(BigDecimal.valueOf(150, 2));
        trans1.setDate(new Date(86400000L));
        trans1.setId(444L);
        trans1.setRunningBalance(BigDecimal.valueOf(200, 2));
        transactions.add(trans1);

        when(as.getAllTransactions()).thenReturn(transactions);
        ac.as = as;

        ResponseEntity<String> response = ac.getAllTransactions();
        assertTrue(response.getStatusCode().is2xxSuccessful());
        String body = response.getBody();
        System.out.println(body);
        String[] lines = body.split("\r\n");
        assertTrue(lines[0].contains("111"));
        assertTrue(lines[0].contains("0.50"));
        assertTrue(lines[0].contains("222"));
        assertTrue(lines[0].contains("1.00"));
        assertTrue(lines[1].contains("333"));
        assertTrue(lines[1].contains("1.50"));
        assertTrue(lines[1].contains("444"));
        assertTrue(lines[1].contains("2.00"));
    }

}
