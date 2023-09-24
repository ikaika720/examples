package hoge.exp.springdata.webapp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@NoArgsConstructor
@EqualsAndHashCode
@ToString
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
