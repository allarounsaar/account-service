package ee.allar.bank.common.exception;

import java.util.UUID;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(UUID accountId) {
        super("No account found with ID: " + accountId);
    }
}
