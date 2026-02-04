package ru.origami.kafka.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.kafka.clients.consumer.Consumer;

@Getter
@Setter
@ToString
public class ConsumerConnection {

    private Class<?> clazz;

    private Consumer<String, String> consumer;

    private boolean isFree;
}
