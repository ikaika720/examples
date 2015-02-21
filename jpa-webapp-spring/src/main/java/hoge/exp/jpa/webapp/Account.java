package hoge.exp.jpa.webapp;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@NamedQueries ({
    @NamedQuery(name = "Account.findAll", query = "SELECT a FROM Account a ORDER BY a.id"),
    @NamedQuery(name = "Account.getTotalBalance", query = "SELECT SUM(a.balance) FROM Account a")
})
public class Account {
    @Id
    private long id;
    private BigDecimal balance;
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;

    public Account() {}

    public Account(long id, BigDecimal balance) {
        this.id = id;
        this.balance = balance;
    }

    @PrePersist
    @PreUpdate
    private void currentTimestamp() {
        lastUpdated = new Date();
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public BigDecimal getBalance() {
        return balance;
    }
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    public Date getLastUpdated() {
        return lastUpdated;
    }
    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return String.format("Account [id=%s, balance=%s, lastUpdated=%s]",
                id, new DecimalFormat(Constants.DECIMAL_FORMAT).format(balance), lastUpdated);
    }
}
