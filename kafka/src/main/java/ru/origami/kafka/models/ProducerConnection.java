package ru.origami.kafka.models;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.kafka.clients.producer.Producer;

@Getter
@Setter
@ToString
public class ProducerConnection {

    private Class<?> clazz;

    private Producer<String, String> producer;
}
