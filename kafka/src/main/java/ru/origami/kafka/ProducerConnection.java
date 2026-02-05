package ru.origami.kafka;

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
