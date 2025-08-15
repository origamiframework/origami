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
import ru.origami.kafka.models.Properties;
import ru.origami.kafka.models.Topic;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.fail;
import static ru.origami.common.OrigamiHelper.getRandomFromList;
import static ru.origami.common.environment.Environment.*;
import static ru.origami.common.environment.Language.getLangValue;

@Slf4j
public class CommonSteps {

    protected Properties properties;

    List<Integer> neededPartitions = new ArrayList<>();

    Producer<String, String> getProducer() {
        Producer<String, String> producer = null;

        if (properties == null) {
            fail(getLangValue("kafka.props.null"));
        }

        try {
            producer = Connection.getProducer(properties);
        } catch (Exception e) {
            fail(getLangValue("kafka.cannot.connect").formatted(e.getMessage()));
        }

        return producer;
    }

    Consumer<String, String> getConsumer(boolean isEarliest) {
        Consumer<String, String> consumer = null;

        if (properties == null) {
            fail(getLangValue("kafka.props.null"));
        }

        try {
            consumer = Connection.getConsumer(properties, isEarliest);
        } catch (Exception e) {
            fail(getLangValue("kafka.cannot.connect").formatted(e.getMessage()));
        }

        return consumer;
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

    Consumer<String, String> subscribe(String topic, boolean isEarliest) {
        Consumer<String, String> consumer = getConsumer(isEarliest);

        try {
//            long epoch = new Date().getTime();
            List<TopicPartition> topicPartitions = null;
            Set<Integer> existingPartitions = consumer.partitionsFor(topic)
                    .stream()
                    .map(PartitionInfo::partition)
                    .collect(Collectors.toSet());

            if (existingPartitions.isEmpty()) {
                fail(getLangValue("kafka.no.partitions").formatted(topic));
            }

            if (neededPartitions.isEmpty()) {
                topicPartitions = IntStream.range(0, consumer.partitionsFor(topic).size())
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

//            Map<TopicPartition, Long> offsets = topicPartitions.stream().collect(Collectors.toMap(k -> k, v -> epoch));
//
//            Map<TopicPartition, Long> filteredOffsets = consumer.offsetsForTimes(offsets)
//                    .entrySet()
//                    .stream()
//                    .filter(offset -> offset.getValue() != null)
//                    .collect(Collectors.toMap(k -> k.getKey(), v -> v.getValue().offset()));

            consumer.assign(topicPartitions);

            if (isEarliest) {
                consumer.seekToBeginning(topicPartitions);
            } else {
                consumer.seekToEnd(Collections.emptySet());
//                consumer.commitSync();
            }
//            filteredOffsets.forEach(consumer::seek);
        } catch (Exception e) {
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

        return consumer;
    }

    long getNumberOfMessages(String topic) {
        Consumer<String, String> consumer = getConsumer(true);

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
}
