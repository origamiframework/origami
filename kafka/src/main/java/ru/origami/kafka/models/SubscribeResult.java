package ru.origami.kafka.models;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SubscribeResult {

    private final ConsumerConnection connection;

    private final String topic;

    private List<ConsumerRecord<String, String>> records = new ArrayList<>();

    private final Class mappingClass;

    @Setter
    private boolean isSubscribed = true;

    @Setter
    private boolean isConsumerClosed = false;

    @Setter
    private Exception exception;

    public SubscribeResult(ConsumerConnection connection, Class mappingClass, String topic) {
        this.connection = connection;
        this.mappingClass = mappingClass;
        this.topic = topic;
    }

    @Override
    public String toString() {
        return String.format("consumer: %s, class: %s", connection.getConsumer(), mappingClass);
    }
}
