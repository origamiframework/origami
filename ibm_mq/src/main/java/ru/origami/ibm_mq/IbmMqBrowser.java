package ru.origami.ibm_mq;

import lombok.extern.slf4j.Slf4j;
import ru.origami.ibm_mq.models.Queue;
import ru.origami.testit_allure.annotations.Step;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.QueueBrowser;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;
import static ru.origami.common.environment.Language.getLangValue;
import static ru.origami.ibm_mq.attachment.IbmMqAttachment.attachBrowserConsumerMessageToAllure;

@Slf4j
public class IbmMqBrowser extends IbmMqBase {

    @Step("getLangValue:ibm.mq.step.browser.read")
    public List<Message> read(Queue queue) {
        List<Message> readMessages = new ArrayList<>();

        try {
            QueueBrowser browser = getBrowser(queue);
            Enumeration<?> messages = browser.getEnumeration();

            log.info(getLangValue("ibm.mq.browser.start.read"), queue.getQueue());
            int attempt = 0;

            do {
                attempt++;
                waitMessage(TIMEOUT);
                logAttempt(attempt);

                while (messages.hasMoreElements()) {
                    readMessages.add((Message) messages.nextElement());
                }
            } while (readMessages.isEmpty() && attempt < MAX_ATTEMPTS);

            if (readMessages.isEmpty()) {
                fail(getLangValue("ibm.mq.no.messages"));
            }

            attachBrowserConsumerMessageToAllure(queue.getQueue(), readMessages);
        } catch (JMSException e) {
            closeSession(e, getLangValue("ibm.mq.read.fail"));
        }

        closeSession();

        return readMessages;
    }

    private void waitMessage(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
