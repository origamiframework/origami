package ru.origami.kafka.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.kafka.clients.consumer.Consumer;
import ru.origami.kafka.ConsumerSteps;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
public class ConsumerConnection {

    private Class<?> clazz;

    private Class<?> creatorClass;

    private Consumer<String, String> consumer;

    private boolean isFree;

    private LocalDateTime startFreeTime = LocalDateTime.now();

    private boolean isClosed;

    private Topic topic;

    private ConsumerSteps consumerSteps;

    public ConsumerConnection(Class<?> clazz, Class<?> creatorClass) {
        this.clazz = clazz;
        this.creatorClass = creatorClass;
    }

    public ConsumerConnection setFree(boolean isFree) {
        this.isFree = isFree;

        if (isFree) {
            startFreeTime = LocalDateTime.now();
            topic = null;
            consumerSteps = null;
        }

        return this;
    }

    public void close() {
        if (!isClosed && Objects.nonNull(consumer)) {
            if (Objects.nonNull(topic) && Objects.nonNull(consumerSteps)) {
                consumerSteps.unsubscribeWhenGetMessageWithEmptyResult(topic, 0);
            }

            consumer.close();
            isClosed = true;
        }
    }
}
