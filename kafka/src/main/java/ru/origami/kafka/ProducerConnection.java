package ru.origami.kafka;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.kafka.clients.producer.Producer;

import java.time.Duration;
import java.util.Objects;

@Getter
@Setter
@ToString
public class ProducerConnection {

    private Class<?> clazz;

    private Producer<String, String> producer;

    private Class<?> creatorClass;

    private boolean isClosed;

    public ProducerConnection(Class<?> clazz, Class<?> creatorClass) {
        this.clazz = clazz;
        this.creatorClass = creatorClass;
    }

    public void close() {
        if (!isClosed && Objects.nonNull(producer)) {
            try {
                producer.close(Duration.ofSeconds(5));
                isClosed = true;
            } catch (Exception e) {
            }
        }
    }
}
