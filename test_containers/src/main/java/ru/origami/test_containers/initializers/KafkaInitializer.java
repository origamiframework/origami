package ru.origami.test_containers.initializers;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.List;
import java.util.Properties;

import static ru.origami.common.environment.Language.getLangValue;

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

            try (AdminClient admin = AdminClient.create(props)) {
                admin.createTopics(topics).all().get();
                log.info(getLangValue("test.containers.kafka.topics.created"), topics.stream().map(NewTopic::name).toList());
            } catch (Exception e) {
                throw new RuntimeException(getLangValue("test.containers.kafka.topics.created.error").formatted(bootstrapServers), e);
            }
        }
    }
}
