package ru.origami.ibm_mq;

import lombok.extern.slf4j.Slf4j;
import ru.origami.ibm_mq.models.Queue;
import ru.origami.testit_allure.annotations.Step;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.fail;
import static ru.origami.common.environment.Language.getLangValue;
import static ru.origami.ibm_mq.attachment.IbmMqAttachment.attachBrowserConsumerMessageToAllure;

@Slf4j
public class IbmMqConsumer extends IbmMqBase {

    @Step("getLangValue:ibm.mq.step.consumer.read")
    public Message read(Queue queue) {
        Message message = null;

        try {
            MessageConsumer consumer = getConsumer(queue);

            log.info(getLangValue("ibm.mq.consumer.start.read"), queue.getQueue());
            int attempt = 0;

            do {
                attempt++;
                logAttempt(attempt);

                message = consumer.receive(TIMEOUT);
            } while (Objects.isNull(message) && attempt < MAX_ATTEMPTS);

            if (Objects.isNull(message)) {
                fail(getLangValue("ibm.mq.no.messages"));
            }

            attachBrowserConsumerMessageToAllure(queue.getQueue(), List.of(message));
        } catch (JMSException e) {
            closeSession(e, getLangValue("ibm.mq.read.fail"));
        }

        closeSession();

        return message;
    }
}
