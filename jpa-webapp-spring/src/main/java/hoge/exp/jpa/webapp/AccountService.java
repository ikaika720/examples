package hoge.exp.jpa.webapp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {
    @PersistenceContext(unitName = "bankPU")
    private EntityManager em;

    @Transactional
    public void transfer(long accountFrom, long accountTo, BigDecimal amount) {
        Date date = new Date();

        long actNum1;
        long actNum2;
        BigDecimal amt1;
        BigDecimal amt2;

        if (accountFrom < accountTo) {
            actNum1 = accountFrom;
            actNum2 = accountTo;
            amt1 = amount.negate();
            amt2 = amount;
        } else {
            actNum1 = accountTo;
            actNum2 = accountFrom;
            amt1 = amount;
            amt2 = amount.negate();
        }

        Account act1 = em.find(Account.class, actNum1, LockModeType.PESSIMISTIC_WRITE);
        Account act2 = em.find(Account.class, actNum2, LockModeType.PESSIMISTIC_WRITE);

        act1.setBalance(act1.getBalance().add(amt1));
        Transaction trn1 = new Transaction(actNum1, date, amt1, act1.getBalance());

        act2.setBalance(act2.getBalance().add(amt2));
        Transaction trn2 = new Transaction(actNum2, date, amt2, act2.getBalance());

        em.persist(trn1);
        em.persist(trn2);
    }

    @Transactional
    public void transfer(long accountFrom, long accountTo, BigDecimal amount, long sleep) {
        transfer(accountFrom, accountTo, amount);
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public Account createAccount(Account account) {
        em.persist(account);
        return account;
    }

    @Transactional
    public void truncateAllTables() {
        em.createNativeQuery("TRUNCATE transaction").executeUpdate();
        em.createNativeQuery("TRUNCATE account CASCADE").executeUpdate();
    }

    @Transactional(readOnly = true)
    public List<Account> getAllAccounts() {
        return em.createNamedQuery("Account.findAll", Account.class).getResultList();
    }

    @Transactional(readOnly = true)
    public Account getAccount(long id) {
        return em.find(Account.class, id);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions() {
        return em.createNamedQuery("Transaction.findAll", Transaction.class).getResultList();
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByAccountId(long accountId) {
        return em.createNamedQuery(
                "Transaction.getTransactionsByAccountId", Transaction.class)
                .setParameter("accountId", accountId)
                .getResultList();
    }

    public BigDecimal getDecimalValue(String value) {
        return new BigDecimal(value).setScale(2, RoundingMode.DOWN);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalBalance() {
        BigDecimal totalBalance = em.createNamedQuery(
                "Account.getTotalBalance", BigDecimal.class).getSingleResult();
        if (totalBalance == null) {
            return BigDecimal.valueOf(0, 2);
        } else {
            return totalBalance;
        }
    }
}
