package ru.origami.kafka.attachment;

import lombok.extern.slf4j.Slf4j;
import ru.origami.testit_allure.allure.java_commons.Attachment;
import ru.origami.testit_allure.annotations.Description;
import ru.origami.testit_allure.annotations.Step;

import static ru.origami.common.environment.Environment.*;
import static ru.origami.common.environment.Language.getLangValue;
import static ru.origami.testit_allure.test_it.testit.aspects.StepAspect.TEST_IT_ATTACHMENT_TECH_STEP_VALUE;

@Slf4j
public class KafkaAttachment {

    @Attachment(value = "{0}.send", type = "application/json")
    @Step(TEST_IT_ATTACHMENT_TECH_STEP_VALUE)
    @Description("{1}")
    public static String attachProducerMessageToAllure(String topic, Integer partition, String key, String message) {
        if (isLocal() || isLoggingEnabled()) {
            log.info(getLangValue("kafka.success.sent"), topic, partition, key, message);
        }

        return message;
    }

    @Attachment(value = "{0}.subscribe", type = "application/json")
    @Step(TEST_IT_ATTACHMENT_TECH_STEP_VALUE)
    @Description("{1}")
    public static String attachConsumerMessageToAllure(String topic, String message, int size) {
        if (isLocal() || isLoggingEnabled()) {
            if (size > 0) {
                String info = size > 1
                        ? getLangValue("kafka.success.read.many")
                        : getLangValue("kafka.success.read");

                log.info(info, topic, message);
            } else {
                log.info(getLangValue("kafka.end.read"), topic, message);
            }
        }

        return message;
    }
}
