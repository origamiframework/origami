package ru.origami.test_containers;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.lifecycle.Startable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.fail;
import static ru.origami.common.environment.Language.getLangValue;

@Getter
@Setter
@ToString
public class TestContainer {

    private Startable container;
    
    private String name;

    private Integer priority;

    private Integer originalPort;

    protected List<String> databaseScriptLocations = new ArrayList<>();

    public GenericContainer<?> getGenericContainer() {
        return (GenericContainer<?>) container;
    }

    public JdbcDatabaseContainer<?> getDatabaseContainer() {
        return (JdbcDatabaseContainer<?>) container;
    }

    public KafkaContainer getKafkaContainer() {
        return (KafkaContainer) container;
    }

    public Integer getPriorityOrDefault() {
        return Objects.isNull(priority) ? Integer.MAX_VALUE : priority;
    }

    public Integer getOriginalPort() {
        if (Objects.isNull(originalPort)) {
            fail(getLangValue("test.containers.original.port.null"));
        }

        return originalPort;
    }
}
