package ru.origami.kafka;

import org.apache.kafka.clients.producer.Producer;
import ru.origami.kafka.models.Topic;
import ru.origami.testit_allure.annotations.Step;
import org.apache.kafka.common.header.Header;

import java.util.List;

import static ru.origami.common.OrigamiHelper.getObjectAsJsonString;
import static ru.origami.testit_allure.allure.java_commons.Allure.getLifecycle;

/**
 * Класс, реализующий шаги для продюсера в Кафке
 */
public class ProducerSteps extends CommonSteps {

    public ProducerSteps setPartition(int partition) {
        neededPartitions.clear();
        neededPartitions.add(partition);

        return this;
    }

    /**
     * Метод для отправки сообщения в Кафку
     * @param topic название топика
     * @param message сообщение
     */
    public void sendMessage(Topic topic, String message) {
        sendMessage(topic, null, message, null);
    }

    /**
     * Метод для отправки сообщения в Кафку
     * @param topic название топика
     * @param message сообщение
     * @param headers список заголовков
     */
    public void sendMessage(Topic topic, String message, List<Header> headers) {
        sendMessage(topic, null, message, headers);
    }

    /**
     * Метод для отправки сообщения в Кафку
     * @param topic название топика
     * @param key ключ сообщения
     * @param message сообщение, которое отправляем в кафку
     */
    public void sendMessage(Topic topic, String key, String message) {
        sendMessage(getTopicFullName(topic), key, message, null);
    }

    /**
     * Метод для отправки сообщения в Кафку
     * @param topic название топика
     * @param key ключ сообщения
     * @param message сообщение, которое отправляем в кафку
     * @param headers список заголовков
     */
    public void sendMessage(Topic topic, String key, String message, List<Header> headers) {
        sendMessage(getTopicFullName(topic), key, message, headers);
    }

    /**
     * Метод для отправки сообщения в Кафку
     * @param topic название топика
     * @param message объект, который будет отправлен в кафку в формате json
     */
    public void sendMessageAsJson(Topic topic, Object message) {
        sendMessageAsJson(topic, null, message, null);
    }

    /**
     * Метод для отправки сообщения в Кафку
     * @param topic название топика
     * @param message объект, который будет отправлен в кафку в формате json
     * @param headers список заголовков
     */
    public void sendMessageAsJson(Topic topic, Object message, List<Header> headers) {
        sendMessageAsJson(topic, null, message, headers);
    }

    /**
     * Метод для отправки сообщения в Кафку
     * @param topic название топика
     * @param key ключ сообщения
     * @param message объект, который будет отправлен в кафку в формате json
     */
    public void sendMessageAsJson(Topic topic, String key, Object message) {
        sendMessage(getTopicFullName(topic), key, getObjectAsJsonString(message), null);
    }

    /**
     * Метод для отправки сообщения в Кафку
     * @param topic название топика
     * @param key ключ сообщения
     * @param message объект, который будет отправлен в кафку в формате json
     * @param headers список заголовков
     */
    public void sendMessageAsJson(Topic topic, String key, Object message, List<Header> headers) {
        sendMessage(getTopicFullName(topic), key, getObjectAsJsonString(message), headers);
    }

    @Step("getLangValue:kafka.step.producer.send")
    private void sendMessage(String topic, String key, String message, List<Header> headers) {
        Producer<String, String> producer = getProducer();
        send(producer, topic, key, message, headers);
        producer.close();
    }

    @Deprecated
    private void rewriteStepName(Topic topic) {
        getLifecycle().updateStep(getLifecycle().getCurrentTestCaseOrStep().get(),
                step -> step.setName(String.format("%s %s", step.getName(), topic.getTopic())));
    }
}
