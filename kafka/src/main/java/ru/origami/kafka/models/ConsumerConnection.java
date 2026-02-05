package ru.origami.kafka.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.kafka.clients.consumer.Consumer;

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

    public ConsumerConnection(Class<?> clazz, Class<?> creatorClass) {
        this.clazz = clazz;
        this.creatorClass = creatorClass;
    }

    public ConsumerConnection setFree(boolean isFree) {
        this.isFree = isFree;

        if (isFree) {
            startFreeTime = LocalDateTime.now();
        }

        return this;
    }

    public void close() {
        if (!isClosed && Objects.nonNull(consumer)) {
            consumer.close();
            isClosed = true;
        }
    }
}
