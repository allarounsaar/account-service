package ee.allar.bank.outbox.mapper;

import ee.allar.bank.outbox.OutboxEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Mapper
public interface OutboxEventMapper {
    void insert(OutboxEvent event);

    List<OutboxEvent> findPendingForUpdate(@Param("limit") int limit, @Param("maxRetries") int maxRetries);

    int markPublished(@Param("id") UUID id);

    int updateRetryState(@Param("id") UUID id,
                         @Param("retryCount") int retryCount,
                         @Param("nextRetryAt") Instant nextRetryAt,
                         @Param("lastError") String lastError);

    int markFailed(@Param("id") UUID id, @Param("lastError") String lastError);
}