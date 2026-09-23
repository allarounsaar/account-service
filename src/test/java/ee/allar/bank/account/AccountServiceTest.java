package ee.allar.bank.account;

import ee.allar.bank.account.dto.AccountResponse;
import ee.allar.bank.account.dto.CreateAccountRequest;
import ee.allar.bank.account.mapper.AccountMapper;
import ee.allar.bank.common.Currency;
import ee.allar.bank.common.MoneyUtils;
import ee.allar.bank.common.exception.AccountNotFoundException;
import ee.allar.bank.outbox.OutboxService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private BalanceService balanceService;
    @Mock
    private OutboxService outboxService;
    @Mock
    private AccountMapper accountMapper;
    @Captor
    private ArgumentCaptor<Account> accountCaptor;

    @InjectMocks
    private AccountService service;

    private static final UUID ACCOUNT_ID = UUID.fromString("01a0c35e-e723-767d-9265-60d0d5a07bf7");
    private static final UUID CUSTOMER_ID = UUID.fromString("01a0c35f-c90b-7054-b507-06caa681e380");

    @Test
    void createAccount_validRequest_createsNewAccountAndBalances() {
        Set<Currency> currencies = Set.of(Currency.EUR, Currency.USD);
        CreateAccountRequest request = new CreateAccountRequest(CUSTOMER_ID, "EE", currencies);

        AccountResponse response = service.createAccount(request);

        verify(accountMapper, times(1)).insert(accountCaptor.capture());
        Account account = accountCaptor.getValue();
        verify(outboxService, times(1)).recordAccountCreated(account);
        verify(balanceService, times(1)).createBalances(account.getId(), currencies);
        assertAll(
                () -> assertThat(response).isNotNull(),
                () -> assertThat(account).isNotNull(),
                () -> assertThat(account.getCustomerId()).isEqualTo(CUSTOMER_ID),
                () -> assertThat(account.getCountry()).isEqualTo("EE")
        );
    }

    @Test
    void findByIdOrThrow_findsAccount_returnsAccount() {
        doReturn(new Account()).when(accountMapper).findById(any());
        assertDoesNotThrow(() -> service.findByIdOrThrow(ACCOUNT_ID));
    }

    @Test
    void findByIdOrThrow_noAccountFound_throwsException() {
        doReturn(null).when(accountMapper).findById(any());
        assertThrows((AccountNotFoundException.class), () -> service.findByIdOrThrow(ACCOUNT_ID));
    }

    @Test
    void getAccountResponse_hasAccount_returnsAccountResponse() {
        doReturn(mockAccount()).when(accountMapper).findById(any(UUID.class));
        doReturn(List.of(mockBalance())).when(balanceService).findAllByAccountId(any(UUID.class));

        AccountResponse response = service.getAccountResponse(ACCOUNT_ID);

        assertThat(response).isNotNull();
        verify(accountMapper, times(1)).findById(ACCOUNT_ID);
        verify(balanceService, times(1)).findAllByAccountId(ACCOUNT_ID);
    }

    @Test
    void getAccountResponse_noAccountFound_throwsException() {
        doReturn(null).when(accountMapper).findById(any(UUID.class));

        assertThrows((AccountNotFoundException.class), () -> service.getAccountResponse(ACCOUNT_ID));

        verify(accountMapper, times(1)).findById(ACCOUNT_ID);
        verifyNoInteractions(balanceService);
    }

    private Account mockAccount() {
        return Account.builder()
                .id(ACCOUNT_ID)
                .customerId(CUSTOMER_ID)
                .build();
    }

    private Balance mockBalance() {
        return Balance.builder()
                .balance(MoneyUtils.toTwoDecimals(BigDecimal.ZERO))
                .currency(Currency.EUR)
                .build();
    }
}
