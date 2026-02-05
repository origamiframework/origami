package ru.origami.kafka;

import ru.origami.kafka.models.ConsumerConnection;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class KafkaConnectionRegistry {

    private static final Set<WeakReference<ConsumerConnection>> CONNECTIONS = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public static void register(ConsumerConnection conn) {
        CONNECTIONS.add(new WeakReference<>(conn));
    }

    public static List<ConsumerConnection> getConnectionsByCreator(Class<?> creatorClass) {
        List<ConsumerConnection> result = new ArrayList<>();

        for (WeakReference<ConsumerConnection> ref : CONNECTIONS) {
            ConsumerConnection conn = ref.get();

            if (conn != null && conn.getCreatorClass().equals(creatorClass)) {
                result.add(conn);
            }
        }

        return result;
    }

    public static void removeConnections(Collection<ConsumerConnection> connectionsToRemove) {
        Set<WeakReference<ConsumerConnection>> toRemove = new HashSet<>();

        for (WeakReference<ConsumerConnection> ref : CONNECTIONS) {
            ConsumerConnection conn = ref.get();

            if (conn != null && connectionsToRemove.contains(conn)) {
                toRemove.add(ref);
            }
        }

        CONNECTIONS.removeAll(toRemove);
    }
}
