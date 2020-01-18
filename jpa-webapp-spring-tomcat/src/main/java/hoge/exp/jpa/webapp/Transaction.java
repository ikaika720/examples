package hoge.exp.jpa.webapp;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;

@Entity
@NamedQuery(name = "Transaction.findAll", query = "SELECT t FROM Transaction t ORDER BY t.account, t.id")
public class Transaction {
    @Id
    @SequenceGenerator(name = "transactionIdSeq", sequenceName = "TRANSACTION_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transactionIdSeq")
    private long id;
    private long account;
    private LocalDate date;
    private BigDecimal amount;
    @Column(name = "running_balance")
    private BigDecimal runningBalance;

    public Transaction() {}

    public Transaction(long account, LocalDate date, BigDecimal amount, BigDecimal runningBalance) {
        this.account = account;
        this.date = date;
        this.amount = amount;
        this.runningBalance = runningBalance;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getAccount() {
        return account;
    }
    public void setAccount(long account) {
        this.account = account;
    }
    public LocalDate getDate() {
        return date;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public BigDecimal getRunningBalance() {
        return runningBalance;
    }
    public void setRunningBalance(BigDecimal runningBalance) {
        this.runningBalance = runningBalance;
    }

    @Override
    public String toString() {
        return String.format("Transaction [id=%s, account=%s, date=%s, amount=%s, runningBalance=%s]",
                id, account, date, amount, runningBalance);
    }
}
