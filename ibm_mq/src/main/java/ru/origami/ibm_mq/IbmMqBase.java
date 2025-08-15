package ru.origami.ibm_mq;

import com.ibm.msg.client.jms.JmsConnectionFactory;
import com.ibm.msg.client.jms.JmsFactoryFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import lombok.extern.slf4j.Slf4j;
import ru.origami.ibm_mq.models.Properties;

import javax.jms.*;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.fail;
import static ru.origami.common.environment.Environment.*;
import static ru.origami.common.environment.Language.getLangValue;

@Slf4j
public abstract class IbmMqBase {

    protected Properties properties;

    protected final int TIMEOUT = 1000;

    protected final int MAX_ATTEMPTS = 10;

    protected Session session = null;

    private Queue destination = null;

    private Connection connection  = null;

    private void createConnection() {
        if (properties == null) {
            fail(getLangValue("ibm.mq.props.null"));
        }

        try {
            JmsFactoryFactory ff = JmsFactoryFactory.getInstance(WMQConstants.WMQ_PROVIDER);
            JmsConnectionFactory cf = ff.createConnectionFactory();

            cf.setStringProperty(WMQConstants.WMQ_HOST_NAME, properties.getHost());
            cf.setIntProperty(WMQConstants.WMQ_PORT, properties.getPort());
            cf.setStringProperty(WMQConstants.WMQ_QUEUE_MANAGER, properties.getQueueManagerName());
            cf.setStringProperty(WMQConstants.WMQ_CHANNEL, properties.getChannel());
            cf.setStringProperty(WMQConstants.USERID, properties.getUsername());
            cf.setStringProperty(WMQConstants.PASSWORD, properties.getPassword());
            cf.setIntProperty(WMQConstants.WMQ_CONNECTION_MODE, properties.isClientTransport() ? WMQConstants.WMQ_CM_CLIENT : WMQConstants.WMQ_CM_BINDINGS);

            this.connection = cf.createConnection();
        } catch (JMSException jmsex) {
            closeSession(jmsex, getLangValue("ibm.mq.connect.error"));
        }
    }

    private void createSession(ru.origami.ibm_mq.models.Queue queue) {
        try {
            this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            this.destination = session.createQueue(queue.getQueue());
            this.connection.start();
        } catch (JMSException jmsex) {
            closeSession(jmsex, getLangValue("ibm.mq.connect.error"));
        }
    }

    protected void closeSession() {
        closeSession(null, null);
    }

    protected void closeSession(Exception ex, String exMessage) {
        if (Objects.nonNull(ex)) {
            ex.printStackTrace();
        }

        try {
            if (Objects.nonNull(session)) {
                session.close();
            }

            if (Objects.nonNull(connection)) {
                connection.close();
            }
        } catch (JMSException jmsex) {
            jmsex.printStackTrace();
            fail(getLangValue("ibm.mq.close.conn.error").formatted(Objects.nonNull(ex) ? ex.getMessage() : jmsex.getMessage()));
        }

        if (Objects.nonNull(ex)) {
            fail(String.format("%s\n\t%s\n", exMessage, ex.getMessage()));
        }
    }

    protected void logAttempt(int attempt) {
        if (isLocal() || isLoggingEnabled()) {
            log.info(getLangValue("ibm.mq.attempt"), attempt);
        }
    }

    protected QueueBrowser getBrowser(ru.origami.ibm_mq.models.Queue queue) {
        QueueBrowser browser = null;

        try {
            createConnection();
            createSession(queue);
            browser = session.createBrowser(destination);
        } catch (Exception e) {
            closeSession(e, getLangValue("ibm.mq.create.browser.error"));
        }

        return browser;
    }

    protected MessageConsumer getConsumer(ru.origami.ibm_mq.models.Queue queue) {
        MessageConsumer consumer = null;

        try {
            createConnection();
            createSession(queue);
            consumer = session.createConsumer(destination);
        } catch (Exception e) {
            closeSession(e, getLangValue("ibm.mq.create.consumer.error"));
        }

        return consumer;
    }

    protected MessageProducer getProducer(ru.origami.ibm_mq.models.Queue queue) {
        MessageProducer producer = null;

        try {
            createConnection();
            createSession(queue);
            producer = session.createProducer(destination);
        } catch (Exception e) {
            closeSession(e, getLangValue("ibm.mq.create.producer.error"));
        }

        return producer;
    }
}
