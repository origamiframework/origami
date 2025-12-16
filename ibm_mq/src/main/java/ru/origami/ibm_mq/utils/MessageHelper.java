package ru.origami.ibm_mq.utils;

import javax.jms.*;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.fail;
import static ru.origami.common.environment.Language.getLangValue;

public class MessageHelper {

    public static String getMessageBody(Message message) {
        try {
            if (message instanceof BytesMessage) {
                BytesMessage byteMessage = (BytesMessage) message;
                byte[] byteData = new byte[(int) byteMessage.getBodyLength()];
                byteMessage.readBytes(byteData);
                byteMessage.reset();

                return new String(byteData);
            } else if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;

                return textMessage.getText();
            } else if (message instanceof MapMessage) {
                MapMessage mapMessage = (MapMessage) message;
                Enumeration<?> names = mapMessage.getMapNames();
                Map<String, Object> map = new HashMap<>();

                while (names.hasMoreElements()) {
                    String name = (String) names.nextElement();
                    map.put(name, mapMessage.getObject(name));
                }

                return map.toString();
            } else if (message instanceof ObjectMessage) {
                Object obj = ((ObjectMessage) message).getObject();

                return String.valueOf(obj);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(getLangValue("ibm.mq.convert.error"));
        }

        fail(getLangValue("ibm.mq.convert.not.supported.error").formatted(message.getClass().getName()));

        return null;
    }
}
