package hoge.exp.springdata.webapp;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Transaction {
    @Id
    @SequenceGenerator(name = "transactionIdSeq", sequenceName = "transaction_id_seq",
        allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "transactionIdSeq")
    private long id;
    private long account;
    private LocalDate date;
    private BigDecimal amount;
    @Column(name = "running_balance")
    private BigDecimal runningBalance;

    public Transaction(long account, LocalDate date, BigDecimal amount, BigDecimal runningBalance) {
        this.account = account;
        this.date = date;
        this.amount = amount;
        this.runningBalance = runningBalance;
    }
}
