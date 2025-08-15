package ru.origami.ibm_mq.utils;

import javax.jms.BytesMessage;
import javax.jms.Message;

import static org.junit.jupiter.api.Assertions.fail;
import static ru.origami.common.environment.Language.getLangValue;

public class MessageHelper {

    public static String getMessageBodyFromBytes(Message message) {
        try {
            if (message instanceof BytesMessage) {
                BytesMessage byteMessage = (BytesMessage) message;
                byte[] byteData = new byte[(int) byteMessage.getBodyLength()];
                byteMessage.readBytes(byteData);
                byteMessage.reset();

                return new String(byteData);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            fail(getLangValue("ibm.mq.convert.bytes.error"));
        }

        return null;
    }
}
