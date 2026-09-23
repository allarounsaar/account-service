package ee.allar.bank.account.controller;

import ee.allar.bank.account.AccountService;
import ee.allar.bank.account.dto.AccountResponse;
import ee.allar.bank.account.dto.CreateAccountRequest;
import ee.allar.bank.common.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Account management endpoints")
public class AccountController {

    private final AccountService service;

    @PostMapping
    @Operation(summary = "Create an account", description = "Creates a new bank account with initial zero balances for specified currencies.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account created successfully", content = @Content(schema = @Schema(implementation = AccountResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload, country, or currencies", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public AccountResponse createAccount(@Valid @RequestBody CreateAccountRequest request) {
        return service.createAccount(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account by ID", description = "Retrieves account details including balances for all supported currencies.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account found", content = @Content(schema = @Schema(implementation = AccountResponse.class))),
            @ApiResponse(responseCode = "404", description = "Account not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public AccountResponse getAccount(
            @Parameter(description = "Account UUID", example = "01a0c40a-66bc-74c2-a193-ad672b487816", required = true)
            @PathVariable UUID id) {
        return service.getAccountResponse(id);
    }
}
