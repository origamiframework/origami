package ru.origami.test_containers.initializers;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.testcontainers.containers.Container;
import ru.origami.common.environment.Environment;
import ru.origami.test_containers.TestContainer;

import java.util.List;
import java.util.Objects;

import static ru.origami.common.environment.Language.getLangValue;

@Slf4j
public final class IbmMqInitializer {

    private IbmMqInitializer() {
    }

    public static void createQueues(TestContainer ibmMqContainer, List<String> queueNames) {
        if (CollectionUtils.isNotEmpty(queueNames)) {
            Container.ExecResult result;

            try {
                StringBuilder mqscScript = new StringBuilder();
                String user = Environment.getSysEnvPropertyOrDefault(ibmMqContainer.getName() + "_username",
                        ibmMqContainer.getName() + "_username", null);
                String name = Environment.getSysEnvPropertyOrDefault(ibmMqContainer.getName() + "_queue_manager",
                        ibmMqContainer.getName() + "_queue_manager", null);

                queueNames.forEach(queueName -> mqscScript
                        .append("DEFINE QLOCAL('")
                        .append(queueName)
                        .append("') REPLACE\n"));
                queueNames.forEach(queueName -> mqscScript
                        .append("SET AUTHREC PROFILE('")
                        .append(queueName)
                        .append("') OBJTYPE(QUEUE) ")
                        .append("  PRINCIPAL('")
                        .append(user)
                        .append("') AUTHADD(ALL)\n"));

                result = ibmMqContainer.getGenericContainer().execInContainer(
                        "/bin/bash",
                        "-c",
                        "echo \"" + mqscScript + "\" | runmqsc " + name);
            } catch (Exception e) {
                throw new RuntimeException(getLangValue("test.containers.ibm.mq.queues.created.error"), e);
            }

            if (result.getExitCode() != 0) {
                log.info("runmqsc stdout:\n{}", result.getStdout());
                System.err.printf("runmqsc stderr:%n%s", result.getStderr());
                throw new RuntimeException(getLangValue("test.containers.ibm.mq.queues.created.error.code").formatted(result.getExitCode()));
            } else {
                log.info(getLangValue("test.containers.ibm.mq.queues.created"), queueNames);
            }
        }
    }
}
