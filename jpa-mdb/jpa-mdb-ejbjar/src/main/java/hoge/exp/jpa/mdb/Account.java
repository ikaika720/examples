package hoge.exp.jpa.mdb;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDateTime;
import java.util.Locale;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@Entity
@NamedQueries ({
    @NamedQuery(name = "Account.findAll", query = "SELECT a FROM Account a ORDER BY a.id"),
    @NamedQuery(name = "Account.getTotalBalance", query = "SELECT SUM(a.balance) FROM Account a")
})
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

    public Account addBalance(String amount) {
        balance = balance.add(new BigDecimal(amount));
        return this;
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
        return String.format(Locale.US, "{\"id\"=%s,\"balance\"=%s,\"lastUpdated\"=\"%tFT%<tT.%<tL\"}",
                id, new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(Locale.US)).format(balance), lastUpdated);
    }
}
