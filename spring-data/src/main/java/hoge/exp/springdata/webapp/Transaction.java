package hoge.exp.springdata.webapp;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@EqualsAndHashCode
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

    @Override
    public String toString() {
        DecimalFormat format = new DecimalFormat(Constants.DECIMAL_FORMAT);
        return String.format("Transaction [id=%s, account=%s, date=%s, amount=%s, runningBalance=%s]",
                id, account, date,
                format.format(amount), format.format(runningBalance));
    }
}
