package hoge.exp.springdata.webapp;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;

import org.springframework.transaction.annotation.Transactional;

public class AccountRepositoryImpl implements AccountRepositoryCustom {
    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void transfer(long accountFrom, long accountTo, BigDecimal amount) {
        LocalDate date = LocalDate.now();

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
        act2.setBalance(act2.getBalance().add(amt2));

        em.persist(new Transaction(actNum1, date, amt1, act1.getBalance()));
        em.persist(new Transaction(actNum2, date, amt2, act2.getBalance()));
    }

    @Override
    @Transactional
    public void transfer(long accountFrom, long accountTo, BigDecimal amount, long sleep) {
        transfer(accountFrom, accountTo, amount);
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
