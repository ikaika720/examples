package hoge.exp.springdata.webapp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Account {
    @Id
    private long id;

    private BigDecimal balance;

    @Column(name = "lastupdated")
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
}
