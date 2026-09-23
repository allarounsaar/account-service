package ee.allar.bank.common.exception;

public class InvalidCountryException extends RuntimeException {
    public InvalidCountryException(String message) {
        super(message);
    }
}
