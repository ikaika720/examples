package hoge.exp.jpa.webapp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@Entity
@NamedQuery(name = "Account.findAll", query = "SELECT a FROM Account a ORDER BY a.id")
public class Account {
    @Id
    private long id;
    private BigDecimal balance;
    private LocalDateTime lastUpdated;

    public Account() {}

    public Account(long id, BigDecimal balance) {
        this.id = id;
        this.balance = balance;
    }

    public Account(long id, BigDecimal balance, LocalDateTime lastUpdated) {
        this.id = id;
        this.balance = balance;
        this.lastUpdated = lastUpdated;
    }

    @PrePersist
    @PreUpdate
    private void currentTimestamp() {
        lastUpdated = LocalDateTime.now();
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
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public String toString() {
        return String.format("Account [id=%s, balance=%s, lastUpdated=%s]",
                id, balance, lastUpdated);
    }
}
