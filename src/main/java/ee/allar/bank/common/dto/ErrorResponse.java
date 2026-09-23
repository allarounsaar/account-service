package ee.allar.bank.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Schema(description = "Standard error response")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    @Schema(description = "Error code identifier", example = "VALIDATION_ERROR")
    private final String error;

    @Schema(description = "Human-readable error message", example = "Validation failed")
    private final String message;

    @Schema(description = "Timestamp when the error occurred", example = "2026-09-23T12:00:00Z")
    private final Instant timestamp;

    @Schema(description = "List of detailed field validation errors")
    private final List<ValidationError> errors;

    public ErrorResponse(String error, String message) {
        this(error, message, null);
    }

    public ErrorResponse(String error, String message, List<ValidationError> errors) {
        this.error = error;
        this.message = message;
        this.errors = errors;
        this.timestamp = Instant.now();
    }

    public record ValidationError(
            @Schema(description = "Field name with validation failure", example = "amount")
            String field,
            @Schema(description = "Validation failure reason", example = "Amount is required")
            String message
    ) {
    }
}
