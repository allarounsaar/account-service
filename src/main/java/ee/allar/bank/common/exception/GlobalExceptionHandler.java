package ee.allar.bank.common.exception;

import ee.allar.bank.common.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFound(AccountNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("ACCOUNT_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFunds(InsufficientFundsException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("INSUFFICIENT_FUNDS", ex.getMessage()));
    }

    @ExceptionHandler(InvalidAmountException.class)
    public ResponseEntity<ErrorResponse> handleInvalidAmount(InvalidAmountException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("INVALID_AMOUNT", ex.getMessage()));
    }

    @ExceptionHandler(InvalidCountryException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCountry(InvalidCountryException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("INVALID_COUNTRY", ex.getMessage()));
    }

    @ExceptionHandler(InvalidCurrencyException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCurrency(InvalidCurrencyException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("INVALID_CURRENCY", ex.getMessage()));
    }

    @ExceptionHandler(InvalidDirectionException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDirection(InvalidDirectionException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("INVALID_DIRECTION", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<ErrorResponse.ValidationError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ErrorResponse.ValidationError(
                        fieldError.getField(),
                        fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Validation error"))
                .toList();
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("VALIDATION_ERROR", "Validation failed", errors));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Throwable mostSpecificCause = ex.getMostSpecificCause();
        if (mostSpecificCause instanceof InvalidAmountException e) {
            return handleInvalidAmount(e);
        }
        if (mostSpecificCause instanceof InvalidCountryException e) {
            return handleInvalidCountry(e);
        }
        if (mostSpecificCause instanceof InvalidCurrencyException e) {
            return handleInvalidCurrency(e);
        }
        if (mostSpecificCause instanceof InvalidDirectionException e) {
            return handleInvalidDirection(e);
        }
        String message = mostSpecificCause.getMessage() != null ? mostSpecificCause.getMessage() : "Malformed JSON request";
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("MALFORMED_JSON", message));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Invalid value '%s' for parameter '%s'", ex.getValue(), ex.getName());
        return ResponseEntity.badRequest()
                .body(new ErrorResponse("INVALID_ARGUMENT", message));
    }
}
