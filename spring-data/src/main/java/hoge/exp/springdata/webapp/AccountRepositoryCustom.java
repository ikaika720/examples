package hoge.exp.springdata.webapp;

import java.math.BigDecimal;

public interface AccountRepositoryCustom {
    void transfer(long accountFrom, long accountTo, BigDecimal amount);

    void transfer(long accountFrom, long accountTo, BigDecimal amount, long sleep);
}
