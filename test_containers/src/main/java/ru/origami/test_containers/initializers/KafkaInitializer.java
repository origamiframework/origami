package ru.origami.test_containers.initializers;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.testcontainers.containers.GenericContainer;
import ru.origami.common.parallel.TestEnvironment;
import ru.origami.test_containers.TestContainer;

import java.util.*;
import java.util.stream.Collectors;

import static ru.origami.common.environment.Environment.EXECUTION_PARALLEL;
import static ru.origami.common.environment.Language.getLangValue;
import static ru.origami.test_containers.TestContainersLauncher.getExecutionParallelThreads;

@Slf4j
public final class KafkaInitializer {

    private KafkaInitializer() {
    }

    public static void createTopics(String bootstrapServers, List<NewTopic> topics) {
        if (CollectionUtils.isNotEmpty(topics)) {
            bootstrapServers = bootstrapServers.replace("PLAINTEXT://", "");
            Properties props = new Properties();
            props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
            props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, "30000");

            List<NewTopic> finalTopics = new ArrayList<>();

            if ("true".equalsIgnoreCase(EXECUTION_PARALLEL)) {
                for (int i = 1; i <= getExecutionParallelThreads(); i++) {
                    NewTopic topic = topics.get(i);
                    String newName = getTopicFullName(topic.name(), i);
                    NewTopic cloned = new NewTopic(newName, topic.numPartitions(), topic.replicationFactor());
                    Map<String, String> configs = topic.configs();

                    if (configs != null && !configs.isEmpty()) {
                        cloned.configs(configs);
                    }

                    finalTopics.add(topic);
                }
            } else {
                finalTopics = topics;
            }

            try (AdminClient admin = AdminClient.create(props)) {
                admin.createTopics(finalTopics).all().get();
                log.info(getLangValue("test.containers.kafka.topics.created"), topics.stream().map(NewTopic::name).toList());
            } catch (Exception e) {
                throw new RuntimeException(getLangValue("test.containers.kafka.topics.created.error").formatted(bootstrapServers), e);
            }
        }
    }

    public static String getTopicFullName(String topic, int threadNum) {
        return "%s-thread-%d;".formatted(topic, threadNum);
    }

    public static void changeTopicNames(List<NewTopic> topics, Map<GenericContainer<?>, TestEnvironment> containerEnvironments) {
        if ("true".equalsIgnoreCase(EXECUTION_PARALLEL)) {
            Set<String> topicNames = topics.stream()
                    .map(NewTopic::name)
                    .collect(Collectors.toSet());

            for (Map.Entry<GenericContainer<?>, TestEnvironment> entry : containerEnvironments.entrySet()) {
                TestEnvironment testEnv = entry.getValue();
                Map<String, String> envMap = entry.getKey().getEnvMap();

                for (Map.Entry<String, String> envEntry : envMap.entrySet()) {
                    String oldValue = envEntry.getValue();

                    if (topicNames.contains(oldValue)) {
                        envEntry.setValue(getTopicFullName(oldValue, testEnv.getId()));
                    }
                }
            }
        }
    }
}
