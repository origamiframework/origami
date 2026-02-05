package ru.origami.kafka;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.kafka.clients.consumer.Consumer;
import ru.origami.kafka.models.Topic;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

import static ru.origami.common.OrigamiHelper.waitInMillis;

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

    private SubscribeTopicTask subscribeTopicTask;

    public ConsumerConnection(Class<?> clazz, Class<?> creatorClass) {
        this.clazz = clazz;
        this.creatorClass = creatorClass;
    }

    public ConsumerConnection setFree(boolean isFree) {
        this.isFree = isFree;

        if (isFree) {
            startFreeTime = LocalDateTime.now();
            topic = null;
            subscribeTopicTask = null;
        }

        return this;
    }

    public void close() {
        if (!isClosed && Objects.nonNull(consumer)) {
            if (Objects.nonNull(topic) && Objects.nonNull(subscribeTopicTask)) {
                subscribeTopicTask.unsubscribe(topic.getTopic(), true, false);
                waitInMillis(520);
            }

            try {
                consumer.close(Duration.ofSeconds(5));
                isClosed = true;
            } catch (Exception e) {
            }
        }
    }
}
