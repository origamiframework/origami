package ru.origami.kafka.models;

import lombok.Getter;
import lombok.Setter;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SubscribeResult {

    private final Consumer<String, String> consumer;

    private final String topic;

    private List<ConsumerRecord<String, String>> records = new ArrayList<>();

    private final Class mappingClass;

    @Setter
    private boolean isSubscribed = true;

    @Setter
    private boolean isConsumerClosed = false;

    @Setter
    private Exception exception;

    public SubscribeResult(Consumer<String, String> consumer, Class mappingClass, String topic) {
        this.consumer = consumer;
        this.mappingClass = mappingClass;
        this.topic = topic;
    }

    @Override
    public String toString() {
        return String.format("consumer: %s, class: %s", consumer, mappingClass);
    }
}
