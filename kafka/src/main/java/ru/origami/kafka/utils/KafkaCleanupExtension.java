package ru.origami.kafka.utils;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import ru.origami.kafka.KafkaConnectionRegistry;
import ru.origami.kafka.ConsumerConnection;
import ru.origami.kafka.ProducerConnection;

import java.util.List;

public class KafkaCleanupExtension implements AfterAllCallback {

    @Override
    public void afterAll(ExtensionContext context) {
        Class<?> testClass = context.getRequiredTestClass();

        List<ConsumerConnection> consumerConnections = KafkaConnectionRegistry.getConsumerConnections(testClass);

        if (!consumerConnections.isEmpty()) {
            consumerConnections.stream().filter(c -> !c.isClosed()).forEach(ConsumerConnection::close);
            KafkaConnectionRegistry.removeConsumerConnections(consumerConnections);
        }

        List<ProducerConnection> producerConnections = KafkaConnectionRegistry.getProducerConnections(testClass);

        if (!producerConnections.isEmpty()) {
            producerConnections.stream().filter(c -> !c.isClosed()).forEach(ProducerConnection::close);
            KafkaConnectionRegistry.removeProducerConnections(producerConnections);
        }
    }
}
