package hoge.exp.jpa;

import hoge.exp.jpa.webapp.Account;
import hoge.exp.jpa.webapp.AccountService;
import hoge.exp.jpa.webapp.Transaction;

import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class Main {
	private static final Log log = LogFactory.getLog(Main.class);
	
    public static void main(String[] args) {
        FileSystemXmlApplicationContext ctx =
                new FileSystemXmlApplicationContext(
                        "src/main/webapp/WEB-INF/applicationContext.xml");
        try {
            AccountService as = ctx.getBean(AccountService.class);

            as.truncateAllTables();

            as.createAccount(new Account(1L, BigDecimal.valueOf(100L)));
            as.createAccount(new Account(2L, BigDecimal.valueOf(100L)));

            as.transfer(1L, 2L, BigDecimal.valueOf(1234L, 2));
            as.transfer(2L, 1L, BigDecimal.valueOf(5678L, 2));
            // An exception will be thrown, then the transaction will be roll backed.
            try {
                as.transfer(2L, 1L, BigDecimal.valueOf(0L), -1);
            } catch (Exception e) {
            }

            for (Account a : as.getAllAccounts()) {
                log.info(a.toString());
            }

            for (Transaction t : as.getAllTransactions()) {
                log.info(t.toString());
            }

            as.truncateAllTables();
        } finally {
            ctx.close();
        }
    }
}
