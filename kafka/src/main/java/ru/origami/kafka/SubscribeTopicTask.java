package ru.origami.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import ru.origami.kafka.models.ConsumerConnection;
import ru.origami.kafka.models.SubscribeResult;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.fail;
import static ru.origami.common.environment.Language.getLangValue;
import static ru.origami.kafka.ConsumerSteps.DURATION;

@Slf4j
public class SubscribeTopicTask extends TimerTask {

    private List<SubscribeResult> subscribeList = new CopyOnWriteArrayList<>();

    void addSubscribe(ConsumerConnection conn, Class mappingClass, String topic) {
        // TODO ошибку fail(getLangValue("kafka.fail.unsubscribe.no.subscribe").formatted(topic)) вынести сюда и отменять прошлые подписки
        subscribeList.add(new SubscribeResult(conn, mappingClass, topic));
        waitInMillis(950);
    }

    List<SubscribeResult> unsubscribe(String topic, boolean needUnsubscribe) {
        waitInMillis(250L);

        Stream<SubscribeResult> resultStream = subscribeList.stream()
                .filter(r -> r.getTopic().equals(topic))
                .filter(SubscribeResult::isSubscribed);

        if (needUnsubscribe) {
            resultStream = resultStream
                    .map(r -> r.setSubscribed(false))
                    .peek(r -> r.getConnection().setFree(true));
        }

        List<SubscribeResult> results = resultStream.toList();
        waitInMillis(100);

        if (results.isEmpty()) {
            fail(getLangValue("kafka.fail.unsubscribe.no.subscribe").formatted(topic));
        }

        if (results.size() > 1) {
            log.info(getLangValue("kafka.many.subscribes"), results.size(), topic);
        }

        String exceptionsText = results.stream()
                .map(SubscribeResult::getException)
                .filter(Objects::nonNull)
                .map(Exception::getMessage)
                .collect(Collectors.joining("\n"));

        if (!exceptionsText.isEmpty()) {
            results.forEach(r -> {
                r.setSubscribed(false);
                r.getConnection().setFree(true);
            });
            fail(getLangValue("kafka.fail.subscribe").formatted(topic, exceptionsText));
        }

        return results;
    }

    @Override
    public void run() {
        while (true) {
            subscribeList.removeAll(subscribeList.parallelStream()
                    .peek(this::addRecords)
                    .filter(r -> !r.isSubscribed())
                    .peek(r -> r.getConnection().setFree(true))
                    .toList());
        }
    }

    private void addRecords(SubscribeResult subscribeResult) {
        if (Objects.isNull(subscribeResult.getException())) {
            try {
                subscribeResult.getRecords().addAll(StreamSupport
                        .stream(subscribeResult.getConnection().getConsumer().poll(DURATION)
                                .records(subscribeResult.getTopic()).spliterator(), false)
                        .collect(Collectors.toList()));
            } catch (Exception ex) {
                subscribeResult.setException(ex);
            }
        }
    }

    public static void waitInMillis(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
