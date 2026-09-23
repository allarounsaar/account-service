package ee.allar.bank.account;


import ee.allar.bank.account.dto.CreateAccountRequest;
import ee.allar.bank.account.mapper.AccountMapper;
import ee.allar.bank.account.dto.AccountResponse;
import ee.allar.bank.common.exception.AccountNotFoundException;
import ee.allar.bank.outbox.OutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final BalanceService balanceService;
    private final OutboxService outboxService;
    private final AccountMapper accountMapper;

    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {
        UUID customerId = request.customerId();
        String country = request.country();

        Account account = new Account(customerId, country);
        accountMapper.insert(account);
        outboxService.recordAccountCreated(account);

        List<Balance> balances = balanceService.createBalances(account.getId(), request.currencies());
        return AccountResponse.from(account, balances);
    };

    public Account findByIdOrThrow(UUID accountId) {
        Account account = accountMapper.findById(accountId);
        if (account == null) {
            throw new AccountNotFoundException(accountId);
        }
        return account;
    }

    public AccountResponse getAccountResponse(UUID accountId) {
        Account account = findByIdOrThrow(accountId);
        List<Balance> balances = balanceService.findAllByAccountId(accountId);
        return AccountResponse.from(account, balances);
    }
}
