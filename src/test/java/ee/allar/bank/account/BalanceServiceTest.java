package ee.allar.bank.account;

import ee.allar.bank.account.mapper.BalanceMapper;
import ee.allar.bank.common.Currency;
import ee.allar.bank.outbox.OutboxService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BalanceServiceTest {

    @Mock
    private BalanceMapper balanceMapper;
    @Mock
    private OutboxService outboxService;
    @InjectMocks
    private BalanceService service;

    private static final UUID ACCOUNT_ID = UUID.fromString("01a0c35e-e723-767d-9265-60d0d5a07bf7");

    @Test
    void createBalances_validCurrencies_createsAndInsertsBalances() {
        Set<Currency> currencies = Set.of(Currency.EUR, Currency.USD);

        List<Balance> result = service.createBalances(ACCOUNT_ID, currencies);

        assertThat(result)
                .hasSize(2)
                .allSatisfy(balance -> assertThat(balance.getAccountId()).isEqualTo(ACCOUNT_ID))
                .extracting(Balance::getCurrency)
                .containsExactlyInAnyOrder(Currency.EUR, Currency.USD);
        verify(balanceMapper, times(2)).insert(any(Balance.class));
        verify(outboxService, times(2)).recordBalanceCreated(any(Balance.class));
    }

    @Test
    void updateBalanceAmount_validBalance_updatesAmountAndRecordsOutbox() {
        Balance balance = mockBalance();
        service.updateBalanceAmount(balance);
        verify(balanceMapper, times(1)).updateBalanceAmount(balance);
        verify(outboxService, times(1)).recordBalanceUpdated(balance);
    }

    @Test
    void findForUpdate_existingBalance_returnsBalance() {
        Balance expected = mockBalance();
        when(balanceMapper.findForUpdate(ACCOUNT_ID, Currency.EUR)).thenReturn(expected);

        Balance result = service.findForUpdate(ACCOUNT_ID, Currency.EUR);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void findAllByAccountId_existingAccount_returnsBalances() {
        when(balanceMapper.findAllByAccountId(ACCOUNT_ID)).thenReturn(List.of(mockBalance()));

        List<Balance> result = service.findAllByAccountId(ACCOUNT_ID);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getAccountId()).isEqualTo(ACCOUNT_ID);
    }

    private Balance mockBalance() {
        return Balance.builder()
                .id(ACCOUNT_ID)
                .accountId(ACCOUNT_ID)
                .currency(Currency.EUR)
                .build();
    }
}