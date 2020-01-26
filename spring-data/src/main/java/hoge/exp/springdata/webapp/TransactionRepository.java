package hoge.exp.springdata.webapp;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Transactional(readOnly = true)
    List<Transaction> findByAccount(long account, Sort sort);

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE transaction", nativeQuery = true)
    void truncate();

//    @Modifying
    @Transactional
    @Query(value = "SELECT setval('transaction_id_seq', 100)", nativeQuery = true)
    void resetTransactionIdSequence();
}
