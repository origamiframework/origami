package ru.origami.kafka.utils;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import ru.origami.kafka.KafkaConnectionRegistry;
import ru.origami.kafka.models.ConsumerConnection;

import java.util.List;

public class KafkaCleanupExtension implements AfterAllCallback {

    @Override
    public void afterAll(ExtensionContext context) {
        Class<?> testClass = context.getRequiredTestClass();

        List<ConsumerConnection> connectionsToClose = KafkaConnectionRegistry.getConnectionsByCreator(testClass);

        if (!connectionsToClose.isEmpty()) {
            connectionsToClose.stream().filter(c -> !c.isClosed()).forEach(ConsumerConnection::close);
            KafkaConnectionRegistry.removeConnections(connectionsToClose);
        }
    }
}
