package ee.allar.bank;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.rabbitmq.RabbitMQContainer;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@ActiveProfiles("test")
@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class BaseIntegrationTest {

    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:18-alpine");

    @ServiceConnection
    static RabbitMQContainer rabbitmq = new RabbitMQContainer("rabbitmq:4-alpine");

    static {
        postgres.start();
        rabbitmq.start();
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void cleanDatabase() {
        jdbcTemplate.execute("TRUNCATE TABLE outbox_event, transaction, balance, account CASCADE");
    }

    protected List<Map<String, Object>> findOutboxEvents(String aggregateType, UUID aggregateId) {
        return jdbcTemplate.queryForList(
                "SELECT * FROM outbox_event WHERE aggregate_type = ? AND aggregate_id = ?",
                aggregateType, aggregateId
        );
    }

    protected List<Map<String, Object>> findOutboxEvents(String aggregateType) {
        return jdbcTemplate.queryForList(
                "SELECT * FROM outbox_event WHERE aggregate_type = ?",
                aggregateType
        );
    }
}
