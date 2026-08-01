package hoge.exp.jpa.webapp;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NamedQueries ({
    @NamedQuery(name = "Account.findAll", query = "SELECT a FROM Account a ORDER BY a.id"),
    @NamedQuery(name = "Account.getTotalBalance", query = "SELECT SUM(a.balance) FROM Account a")
})

@NoArgsConstructor
@EqualsAndHashCode
public class Account {
    @Id
    @Setter
    @Getter
    private long id;
    @Setter
    @Getter
    private BigDecimal balance;
    @Setter
    @Getter
    private LocalDateTime lastUpdated;

    public Account(long id, BigDecimal balance) {
        this.id = id;
        this.balance = balance;
    }

    @PrePersist
    @PreUpdate
    private void currentTimestamp() {
        lastUpdated = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format("Account [id=%s, balance=%s, lastUpdated=%s]",
                id, new DecimalFormat(Constants.DECIMAL_FORMAT).format(balance), lastUpdated);
    }
}
