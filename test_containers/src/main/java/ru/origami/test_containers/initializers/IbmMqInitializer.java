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
            StringBuilder mqscScript = new StringBuilder();

            queueNames.forEach(queueName -> mqscScript
                    .append("DEFINE QLOCAL('")
                    .append(queueName)
                    .append("') REPLACE\n"));
            queueNames.forEach(queueName -> mqscScript
                    .append("SET AUTHREC PROFILE('")
                    .append(queueName)
                    .append("') OBJTYPE(QUEUE) ")
                    .append("SET AUTHREC PROFILE('TEST.QUEUE1') OBJTYPE(QUEUE) ")
                    .append("  PRINCIPAL('")
                    .append(Environment.get(ibmMqContainer.getName() + "_user"))
                    .append("') AUTHADD(ALL)\n"));

            Container.ExecResult result;

            try {
                result = ibmMqContainer.getGenericContainer().execInContainer(
                        "/bin/bash",
                        "-c",
                        "echo \"" + mqscScript.toString().replace("\n", "\\n") + "\" | runmqsc "
                                + Environment.get(ibmMqContainer.getName() + "_name"));
            } catch (Exception e) {
                throw new RuntimeException(getLangValue("test.containers.ibm.mq.queues.created.error"), e);
            }

            if (Objects.nonNull(result) && result.getExitCode() != 0) {
                throw new RuntimeException(getLangValue("test.containers.ibm.mq.queues.created.error.code").formatted(result.getExitCode()));
            } else {
                log.info(getLangValue("test.containers.ibm.mq.queues.created"), queueNames);
            }
        }
    }
}
