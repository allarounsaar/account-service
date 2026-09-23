package ee.allar.bank.account;


import ee.allar.bank.common.Currency;
import ee.allar.bank.account.mapper.BalanceMapper;
import ee.allar.bank.outbox.OutboxService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BalanceService {
    private final BalanceMapper balanceMapper;
    private final OutboxService outboxService;

    @Transactional
    public List<Balance> createBalances(UUID accountId, Set<Currency> currencies) {
        List<Balance> balances = mapCurrenciesToBalances(accountId, currencies);
        for (Balance balance : balances) {
            balanceMapper.insert(balance);
            outboxService.recordBalanceCreated(balance);
        }
        return balances;
    }

    @Transactional
    public void updateBalanceAmount(Balance balance) {
        balanceMapper.updateBalanceAmount(balance);
        outboxService.recordBalanceUpdated(balance);
    }

    @Transactional
    public Balance findForUpdate(UUID accountId, Currency currency) {
        return balanceMapper.findForUpdate(accountId, currency);
    }

    public List<Balance> findAllByAccountId(UUID accountId) {
        return balanceMapper.findAllByAccountId(accountId);
    }

    private List<Balance> mapCurrenciesToBalances(UUID accountId, Set<Currency> currencies) {
        return currencies.stream()
                .map(currency -> new Balance(accountId, currency))
                .toList();
    }
}
