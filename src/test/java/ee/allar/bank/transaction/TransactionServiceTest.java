package ee.allar.bank.transaction;

import ee.allar.bank.account.AccountService;
import ee.allar.bank.account.Balance;
import ee.allar.bank.account.BalanceService;
import ee.allar.bank.common.Currency;
import ee.allar.bank.common.exception.InsufficientFundsException;
import ee.allar.bank.common.exception.InvalidCurrencyException;
import ee.allar.bank.outbox.OutboxService;
import ee.allar.bank.transaction.dto.CreateTransactionRequest;
import ee.allar.bank.transaction.dto.TransactionResponse;
import ee.allar.bank.transaction.mapper.TransactionMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    private AccountService accountService;
    @Mock
    private BalanceService balanceService;
    @Mock
    private OutboxService outboxService;
    @Mock
    private TransactionMapper transactionMapper;
    @Captor
    private ArgumentCaptor<Transaction> transactionCaptor;
    @InjectMocks
    private TransactionService service;

    private static final UUID ACCOUNT_ID = UUID.fromString("01a0c35e-e723-767d-9265-60d0d5a07bf7");


    @Test
    void createTransaction_validRequestOutNoBalance_throwsException() {
        assertThrows((InvalidCurrencyException.class), () -> service.createTransaction(ACCOUNT_ID, mockTransactionOUT()));
        verify(accountService, times(1)).findByIdOrThrow(ACCOUNT_ID);
        verify(balanceService, times(1)).findForUpdate(ACCOUNT_ID, Currency.EUR);
    }

    @Test
    void createTransaction_validRequestOutInsufficient_throwsException() {
        doReturn(mockBalanceTo(BigDecimal.ZERO)).when(balanceService).findForUpdate(any(UUID.class), any(Currency.class));

        assertThrows((InsufficientFundsException.class), () -> service.createTransaction(ACCOUNT_ID, mockTransactionOUT()));

        verify(accountService, times(1)).findByIdOrThrow(ACCOUNT_ID);
        verify(balanceService, times(1)).findForUpdate(ACCOUNT_ID, Currency.EUR);
    }

    @Test
    void createTransaction_validRequestOutHasBalance_makesTransaction() {
        Balance balance = mockBalanceTo(BigDecimal.TEN);
        doReturn(balance).when(balanceService).findForUpdate(any(UUID.class), any(Currency.class));

        TransactionResponse response = service.createTransaction(ACCOUNT_ID, mockTransactionOUT());

        verify(accountService, times(1)).findByIdOrThrow(ACCOUNT_ID);
        verify(balanceService, times(1)).findForUpdate(ACCOUNT_ID, Currency.EUR);
        verify(balanceService, times(1)).updateBalanceAmount(balance);
        verify(transactionMapper, times(1)).insert(transactionCaptor.capture());
        verify(outboxService, times(1)).recordTransactionCreated(transactionCaptor.getValue());

        assertThat(response).isNotNull();
        assertAll(
                () -> assertThat(response.accountId()).isEqualTo(ACCOUNT_ID),
                () -> assertThat(response.currency()).isEqualTo(Currency.EUR),
                () -> assertThat(response.direction()).isEqualTo(Direction.OUT),
                () -> assertThat(response.description()).isEqualTo("description"),
                () -> assertThat(response.balanceAfter()).isEqualTo(BigDecimal.ZERO)
        );
    }

    @Test
    void createTransaction_validRequestInZeroBalance_makesTransaction() {
        CreateTransactionRequest requestIn = new CreateTransactionRequest(BigDecimal.TEN, Currency.EUR, Direction.IN, "description");
        Balance balance = mockBalanceTo(BigDecimal.ZERO);
        doReturn(balance).when(balanceService).findForUpdate(any(UUID.class), any(Currency.class));

        TransactionResponse response = service.createTransaction(ACCOUNT_ID, requestIn);

        verify(accountService, times(1)).findByIdOrThrow(ACCOUNT_ID);
        verify(balanceService, times(1)).findForUpdate(ACCOUNT_ID, Currency.EUR);
        verify(balanceService, times(1)).updateBalanceAmount(balance);
        verify(transactionMapper, times(1)).insert(transactionCaptor.capture());
        verify(outboxService, times(1)).recordTransactionCreated(transactionCaptor.getValue());

        assertThat(response).isNotNull();
        assertAll(
                () -> assertThat(response.accountId()).isEqualTo(ACCOUNT_ID),
                () -> assertThat(response.currency()).isEqualTo(Currency.EUR),
                () -> assertThat(response.direction()).isEqualTo(Direction.IN),
                () -> assertThat(response.description()).isEqualTo("description"),
                () -> assertThat(response.balanceAfter()).isEqualTo(BigDecimal.TEN)
        );
    }


    @Test
    void getTransactions_hasTransactions_returnsTransactionsList() {
        List<Transaction> transactions = List.of(new Transaction(), new Transaction());
        doReturn(transactions).when(transactionMapper).findAllByAccountId(any(UUID.class));

        List<TransactionResponse> response = service.getTransactions(ACCOUNT_ID);

        assertThat(response).hasSize(2);
        verify(accountService, times(1)).findByIdOrThrow(ACCOUNT_ID);
        verify(transactionMapper, times(1)).findAllByAccountId(ACCOUNT_ID);
    }

    @Test
    void getTransactions_noTransactions_returnsTransactionsEmptyList() {
        doReturn(List.of()).when(transactionMapper).findAllByAccountId(any(UUID.class));

        List<TransactionResponse> response = service.getTransactions(ACCOUNT_ID);

        assertThat(response).isEmpty();
        verify(accountService, times(1)).findByIdOrThrow(ACCOUNT_ID);
        verify(transactionMapper, times(1)).findAllByAccountId(ACCOUNT_ID);
    }

    private CreateTransactionRequest mockTransactionOUT() {
        return new CreateTransactionRequest(BigDecimal.TEN, Currency.EUR, Direction.OUT, "description");
    }

    private Balance mockBalanceTo(BigDecimal amount) {
        return Balance.builder()
                .balance(amount)
                .currency(Currency.EUR)
                .build();
    }
}