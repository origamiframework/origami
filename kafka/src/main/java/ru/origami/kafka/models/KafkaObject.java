package ru.origami.kafka.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KafkaObject<T> {

    private Exception exception;

    private T value;

    @Override
    public String toString() {
        return String.format("ex: %s, value: %s", exception, value);
    }
}
