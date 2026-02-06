package ru.origami.kafka;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class KafkaConnectionRegistry {

    private static final Set<WeakReference<ConsumerConnection>> CONSUMER_CONNECTIONS = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private static final Set<WeakReference<ProducerConnection>> PRODUCER_CONNECTIONS = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public static void register(ConsumerConnection conn) {
        CONSUMER_CONNECTIONS.add(new WeakReference<>(conn));
    }

    public static void register(ProducerConnection conn) {
        PRODUCER_CONNECTIONS.add(new WeakReference<>(conn));
    }

    public static List<ConsumerConnection> getConsumerConnections(Class<?> creatorClass) {
        List<ConsumerConnection> result = new ArrayList<>();

        for (WeakReference<ConsumerConnection> ref : CONSUMER_CONNECTIONS) {
            ConsumerConnection conn = ref.get();

            if (conn != null && conn.getCreatorClass().equals(creatorClass)) {
                result.add(conn);
            }
        }

        return result;
    }

    public static List<ProducerConnection> getProducerConnections(Class<?> creatorClass) {
        List<ProducerConnection> result = new ArrayList<>();

        for (WeakReference<ProducerConnection> ref : PRODUCER_CONNECTIONS) {
            ProducerConnection conn = ref.get();

            if (conn != null && conn.getCreatorClass().equals(creatorClass)) {
                result.add(conn);
            }
        }

        return result;
    }

    public static void removeConsumerConnections(Collection<ConsumerConnection> connectionsToRemove) {
        Set<WeakReference<ConsumerConnection>> toRemove = new HashSet<>();

        for (WeakReference<ConsumerConnection> ref : CONSUMER_CONNECTIONS) {
            ConsumerConnection conn = ref.get();

            if (conn == null || connectionsToRemove.contains(conn)) {
                toRemove.add(ref);
            }
        }

        CONSUMER_CONNECTIONS.removeAll(toRemove);
    }

    public static void removeProducerConnections(Collection<ProducerConnection> connectionsToRemove) {
        Set<WeakReference<ProducerConnection>> toRemove = new HashSet<>();

        for (WeakReference<ProducerConnection> ref : PRODUCER_CONNECTIONS) {
            ProducerConnection conn = ref.get();

            if (conn == null || connectionsToRemove.contains(conn)) {
                toRemove.add(ref);
            }
        }

        PRODUCER_CONNECTIONS.removeAll(toRemove);
    }
}
