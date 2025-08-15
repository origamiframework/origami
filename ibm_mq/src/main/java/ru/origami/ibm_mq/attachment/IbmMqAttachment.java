package ru.origami.ibm_mq.attachment;

import lombok.extern.slf4j.Slf4j;
import ru.origami.testit_allure.allure.java_commons.Attachment;
import ru.origami.testit_allure.annotations.Description;
import ru.origami.testit_allure.annotations.Step;

import javax.jms.Message;
import java.util.List;
import java.util.stream.Collectors;

import static ru.origami.common.environment.Environment.*;
import static ru.origami.common.environment.Language.getLangValue;
import static ru.origami.testit_allure.test_it.testit.aspects.StepAspect.TEST_IT_ATTACHMENT_TECH_STEP_VALUE;

@Slf4j
public class IbmMqAttachment {

    @Attachment(value = "{0}.send")
    @Step(TEST_IT_ATTACHMENT_TECH_STEP_VALUE)
    @Description("{1}")
    public static String attachProducerMessageToAllure(String queue, String message) {
        if (isLocal() || isLoggingEnabled()) {
            log.info(getLangValue("ibm.mq.success.sent"), queue, message);
        }

        return message;
    }

    @Attachment(value = "{0}.subscribe")
    public static String attachBrowserConsumerMessageToAllure(String queue, List<Message> messages) {
        String result = messages.stream()
                .map(Message::toString)
                .collect(Collectors.joining("\n\n"));
        attachMessagesToTestIT(result);

        if (isLocal() || isLoggingEnabled()) {
            if (messages.size() > 0) {
                String info = messages.size() > 1
                        ? getLangValue("ibm.mq.success.read.many")
                        : getLangValue("ibm.mq.success.read");
                log.info(info, queue, result);
            } else {
                log.info(getLangValue("ibm.mq.end.read"), queue);
            }
        }

        return result;
    }

    @Step(TEST_IT_ATTACHMENT_TECH_STEP_VALUE)
    @Description("{0}")
    private static void attachMessagesToTestIT(String messages) {
    }
}
