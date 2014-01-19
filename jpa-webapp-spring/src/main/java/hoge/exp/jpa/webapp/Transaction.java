package hoge.exp.jpa.webapp;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@NamedQuery(name = "Transaction.findAll", query = "SELECT t FROM Transaction t ORDER BY t.account, t.id")
public class Transaction {
    @Id
    @SequenceGenerator(name = "transactionIdSeq", sequenceName = "transaction_id_seq",
        allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transactionIdSeq")
    private long id;
    private long account;
    @Temporal(TemporalType.DATE)
    private Date date;
    private BigDecimal amount;
    @Column(name = "running_balance")
    private BigDecimal runningBalance;

    public Transaction() {}

    public Transaction(long account, Date date, BigDecimal amount, BigDecimal runningBalance) {
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
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
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
        DecimalFormat format = new DecimalFormat(Constants.DECIMAL_CORMAT);
        return String.format("Transaction [id=%s, account=%s, date=%s, amount=%s, runningBalance=%s]",
                id, account, date,
                format.format(amount), format.format(runningBalance));
    }
}
