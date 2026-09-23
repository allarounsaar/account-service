package ee.allar.bank.outbox;


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
public class OutboxEvent {
    private UUID id;
    private UUID aggregateId;
    private String aggregateType;
    private OutboxEventType eventType;
    private String payload;
    private OutboxEventStatus status;
    private int retryCount;
    private String lastError;
    private Instant nextRetryAt;
    private Instant publishedAt;
    private Instant createdAt;
    private Instant updatedAt;
}