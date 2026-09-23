package ee.allar.bank.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisherScheduler {
    private static final int BATCH_SIZE = 50;
    private static final int MAX_RETRIES = 5;

    private final OutboxService outboxService;
    private final OutboxEventPublisher eventPublisher;

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void publish() {
        List<OutboxEvent> pending = outboxService.findPendingForUpdate(BATCH_SIZE, MAX_RETRIES);

        for (OutboxEvent event : pending) {
            try {
                eventPublisher.publish(event);
                outboxService.markPublished(event.getId());
                log.info("Outbox event {} published successfully", event.getId());
            } catch (Exception e) {
                log.error("Failed to publish outbox event {}: {}", event.getId(), e.getMessage(), e);
                handlePublishFailure(event, e);
            }
        }
    }

    private void handlePublishFailure(OutboxEvent event, Exception e) {
        int nextRetryCount = event.getRetryCount() + 1;

        if (nextRetryCount >= MAX_RETRIES) {
            log.error("Outbox event {} exceeded max retries. Marking as FAILED.", event.getId());
            outboxService.markFailed(event.getId(), e.getMessage());
        } else {
            long delaySeconds = (long) Math.pow(2, nextRetryCount) * 5;
            Instant nextRetryAt = Instant.now().plusSeconds(delaySeconds);

            log.warn("Retrying outbox event {} in {} seconds (Attempt {}/{})",
                    event.getId(), delaySeconds, nextRetryCount, MAX_RETRIES);

            outboxService.updateRetryState(
                    event.getId(),
                    nextRetryCount,
                    nextRetryAt,
                    e.getMessage()
            );
        }
    }
}
