package ru.origami.kafka.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.kafka.clients.consumer.Consumer;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class ConsumerConnection {

    private Class<?> clazz;

    private Consumer<String, String> consumer;

    private boolean isFree;

    private LocalDateTime startFreeTime = LocalDateTime.now();

    public ConsumerConnection setFree(boolean isFree) {
        this.isFree = isFree;

        if (isFree) {
            startFreeTime = LocalDateTime.now();
        }

        return this;
    }
}
