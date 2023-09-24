package hoge.exp.springdata.webapp;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Transaction {
    @Id
    @SequenceGenerator(name = "transactionIdSeq", sequenceName = "transaction_id_seq",
        allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transactionIdSeq")
    @Setter
    @Getter
    private long id;
    @Setter
    @Getter
    private long account;
    @Setter
    @Getter
    private LocalDate date;
    @Setter
    @Getter
    private BigDecimal amount;
    @Column(name = "running_balance")
    @Setter
    @Getter
    private BigDecimal runningBalance;

    public Transaction(long account, LocalDate date, BigDecimal amount, BigDecimal runningBalance) {
        this.account = account;
        this.date = date;
        this.amount = amount;
        this.runningBalance = runningBalance;
    }
}
