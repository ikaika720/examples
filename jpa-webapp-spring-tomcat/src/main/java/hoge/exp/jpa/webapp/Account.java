package hoge.exp.jpa.webapp;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@NamedQuery(name = "Account.findAll", query = "SELECT a FROM Account a ORDER BY a.id")
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

    public Account(long id, BigDecimal balance, Date lastUpdated) {
        this.id = id;
        this.balance = balance;
        this.lastUpdated = lastUpdated;
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
                id, balance, lastUpdated);
    }
}
