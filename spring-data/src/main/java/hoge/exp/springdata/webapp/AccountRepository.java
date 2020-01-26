package hoge.exp.springdata.webapp;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

interface AccountRepository extends CrudRepository<Account, Long>, AccountRepositoryCustom {
    @Transactional(readOnly = true)
    @Query("SELECT SUM(a.balance) FROM Account a")
    BigDecimal getTotalBalance();

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE account CASCADE", nativeQuery = true)
    void truncate();
}
