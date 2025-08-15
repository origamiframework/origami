package ru.origami.ibm_mq;

import lombok.extern.slf4j.Slf4j;
import ru.origami.ibm_mq.models.Queue;
import ru.origami.testit_allure.annotations.Step;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import static ru.origami.common.environment.Language.getLangValue;
import static ru.origami.ibm_mq.attachment.IbmMqAttachment.attachProducerMessageToAllure;

@Slf4j
public class IbmMqProducer extends IbmMqBase {

    @Step("getLangValue:ibm.mq.step.producer.send")
    public void send(Queue queue, String messageStr) {
        try {
            MessageProducer producer = getProducer(queue);
            log.info(getLangValue("ibm.mq.producer.start.sending"), queue.getQueue());
            TextMessage message = session.createTextMessage(messageStr);
            producer.send(message);

            attachProducerMessageToAllure(queue.getQueue(), messageStr);
        } catch (JMSException e) {
            closeSession(e, getLangValue("ibm.mq.producer.error.sending"));
        }

        closeSession();
    }
}
