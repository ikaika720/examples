package hoge.exp.jpa.mdb;

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

    public Account addBalance(String amount) {
        balance = balance.add(new BigDecimal(amount));
        return this;
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
        return String.format("{\"id\"=%s,\"balance\"=%s,\"lastUpdated\"=\"%tFT%<tT.%<tL%<tz\"}",
                id, new DecimalFormat("#,##0.00").format(balance), lastUpdated);
    }
}
