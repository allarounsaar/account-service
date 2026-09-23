package ee.allar.bank.account.mapper;

import ee.allar.bank.account.Account;
import org.apache.ibatis.annotations.Mapper;

import java.util.UUID;

@Mapper
public interface AccountMapper {

    void insert(Account account);

    Account findById(UUID id);
}
