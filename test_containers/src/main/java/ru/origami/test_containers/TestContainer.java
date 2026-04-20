package ru.origami.test_containers;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
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

    private GenericContainerReplicaSet containerReplicaSet;

    private int replicaCount;
    
    private String name;

    private Integer priority;

    private Integer originalPort;

    protected List<String> databaseScriptLocations = new ArrayList<>();

    private String postgreSQLSchema;

    public JdbcDatabaseContainer<?> getDatabaseContainer() {
        return (JdbcDatabaseContainer<?>) containerReplicaSet.getGenericContainers().getFirst();
    }

    public KafkaContainer getKafkaContainer() {
        return (KafkaContainer) containerReplicaSet.getGenericContainers().getFirst();
    }

    public GenericContainer<?> getIbmMqContainer() {
        return containerReplicaSet.getGenericContainers().getFirst();
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

    public List<Startable> getAllContainers() {
        List<Startable> allContainers = new ArrayList<>();

        if (Objects.nonNull(containerReplicaSet)) {
            allContainers.addAll(containerReplicaSet.getGenericContainers());
            allContainers.addAll(containerReplicaSet.getContainers());
        }

        return allContainers;
    }
}
