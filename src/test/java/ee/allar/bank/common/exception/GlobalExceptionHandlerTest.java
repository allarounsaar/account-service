package ee.allar.bank.common.exception;

import ee.allar.bank.common.dto.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleAccountNotFound_returns404NotFound() {
        UUID accountId = UUID.fromString("01a0c35e-e723-767d-9265-60d0d5a07bf7");
        AccountNotFoundException ex = new AccountNotFoundException(accountId);

        ResponseEntity<ErrorResponse> response = handler.handleAccountNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertAll(
                () -> assertThat(response.getBody().getError()).isEqualTo("ACCOUNT_NOT_FOUND"),
                () -> assertThat(response.getBody().getMessage()).isEqualTo("No account found with ID: " + accountId)
        );
    }

    @Test
    void handleInsufficientFunds_returns400BadRequest() {
        InsufficientFundsException ex = new InsufficientFundsException("Insufficient funds");

        ResponseEntity<ErrorResponse> response = handler.handleInsufficientFunds(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertAll(
                () -> assertThat(response.getBody().getError()).isEqualTo("INSUFFICIENT_FUNDS"),
                () -> assertThat(response.getBody().getMessage()).isEqualTo("Insufficient funds")
        );
    }

    @Test
    void handleInvalidAmount_returns400BadRequest() {
        InvalidAmountException ex = new InvalidAmountException("Amount cannot be null");

        ResponseEntity<ErrorResponse> response = handler.handleInvalidAmount(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertAll(
                () -> assertThat(response.getBody().getError()).isEqualTo("INVALID_AMOUNT"),
                () -> assertThat(response.getBody().getMessage()).isEqualTo("Amount cannot be null")
        );
    }

    @Test
    void handleInvalidCountry_returns400BadRequest() {
        InvalidCountryException ex = new InvalidCountryException("Invalid country code");

        ResponseEntity<ErrorResponse> response = handler.handleInvalidCountry(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertAll(
                () -> assertThat(response.getBody().getError()).isEqualTo("INVALID_COUNTRY"),
                () -> assertThat(response.getBody().getMessage()).isEqualTo("Invalid country code")
        );
    }

    @Test
    void handleInvalidCurrency_returns400BadRequest() {
        InvalidCurrencyException ex = new InvalidCurrencyException("Invalid currency");

        ResponseEntity<ErrorResponse> response = handler.handleInvalidCurrency(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertAll(
                () -> assertThat(response.getBody().getError()).isEqualTo("INVALID_CURRENCY"),
                () -> assertThat(response.getBody().getMessage()).isEqualTo("Invalid currency")
        );
    }

    @Test
    void handleInvalidDirection_returns400BadRequest() {
        InvalidDirectionException ex = new InvalidDirectionException("Invalid direction");

        ResponseEntity<ErrorResponse> response = handler.handleInvalidDirection(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertAll(
                () -> assertThat(response.getBody().getError()).isEqualTo("INVALID_DIRECTION"),
                () -> assertThat(response.getBody().getMessage()).isEqualTo("Invalid direction")
        );
    }

    @Test
    void handleValidation_withFieldError_returnsValidationErrors() throws NoSuchMethodException {
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "amount", "Amount is required"));
        bindingResult.addError(new FieldError("target", "description", "Description cannot be blank"));

        MethodParameter parameter = new MethodParameter(
                GlobalExceptionHandlerTest.class.getDeclaredMethod("dummyMethod", String.class), 0);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertAll(
                () -> assertThat(response.getBody().getError()).isEqualTo("VALIDATION_ERROR"),
                () -> assertThat(response.getBody().getMessage()).isEqualTo("Validation failed"),
                () -> assertThat(response.getBody().getErrors()).hasSize(2),
                () -> assertThat(response.getBody().getErrors().get(0).field()).isEqualTo("amount"),
                () -> assertThat(response.getBody().getErrors().get(0).message()).isEqualTo("Amount is required"),
                () -> assertThat(response.getBody().getErrors().get(1).field()).isEqualTo("description"),
                () -> assertThat(response.getBody().getErrors().get(1).message()).isEqualTo("Description cannot be blank")
        );
    }

    @Test
    void handleValidation_withoutFieldError_returnsDefaultMessage() throws NoSuchMethodException {
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");

        MethodParameter parameter = new MethodParameter(
                GlobalExceptionHandlerTest.class.getDeclaredMethod("dummyMethod", String.class), 0);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ResponseEntity<ErrorResponse> response = handler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertAll(
                () -> assertThat(response.getBody().getError()).isEqualTo("VALIDATION_ERROR"),
                () -> assertThat(response.getBody().getMessage()).isEqualTo("Validation failed")
        );
    }

    @Test
    void handleHttpMessageNotReadable_withInvalidAmountCause_delegatesToInvalidAmount() {
        InvalidAmountException cause = new InvalidAmountException("Invalid amount format: abc");
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Failed to read HTTP message", cause, (HttpInputMessage) null);

        ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadable(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertAll(
                () -> assertThat(response.getBody().getError()).isEqualTo("INVALID_AMOUNT"),
                () -> assertThat(response.getBody().getMessage()).isEqualTo("Invalid amount format: abc")
        );
    }

    @Test
    void handleHttpMessageNotReadable_withInvalidCountryCause_delegatesToInvalidCountry() {
        InvalidCountryException cause = new InvalidCountryException("Country must be valid ISO 3166-1 alpha-2 code");
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Failed to read HTTP message", cause, (HttpInputMessage) null);

        ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadable(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertAll(
                () -> assertThat(response.getBody().getError()).isEqualTo("INVALID_COUNTRY"),
                () -> assertThat(response.getBody().getMessage()).isEqualTo("Country must be valid ISO 3166-1 alpha-2 code")
        );
    }

    @Test
    void handleHttpMessageNotReadable_withInvalidCurrencyCause_delegatesToInvalidCurrency() {
        InvalidCurrencyException cause = new InvalidCurrencyException("Invalid currency: XYZ");
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Failed to read HTTP message", cause, (HttpInputMessage) null);

        ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadable(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertAll(
                () -> assertThat(response.getBody().getError()).isEqualTo("INVALID_CURRENCY"),
                () -> assertThat(response.getBody().getMessage()).isEqualTo("Invalid currency: XYZ")
        );
    }

    @Test
    void handleHttpMessageNotReadable_withInvalidDirectionCause_delegatesToInvalidDirection() {
        InvalidDirectionException cause = new InvalidDirectionException("Invalid direction: SIDEWAYS");
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("Failed to read HTTP message", cause, (HttpInputMessage) null);

        ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadable(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertAll(
                () -> assertThat(response.getBody().getError()).isEqualTo("INVALID_DIRECTION"),
                () -> assertThat(response.getBody().getMessage()).isEqualTo("Invalid direction: SIDEWAYS")
        );
    }

    @Test
    void handleHttpMessageNotReadable_withGenericCause_returnsMalformedJson() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException("JSON parse error: Unexpected end-of-input", (HttpInputMessage) null);

        ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadable(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertAll(
                () -> assertThat(response.getBody().getError()).isEqualTo("MALFORMED_JSON"),
                () -> assertThat(response.getBody().getMessage()).contains("JSON parse error")
        );
    }

    @Test
    void handleHttpMessageNotReadable_withNullMessage_returnsDefaultMessage() {
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException(null, (Throwable) null, (HttpInputMessage) null);

        ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadable(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertAll(
                () -> assertThat(response.getBody().getError()).isEqualTo("MALFORMED_JSON"),
                () -> assertThat(response.getBody().getMessage()).isEqualTo("Malformed JSON request")
        );
    }

    @Test
    void handleTypeMismatch_returns400BadRequest() throws NoSuchMethodException {
        MethodParameter parameter = new MethodParameter(
                GlobalExceptionHandlerTest.class.getDeclaredMethod("dummyMethod", String.class), 0);
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                "invalid-uuid", UUID.class, "accountId", parameter, new IllegalArgumentException());

        ResponseEntity<ErrorResponse> response = handler.handleTypeMismatch(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertAll(
                () -> assertThat(response.getBody().getError()).isEqualTo("INVALID_ARGUMENT"),
                () -> assertThat(response.getBody().getMessage()).isEqualTo("Invalid value 'invalid-uuid' for parameter 'accountId'")
        );
    }

    @SuppressWarnings("unused")
    private void dummyMethod(String param) {}
}
