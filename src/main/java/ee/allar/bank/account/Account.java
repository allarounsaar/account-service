package ee.allar.bank.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private UUID id;
    private UUID customerId;
    private String country;
    private Instant createdAt;
    private Instant updatedAt;

    public Account(UUID customerId, String country) {
        this.customerId = customerId;
        this.country = country;
    }
}