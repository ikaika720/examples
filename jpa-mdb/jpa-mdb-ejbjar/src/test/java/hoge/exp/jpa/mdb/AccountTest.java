package hoge.exp.jpa.mdb;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountTest {

    @Test
    void testAddBalance() {
        Account account = new Account(1L, new BigDecimal("1000.00"));
        account.addBalance("500.00");
        assertEquals(new BigDecimal("1500.00"), account.getBalance());
    }
}
