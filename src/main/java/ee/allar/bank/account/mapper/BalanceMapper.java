package ee.allar.bank.account.mapper;

import ee.allar.bank.account.Balance;
import ee.allar.bank.common.Currency;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface BalanceMapper {
    void insert(Balance balance);

    int updateBalanceAmount(Balance balance);

    Balance findForUpdate(@Param("accountId") UUID accountId, @Param("currency") Currency currency);

    List<Balance> findAllByAccountId(UUID accountId);
}
