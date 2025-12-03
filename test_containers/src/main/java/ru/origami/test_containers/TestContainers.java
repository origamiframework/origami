package ru.origami.test_containers;

import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import junit.framework.AssertionFailedError;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;
import ru.origami.common.environment.Environment;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.fail;
import static ru.origami.common.environment.Environment.getSysEnvPropertyOrDefault;
import static ru.origami.common.environment.Language.getLangValue;
import static ru.origami.test_containers.CmdUtil.SERVICE_SRC_DIR;
import static ru.origami.test_containers.CmdUtil.ensureServiceJarBuilt;

@Slf4j
public abstract class TestContainers {

    private static final String APP_LOG_LEVEL_PROP = "test.containers.app.log.level";
    private static final String CI_APP_LOG_LEVEL_PROP = "TEST_CONTAINERS_APP_LOG_LEVEL";
    private static final String APP_LOG_LEVEL = getSysEnvPropertyOrDefault(APP_LOG_LEVEL_PROP,CI_APP_LOG_LEVEL_PROP, "INFO");

    private static final String CONTAINERS_REUSE = "test.containers.reuse";
    private static final String CI_CONTAINERS_REUSE = "TEST_CONTAINERS_REUSE";

    private static final String CONTAINERS_FIXED_PORTS = "test.containers.fixed.ports";
    private static final String CI_CONTAINERS_FIXED_PORTS = "TEST_CONTAINERS_FIXED_PORTS";
    private static final String CONTAINERS_WITH_FIXED_PORTS = Environment.getSysEnvPropertyOrDefault(CONTAINERS_FIXED_PORTS,
            CI_CONTAINERS_FIXED_PORTS, null);

    private final AtomicBoolean IS_STARTED = new AtomicBoolean(false);

    protected Network network = Network.newNetwork();

    protected TestContainer postgres = null;
    protected TestContainer kafka = null;
    protected List<TestContainer> containers = new ArrayList();

    private boolean withPostgres = false;
    private boolean withKafka = false;

    protected List<NewTopic> kafkaTopics = new ArrayList<>();

    private boolean withFixedPorts = false;
    private static int lastPort = 8080;

    protected static int getLastPort() {
        return lastPort++;
    }

    protected void withFixedPorts() {
        withFixedPorts = true;
    }

    private boolean getWithFixedPorts() {
        if (Objects.nonNull(CONTAINERS_WITH_FIXED_PORTS)) {
            return "true".equalsIgnoreCase(CONTAINERS_WITH_FIXED_PORTS);
        }

        return withFixedPorts;
    }

    protected void withPostgres() {
        postgres = buildDefaultPostgreSQLContainer();
        withPostgres = true;
    }

    protected void withKafka() {
        kafka = buildDefaultKafkaContainer();
        withKafka = true;
    }

    public final void startIfNeeded() throws AssertionFailedError {
        synchronized (this) {
            if (IS_STARTED.getAndSet(true)) {
                return;
            }

//            boolean reuse = reuseEnabled(); // todo на будущее подумать нужно ли

            if (withPostgres) {
                if (Objects.isNull(postgres)) {
                    postgres = buildDefaultPostgreSQLContainer();
                }

                try {
                    postgres.getPostgreSQLContainer().start();
                    log.info(getLangValue("test.containers.postgres.started"),
                            postgres.getPostgreSQLContainer().getDockerImageName(), postgres.getPostgreSQLContainer().getJdbcUrl());

                    if (Objects.nonNull(postgres.getName())) {
                        System.setProperty(postgres.getName() + "_host", postgres.getPostgreSQLContainer().getHost());
                        System.setProperty(postgres.getName() + "_port", String.valueOf(postgres.getPostgreSQLContainer()
                                .getMappedPort(postgres.getOriginalPort())));
                    }
                } catch (Exception e) {
                    fail(getLangValue("test.containers.postgres.started.error").formatted(e.getMessage()));
                }
            }

            if (withKafka) {
                if (Objects.isNull(kafka)) {
                    kafka = buildDefaultKafkaContainer();
                }

                try {
                    kafka.getKafkaContainer().start();
                    log.info(getLangValue("test.containers.kafka.started"),
                            kafka.getKafkaContainer().getDockerImageName(), kafka.getKafkaContainer().getBootstrapServers());

                    if (Objects.nonNull(kafka.getName())) {
                        System.setProperty(kafka.getName() + "_bootstrap_servers", kafka.getKafkaContainer().getBootstrapServers());
                    }
                } catch (Exception e) {
                    fail(getLangValue("test.containers.kafka.started.error").formatted(e.getMessage()));
                }

                if (CollectionUtils.isNotEmpty(kafkaTopics)) {
                    String bootstrapServers = kafka.getKafkaContainer().getBootstrapServers().replace("PLAINTEXT://", "");
                    createTopics(bootstrapServers, kafkaTopics);
                }
            }

            try {
                startContainersByPriority();

                for (TestContainer startable : containers) {
                    if (startable.getContainer() instanceof GenericContainer<?> container) {
                        if (container.isRunning()) {
                            String mappedPorts = container.getExposedPorts().stream()
                                    .map(p -> p + "->" + container.getMappedPort(p))
                                    .map(String::valueOf)
                                    .collect(Collectors.joining(", "));
                            log.info(getLangValue("test.containers.container.started"),
                                    startable.getName(), container.getDockerImageName(), true, container.getNetworkAliases(),
                                    container.getHost(), mappedPorts);

                            if (Objects.nonNull(startable.getName())) {
                                String schema = "";

                                if (!(container instanceof JdbcDatabaseContainer)) {
                                    schema = "http://";
                                }

                                System.setProperty(startable.getName() + "_host", schema + container.getHost());
                                System.setProperty(startable.getName() + "_port", String.valueOf(
                                        container.getMappedPort(startable.getOriginalPort())));
                                System.setProperty(startable.getName() + "_ws_host", container.getHost());
                                System.setProperty(startable.getName() + "_ws_port", String.valueOf(container
                                        .getMappedPort(startable.getOriginalPort())));
                            }
                        }
                    } else {
                        // На случай нестандартных Startable
                        log.info(getLangValue("test.containers.non.standard.container.started"), startable.getClass().getName());
                    }
                }
            } catch (Exception e) {
                for (TestContainer startable : containers) {
                    if (startable.getContainer() instanceof GenericContainer<?> container) {
                        if (!container.isRunning()) {
                            log.error(getLangValue("test.containers.container.started.error"), startable.getName(),
                                    container.getDockerImageName(), e.getMessage());
                        }
                    }
                }

                fail(getLangValue("test.containers.started.error").formatted(e.getMessage()));
            }
        }
    }

    protected boolean reuseEnabled() {
        String value = getSysEnvPropertyOrDefault(CONTAINERS_REUSE, CI_CONTAINERS_REUSE, "false");

        return "true".equalsIgnoreCase(value);
    }

    protected TestContainer buildDefaultPostgreSQLContainer() {
        return buildDefaultPostgreSQLContainer("postgres_container");
    }

    protected TestContainer buildDefaultPostgreSQLContainer(String containerName) {
        int port = 5432;
        PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:16"))
                .withNetwork(network)
                .withNetworkAliases("db")
                .withDatabaseName("testdb")
                .withUsername("postgres")
                .withPassword("postgres");

        if (getWithFixedPorts()) {
            postgreSQLContainer.withCreateContainerCmdModifier(cmd -> cmd.getHostConfig()
                    .withPortBindings(new PortBinding(Ports.Binding.bindPort(port), new ExposedPort(port))));
        }

        return new TestContainer()
                .setName(containerName)
                .setOriginalPort(port)
                .setContainer(postgreSQLContainer);
    }

    protected TestContainer buildDefaultKafkaContainer() {
        return buildDefaultKafkaContainer("kafka_container");
    }

    protected TestContainer buildDefaultKafkaContainer(String containerName) {
        int port = 9092;
        KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("apache/kafka:3.9.1"))
                .withNetwork(network)
                .withNetworkAliases("broker")
                .withListener("broker:19092")
                .withExposedPorts(port);

        if (getWithFixedPorts()) {
            kafkaContainer.withCreateContainerCmdModifier(cmd -> cmd.getHostConfig()
                    .withPortBindings(new PortBinding(Ports.Binding.bindPort(port), new ExposedPort(port))));
        }

        return new TestContainer()
                .setName(containerName)
                .setOriginalPort(port)
                .setContainer(kafkaContainer);
    }

    protected NewTopic getTopic(String name, int partitions, short rf, boolean compact) {
        NewTopic t = new NewTopic(name, partitions, rf);

        if (compact) {
            Map<String, String> cfg = new HashMap<>();
            cfg.put(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_COMPACT);
            t.configs(cfg);
        }

        return t;
    }

    private void createTopics(String bootstrapServers, List<NewTopic> topics) {
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

    protected TestContainer buildDefaultAppContainer(String imageName, String containerName) {
        return buildDefaultAppContainer(imageName, null, containerName, null, null);
    }

    protected TestContainer buildDefaultAppContainer(String imageName, String containerName, Integer startPriority) {
        return buildDefaultAppContainer(imageName, null, containerName, startPriority, null);
    }

    protected TestContainer buildDefaultAppContainer(String imageName, String imageVersion, String containerName) {
        return buildDefaultAppContainer(imageName, imageVersion, containerName, null, null);
    }

    protected TestContainer buildDefaultAppContainer(String imageName, String imageVersion, String containerName,
                                                     Integer startPriority, String ciJavaOpts) {
        GenericContainer<?> genericContainer;
        int port = 8080;

        if (Environment.isLocal()) {
            if (Objects.nonNull(imageVersion)) {
                imageName = "%s:%s".formatted(imageName, imageVersion);
            }

            genericContainer = new GenericContainer<>(DockerImageName.parse(imageName));
        } else {
            ensureServiceJarBuilt(imageName);

            ImageFromDockerfile appImage = new ImageFromDockerfile(imageName, false)
                    .withDockerfileFromBuilder(d -> {
                                d.from("eclipse-temurin:17-jre")
                                        .copy("app.jar", "/app.jar")
                                        .entryPoint("sh", "-c", "java $JAVA_OPTS -jar /app.jar");

                                if (Objects.nonNull(ciJavaOpts)) {
                                    d.env("JAVA_OPTS", ciJavaOpts);
                                } else {
                                    d.env("JAVA_OPTS", "");
                                }

                                d.build();
                            }
                    )
                    .withFileFromPath("app.jar", Path.of("%s/target/%s.jar".formatted(SERVICE_SRC_DIR, imageName)));

            genericContainer = new GenericContainer<>(appImage);
        }

        if (withPostgres) {
            genericContainer.dependsOn(postgres.getPostgreSQLContainer())
                    .withEnv("DATASOURCE_URL", "jdbc:postgresql://db:5432/testdb")
                    .withEnv("DATASOURCE_SCHEMA", "public")
                    .withEnv("DATASOURCE_USER", "postgres")
                    .withEnv("DATASOURCE_PASSWORD", "postgres");
        }

        if (withKafka) {
            genericContainer.dependsOn(kafka.getKafkaContainer())
                    .withEnv("KAFKA_BROKERS", "broker:19092")
                    .withEnv("KAFKA_BOOTSTRAP_SERVERS", "broker:19092");
        }

        if (getWithFixedPorts()) {
            int bindPort = getLastPort();
            genericContainer.withCreateContainerCmdModifier(cmd -> cmd.getHostConfig()
                    .withPortBindings(new PortBinding(Ports.Binding.bindPort(bindPort), new ExposedPort(port))));
        }

        return new TestContainer()
                .setName(containerName)
                .setPriority(startPriority)
                .setOriginalPort(port)
                .setContainer(genericContainer
                        .withNetwork(network)
                        .withExposedPorts(port)
                        .withEnv("LOG_LEVEL", APP_LOG_LEVEL)
                        .withEnv("LOGGING_LEVEL_ROOT", APP_LOG_LEVEL)
                        .withEnv("LOGGING_LEVEL_ORG_APACHE_KAFKA", APP_LOG_LEVEL));
    }

    private String getContainerEnvByName(GenericContainer<?> container, String envName) throws Exception {
        InspectContainerResponse info = container.getContainerInfo();
        String[] envList = info.getConfig().getEnv();

        if (Objects.isNull(envName) || Objects.isNull(envList)) {
            return null;
        } else {
            return Arrays.stream(envList)
                    .filter(s -> s.startsWith("%s=".formatted(envName)))
                    .map(s -> s.substring("%s=".formatted(envName).length()))
                    .findFirst()
                    .orElse(null);
        }
    }

    private void startContainersByPriority() {
        if (CollectionUtils.isNotEmpty(containers)) {
            List<List<TestContainer>> groupsByPriority = containers.stream()
                    .collect(Collectors.groupingBy(TestContainer::getPriorityOrDefault))
                    .entrySet().stream()
                    .sorted(Map.Entry.comparingByKey(Comparator.nullsLast(Comparator.naturalOrder())))
                    .map(Map.Entry::getValue)
                    .toList();

            for (List<TestContainer> toStartList : groupsByPriority) {
                Startables.deepStart(toStartList.stream().map(TestContainer::getContainer)).join();
            }
        }
    }
}
