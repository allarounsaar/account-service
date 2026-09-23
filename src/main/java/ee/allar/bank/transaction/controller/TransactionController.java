package ee.allar.bank.transaction.controller;

import ee.allar.bank.common.dto.ErrorResponse;
import ee.allar.bank.transaction.TransactionService;
import ee.allar.bank.transaction.dto.CreateTransactionRequest;
import ee.allar.bank.transaction.dto.TransactionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts/{accountId}/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "Transaction management endpoints")
public class TransactionController {

    private final TransactionService service;

    @PostMapping
    @Operation(summary = "Create a transaction", description = "Creates a monetary transaction (IN or OUT) and updates the account balance.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transaction created successfully", content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Insufficient funds, invalid amount, direction, or currency", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public TransactionResponse createTransaction(
            @Parameter(description = "Account UUID", example = "01a0c40a-66bc-74c2-a193-ad672b487816", required = true)
            @PathVariable UUID accountId,
            @Valid @RequestBody CreateTransactionRequest transaction) {
        return service.createTransaction(accountId, transaction);
    }

    @GetMapping
    @Operation(summary = "Get transactions by account ID", description = "Retrieves all transactions for the specified account.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully", content = @Content(array = @ArraySchema(schema = @Schema(implementation = TransactionResponse.class)))),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public List<TransactionResponse> getTransactions(
            @Parameter(description = "Account UUID", example = "01a0c40a-66bc-74c2-a193-ad672b487816", required = true)
            @PathVariable UUID accountId) {
        return service.getTransactions(accountId);
    }
}
