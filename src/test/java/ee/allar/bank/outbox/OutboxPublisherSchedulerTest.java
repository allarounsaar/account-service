package ee.allar.bank.outbox;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxPublisherSchedulerTest {

    @Mock
    private OutboxService outboxService;

    @Mock
    private OutboxEventPublisher eventPublisher;

    @InjectMocks
    private OutboxPublisherScheduler scheduler;

    private static final UUID EVENT_ID = UUID.fromString("01a0c35e-e723-767d-9265-60d0d5a07bf7");

    @Test
    void publish_successfulPublish_marksPublished() {
        OutboxEvent event = OutboxEvent.builder()
                .id(EVENT_ID)
                .aggregateId(UUID.randomUUID())
                .aggregateType("ACCOUNT")
                .eventType(OutboxEventType.CREATED)
                .payload("{}")
                .status(OutboxEventStatus.PENDING)
                .retryCount(0)
                .build();

        when(outboxService.findPendingForUpdate(50, 5)).thenReturn(List.of(event));

        scheduler.publish();

        verify(eventPublisher, times(1)).publish(event);
        verify(outboxService, times(1)).markPublished(EVENT_ID);
        verify(outboxService, never()).updateRetryState(any(), anyInt(), any(), any());
        verify(outboxService, never()).markFailed(any(), any());
    }

    @Test
    void publish_failureWithRetriesLeft_updatesRetryState() {
        OutboxEvent event = OutboxEvent.builder()
                .id(EVENT_ID)
                .aggregateId(UUID.randomUUID())
                .aggregateType("ACCOUNT")
                .eventType(OutboxEventType.CREATED)
                .payload("{}")
                .status(OutboxEventStatus.PENDING)
                .retryCount(1)
                .build();

        when(outboxService.findPendingForUpdate(50, 5)).thenReturn(List.of(event));
        doThrow(new RuntimeException("RabbitMQ connection refused")).when(eventPublisher).publish(event);

        scheduler.publish();

        verify(eventPublisher, times(1)).publish(event);
        verify(outboxService, never()).markPublished(any());
        verify(outboxService, times(1)).updateRetryState(
                eq(EVENT_ID),
                eq(2),
                any(Instant.class),
                eq("RabbitMQ connection refused")
        );
        verify(outboxService, never()).markFailed(any(), any());
    }

    @Test
    void publish_failureExceedingMaxRetries_marksFailed() {
        OutboxEvent event = OutboxEvent.builder()
                .id(EVENT_ID)
                .aggregateId(UUID.randomUUID())
                .aggregateType("ACCOUNT")
                .eventType(OutboxEventType.CREATED)
                .payload("{}")
                .status(OutboxEventStatus.PENDING)
                .retryCount(4)
                .build();

        when(outboxService.findPendingForUpdate(50, 5)).thenReturn(List.of(event));
        doThrow(new RuntimeException("RabbitMQ connection refused")).when(eventPublisher).publish(event);

        scheduler.publish();

        verify(eventPublisher, times(1)).publish(event);
        verify(outboxService, never()).markPublished(any());
        verify(outboxService, never()).updateRetryState(any(), anyInt(), any(), any());
        verify(outboxService, times(1)).markFailed(eq(EVENT_ID), eq("RabbitMQ connection refused"));
    }
}
