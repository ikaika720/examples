package hoge.exp.jpa.webapp;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NamedQueries({
    @NamedQuery(name = "Transaction.findAll", query = "SELECT t FROM Transaction t ORDER BY t.account, t.id"),
    @NamedQuery(name = "Transaction.getTransactionsByAccountId", query = "SELECT t FROM Transaction t WHERE t.account = :accountId ORDER BY t.id")
})
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
    @Temporal(TemporalType.DATE)
    @Setter
    @Getter
    private Date date;
    @Setter
    @Getter
    private BigDecimal amount;
    @Column(name = "running_balance")
    @Setter
    @Getter
    private BigDecimal runningBalance;

    public Transaction(long account, Date date, BigDecimal amount, BigDecimal runningBalance) {
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
