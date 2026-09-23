package ee.allar.bank.transaction;


import ee.allar.bank.account.AccountService;
import ee.allar.bank.account.Balance;
import ee.allar.bank.account.BalanceService;
import ee.allar.bank.common.Currency;
import ee.allar.bank.transaction.dto.TransactionResponse;
import ee.allar.bank.transaction.dto.CreateTransactionRequest;
import ee.allar.bank.common.exception.InsufficientFundsException;
import ee.allar.bank.common.exception.InvalidCurrencyException;
import ee.allar.bank.outbox.OutboxService;
import ee.allar.bank.transaction.mapper.TransactionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountService accountService;
    private final BalanceService balanceService;
    private final OutboxService outboxService;
    private final TransactionMapper transactionMapper;

    @Transactional
    public TransactionResponse createTransaction(UUID accountId, CreateTransactionRequest transaction) {
        log.debug("Transaction request: {}", transaction);

        Currency currency = transaction.currency();
        Direction direction = transaction.direction();
        BigDecimal amount = transaction.amount();
        String description = transaction.description();
        accountService.findByIdOrThrow(accountId);
        Balance balance = balanceService.findForUpdate(accountId, currency);
        if (balance == null) {
            throw new InvalidCurrencyException("No balance with currency " + currency);
        }
        return makeTransaction(accountId, balance, amount, currency, direction, description);
    }


    public List<TransactionResponse> getTransactions(UUID accountId) {
        accountService.findByIdOrThrow(accountId);
        List<Transaction> transactions = transactionMapper.findAllByAccountId(accountId);

        return transactions.stream()
                .map(TransactionResponse::from)
                .toList();
    }

    private TransactionResponse makeTransaction(UUID accountId, Balance balance, BigDecimal amount, Currency currency, Direction direction, String description) {
        BigDecimal balanceAfter = calculateBalanceAfter(balance, amount, direction);

        Transaction transaction = Transaction.builder()
                .accountId(accountId)
                .balanceId(balance.getId())
                .amount(amount)
                .currency(currency)
                .direction(direction)
                .description(description)
                .balanceAfter(balanceAfter)
                .build();

        balance.setBalance(balanceAfter);
        balanceService.updateBalanceAmount(balance);
        transactionMapper.insert(transaction);
        outboxService.recordTransactionCreated(transaction);
        return TransactionResponse.from(transaction);
    }

    private BigDecimal calculateBalanceAfter(Balance balance, BigDecimal amount, Direction direction) {
        if (Direction.IN.equals(direction)) {
            return balance.getBalance().add(amount);
        }
        BigDecimal balanceAfter = balance.getBalance().subtract(amount);

        if (balance.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }
        return balanceAfter;
    }
}



