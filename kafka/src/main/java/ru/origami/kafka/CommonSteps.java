package ru.origami.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.Header;
import ru.origami.kafka.attachment.KafkaAttachment;
import ru.origami.kafka.models.ConsumerConnection;
import ru.origami.kafka.models.ProducerConnection;
import ru.origami.kafka.models.Properties;
import ru.origami.kafka.models.Topic;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.fail;
import static ru.origami.common.OrigamiHelper.getRandomFromList;
import static ru.origami.common.environment.Environment.*;
import static ru.origami.common.environment.Language.getLangValue;
import static ru.origami.kafka.KafkaConnectionRegistry.getConnectionsByCreator;

@Slf4j
public class CommonSteps {

    protected Properties properties;

    List<Integer> neededPartitions = new ArrayList<>();

    List<ProducerConnection> producerPool = new ArrayList<>();

    synchronized Producer<String, String> getProducer() {
        Producer<String, String> producer = null;

        if (properties == null) {
            fail(getLangValue("kafka.props.null"));
        }

        try {
            ProducerConnection conn = producerPool.stream()
                    .filter(c -> c.getClazz().equals(this.getClass()))
                    .findFirst()
                    .orElse(null);

            if (Objects.nonNull(conn)) {
                producer = conn.getProducer();
            } else {
                producer = Connection.getProducer(properties);
                producerPool.add(new ProducerConnection().setProducer(producer).setClazz(this.getClass()));
            }
        } catch (Exception e) {
            fail(getLangValue("kafka.cannot.connect").formatted(e.getMessage()));
        }

        return producer;
    }

    synchronized ConsumerConnection getConsumer(boolean isEarliest) {
        ConsumerConnection conn = null;

        if (properties == null) {
            fail(getLangValue("kafka.props.null"));
        }

        try {
            if (!isEarliest) {
                conn = getConnectionsByCreator(getCallerClass()).stream()
                        .filter(c -> c.getClazz().equals(this.getClass()))
                        .filter(ConsumerConnection::isFree)
                        .filter(c -> Duration.between(c.getStartFreeTime(), LocalDateTime.now()).toMillis() >= 500L)
                        .findFirst()
                        .orElse(null);
            }

            if (Objects.isNull(conn)) {
                conn = new ConsumerConnection(this.getClass(), getCallerClass())
                        .setConsumer(Connection.getConsumer(properties, isEarliest));

                if (!isEarliest) {
                    KafkaConnectionRegistry.register(conn);
                }
            }
        } catch (Exception e) {
            fail(getLangValue("kafka.cannot.connect").formatted(e.getMessage()));
        }

        return conn.setFree(false);
    }

    void send(Producer<String, String> producer, String topic, String key, String message, List<Header> headers) {
        try {
            Set<Integer> existingPartitions = producer.partitionsFor(topic).stream()
                    .map(PartitionInfo::partition)
                    .collect(Collectors.toSet());
            Integer partition = null;

            if (!neededPartitions.isEmpty()) {
                if (existingPartitions.contains(neededPartitions.get(0))) {
                    partition = neededPartitions.get(0);
                } else {
                    fail(getLangValue("kafka.no.existing.partition").formatted(neededPartitions.get(0),
                            existingPartitions.stream().map(String::valueOf).collect(Collectors.joining(", "))));
                }
            } else {
                partition = getRandomFromList(existingPartitions.stream().toList());
            }

            RecordMetadata recordMetadata = (RecordMetadata) producer
                    .send(new ProducerRecord(topic, partition, key, message, headers))
                    .get();

            if (recordMetadata.hasOffset()) {
                KafkaAttachment.attachProducerMessageToAllure(topic, partition, key, message);
            } else {
                fail(getLangValue("kafka.message.not.posted").formatted(topic));
            }
        } catch (Exception e) {
            fail(getLangValue("kafka.fail.sent").formatted(topic, e.getMessage()));
        } finally {
            neededPartitions.clear();
        }
    }

    ConsumerConnection subscribe(String topic, boolean isEarliest) {
        ConsumerConnection conn = getConsumer(isEarliest);

        try {
            List<TopicPartition> topicPartitions = null;
            Set<Integer> existingPartitions = conn.getConsumer().partitionsFor(topic)
                    .stream()
                    .map(PartitionInfo::partition)
                    .collect(Collectors.toSet());

            if (existingPartitions.isEmpty()) {
                fail(getLangValue("kafka.no.partitions").formatted(topic));
            }

            if (neededPartitions.isEmpty()) {
                topicPartitions = IntStream.range(0, conn.getConsumer().partitionsFor(topic).size())
                        .mapToObj(i -> new TopicPartition(topic, i))
                        .collect(Collectors.toList());
            } else {
                List<Integer> notExistingPartitions = neededPartitions.stream()
                        .filter(p -> !existingPartitions.contains(p))
                        .toList();

                if (notExistingPartitions.isEmpty()) {
                    topicPartitions = neededPartitions.stream()
                            .map(i -> new TopicPartition(topic, i))
                            .collect(Collectors.toList());
                } else {
                    fail((notExistingPartitions.size() == 1
                            ? getLangValue("kafka.no.existing.partition")
                            : getLangValue("kafka.no.existing.partitions")).formatted(
                            notExistingPartitions.stream().map(String::valueOf).collect(Collectors.joining(", ")),
                            existingPartitions.stream().map(String::valueOf).collect(Collectors.joining(", "))));
                }
            }

            conn.getConsumer().assign(topicPartitions);

            if (isEarliest) {
                conn.getConsumer().seekToBeginning(topicPartitions);
            } else {
                conn.getConsumer().seekToEnd(topicPartitions);
            }

            conn.getConsumer().poll(Duration.ofMillis(500));
        } catch (Exception e) {
            if (isEarliest) {
                conn.close();
            } else {
                conn.setFree(true);
            }

            fail(getLangValue("kafka.fail.subscribe").formatted(topic, e.getMessage()));
        }

        if (isLocal() || isLoggingEnabled()) {
            if (neededPartitions.isEmpty()) {
                log.info(getLangValue("kafka.success.subscribe"), topic);
            } else {
                log.info(neededPartitions.size() == 1
                                ? getLangValue("kafka.success.subscribe.with.partition")
                                : getLangValue("kafka.success.subscribe.with.partitions"),
                        neededPartitions.stream().map(String::valueOf).collect(Collectors.joining(", ")), topic);
            }
        }

        return conn;
    }

    long getNumberOfMessages(String topic) {
        ConsumerConnection conn = getConsumer(true);
        Consumer<String, String> consumer = conn.getConsumer();

        try {
            List<TopicPartition> partitions = consumer.partitionsFor(topic)
                    .stream()
                    .map(p -> new TopicPartition(topic, p.partition()))
                    .collect(Collectors.toList());

            consumer.assign(partitions);
            consumer.seekToEnd(Collections.emptySet());
            Map<TopicPartition, Long> endPartitions = partitions.stream().collect(Collectors.toMap(Function.identity(), consumer::position));

            consumer.seekToBeginning(Collections.emptySet());

            return partitions.stream().mapToLong(p -> endPartitions.get(p) - consumer.position(p)).sum();
        } catch (Exception e) {
            fail(getLangValue("kafka.fail.subscribe").formatted(topic, e.getMessage()));
        } finally {
            conn.close();
        }

        return 0;
    }

    String getTopicFullName(Topic topic) {
        StringBuilder topicName = new StringBuilder();

        if (Objects.nonNull(properties.getTopicPrefix())) {
            topicName.append(properties.getTopicPrefix());
        }

        topicName.append(topic.getTopic());

        if (Objects.nonNull(properties.getTopicPostfix())) {
            topicName.append(properties.getTopicPostfix());
        }

        return topicName.toString();
    }



    private Class<?> getCallerClass() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();

        for (int i = 2; i < stack.length; i++) {
            StackTraceElement element = stack[i];
            String className = element.getClassName();

            if (className.startsWith("ru.origami.kafka.") ||
                    className.startsWith("java.") ||
                    className.startsWith("javax.") ||
                    className.startsWith("sun.") ||
                    className.startsWith("jdk.") ||
                    className.startsWith("org.junit.") ||
                    className.startsWith("org.apache.kafka.")) {
                continue;
            }

            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {
            }
        }

        return Object.class;
    }
}
