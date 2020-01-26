package hoge.exp.springdata.webapp;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
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
