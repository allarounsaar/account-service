package ee.allar.bank.transaction.mapper;

import ee.allar.bank.transaction.Transaction;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.UUID;

@Mapper
public interface TransactionMapper {
    void insert(Transaction transaction);

    List<Transaction> findAllByAccountId(UUID accountId);
}
