package ru.origami.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.xml.bind.JAXB;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import ru.origami.common.OrigamiHelper;
import ru.origami.kafka.models.*;
import ru.origami.testit_allure.annotations.Step;

import java.io.IOException;
import java.io.StringReader;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.fail;
import static ru.origami.common.OrigamiHelper.waitInMillis;
import static ru.origami.common.environment.Environment.*;
import static ru.origami.common.environment.Language.getLangValue;
import static ru.origami.kafka.attachment.KafkaAttachment.attachConsumerMessageToAllure;
import static ru.origami.testit_allure.allure.java_commons.Allure.getLifecycle;

/**
 * Класс, реализующий шаги для продюсера в Кафке
 */
@Slf4j
public class ConsumerSteps extends CommonSteps {

    public static final Duration DURATION = Duration.ofMillis(500);

    public static final Duration DURATION_SECOND = Duration.ofMillis(2000);

    private static final long RETRY_DEFAULT_WAITING_TIME = 5000L;

    private static final int RETRY_DEFAULT_MAX_ATTEMPTS = 10;

    private static final long RETRY_DEFAULT_READ_TIMEOUT = 500;

    @Setter
    private Long retryWaitingTime = null;

    @Setter
    private Integer retryMaxAttempts = null;

    @Setter
    private Long retryReadTimeout = null;

    @Setter
    private Long period = null;

    private final SubscribeTopicTask subscribeTopicTask = new SubscribeTopicTask();

    private Timer timer = new Timer();

    public ConsumerSteps() {
        timer.schedule(subscribeTopicTask, 0, TimeUnit.HOURS.toMillis(10));
    }

    public ConsumerSteps setPartition(int partition) {
        neededPartitions.clear();
        neededPartitions.add(partition);

        return this;
    }

    public ConsumerSteps setPartitions(Integer... partitions) {
        neededPartitions.clear();
        neededPartitions.addAll(Arrays.asList(partitions));

        return this;
    }

    /**
     * Метод для вычитывания любого первого сообщения из топика
     *
     * @param topic название топика
     * @return Возвращается вычитанное сообщение в формате строки
     */
    public KafkaRecord<String> readFirst(Topic topic) {
        return readFirstBySearchWords(topic, emptyList(), false);
    }

    /**
     * Метод для вычитывания первого сообщения из топика
     *
     * @param topic      название топика
     * @param searchWord слово поиска
     * @return Возвращается вычитанное сообщение в формате строки
     */
    public KafkaRecord<String> readFirst(Topic topic, String searchWord) {
        return readFirstBySearchWords(topic, singletonList(searchWord), false);
    }

    /**
     * Метод для вычитывания первого сообщения из топика. Вернется первое сообщение по одному из переданных ключей
     *
     * @param topic       название топика
     * @param searchWords слова поиска
     * @return Возвращается вычитанное сообщение в формате строки
     */
    public KafkaRecord<String> readFirst(Topic topic, List<String> searchWords) {
        return readFirstBySearchWords(topic, searchWords, false);
    }

    /**
     * Метод для вычитывания любого первого сообщения из кафки с возможностью получения пустого результата
     *
     * @param topic название топика
     * @return В случае нахождения сообщения возвращается вычитанное сообщение в формате строки, иначе null
     */
    public KafkaRecord<String> readFirstWithEmptyResult(Topic topic) {
        return readFirstBySearchWords(topic, emptyList(), true);
    }

    /**
     * Метод для вычитывания первого сообщения из кафки с возможностью получения пустого результата
     *
     * @param topic      название топика
     * @param searchWord слово поиска
     * @return В случае нахождения сообщения возвращается вычитанное сообщение в формате строки, иначе null
     */
    public KafkaRecord<String> readFirstWithEmptyResult(Topic topic, String searchWord) {
        return readFirstBySearchWords(topic, singletonList(searchWord), true);
    }

    /**
     * Метод для вычитывания первого сообщения из кафки с возможностью получения пустого результата.
     * Вернется первое сообщение по одному из переданных ключей
     *
     * @param topic       название топика
     * @param searchWords слова поиска
     * @return В случае нахождения сообщения возвращается вычитанное сообщение в формате строки, иначе null
     */
    public KafkaRecord<String> readFirstWithEmptyResult(Topic topic, List<String> searchWords) {
        return readFirstBySearchWords(topic, searchWords, true);
    }

    /**
     * Метод для вычитывания любого первого сообщения из кафки в формате xml с возможностью получения пустого результата
     *
     * @param topic название топика
     * @param clazz тип для возвращаемого значения
     * @return В случае нахождения сообщения возвращается объект типа <Т>, иначе null
     */
    public <T> KafkaRecord<T> readFirstFromXmlWithEmptyResult(Topic topic, Class<T> clazz) {
        KafkaRecord<String> record = readFirstBySearchWords(topic, emptyList(), true);

        return Objects.isNull(record) ? null : new KafkaRecord<T>(record, OrigamiHelper.getObjectFromXml(record.getValue(), clazz));
    }

    /**
     * Метод для вычитывания первого сообщения из кафки в формате xml с возможностью получения пустого результата
     *
     * @param topic      название топика
     * @param searchWord слово поиска
     * @param clazz      тип для возвращаемого значения
     * @return В случае нахождения сообщения возвращается объект типа <Т>, иначе null
     */
    public <T> KafkaRecord<T> readFirstFromXmlWithEmptyResult(Topic topic, String searchWord, Class<T> clazz) {
        KafkaRecord<String> record = readFirstBySearchWords(topic, singletonList(searchWord), true);

        return Objects.isNull(record) ? null : new KafkaRecord<T>(record, OrigamiHelper.getObjectFromXml(record.getValue(), clazz));
    }

    /**
     * Метод для вычитывания первого сообщения из кафки в формате xml с возможностью получения пустого результата.
     * Вернется первое сообщение по одному из переданных ключей
     *
     * @param topic       название топика
     * @param searchWords слова поиска
     * @param clazz       тип для возвращаемого значения
     * @return В случае нахождения сообщения возвращается объект типа <Т>, иначе null
     */
    public <T> KafkaRecord<T> readFirstFromXmlWithEmptyResult(Topic topic, List<String> searchWords, Class<T> clazz) {
        KafkaRecord<String> record = readFirstBySearchWords(topic, searchWords, true);

        return Objects.isNull(record) ? null : new KafkaRecord<T>(record, OrigamiHelper.getObjectFromXml(record.getValue(), clazz));
    }

    /**
     * Метод для вычитывания любого первого сообщения из кафки в формате xml
     *
     * @param topic название топика
     * @param clazz тип для возвращаемого значения
     * @return Возвращается объект типа <Т>
     */
    public <T> KafkaRecord<T> readFirstFromXml(Topic topic, Class<T> clazz) {
        KafkaRecord<String> record = readFirstBySearchWords(topic, emptyList(), false);

        return Objects.isNull(record) ? null : new KafkaRecord<T>(record, OrigamiHelper.getObjectFromXml(record.getValue(), clazz));
    }

    /**
     * Метод для вычитывания первого сообщения из кафки в формате xml
     *
     * @param topic      название топика
     * @param searchWord слово поиска
     * @param clazz      тип для возвращаемого значения
     * @return Возвращается объект типа <Т>
     */
    public <T> KafkaRecord<T> readFirstFromXml(Topic topic, String searchWord, Class<T> clazz) {
        KafkaRecord<String> record = readFirstBySearchWords(topic, singletonList(searchWord), false);

        return Objects.isNull(record) ? null : new KafkaRecord<T>(record, OrigamiHelper.getObjectFromXml(record.getValue(), clazz));
    }

    /**
     * Метод для вычитывания первого сообщения из кафки в формате xml.
     * Вернется первое сообщение по одному из переданных ключей
     *
     * @param topic       название топика
     * @param searchWords слова поиска
     * @param clazz       тип для возвращаемого значения
     * @return Возвращается объект типа <Т>
     */
    public <T> KafkaRecord<T> readFirstFromXml(Topic topic, List<String> searchWords, Class<T> clazz) {
        KafkaRecord<String> record = readFirstBySearchWords(topic, searchWords, false);

        return Objects.isNull(record) ? null : new KafkaRecord<T>(record, OrigamiHelper.getObjectFromXml(record.getValue(), clazz));
    }

    /**
     * Метод для вычитывания любого первого сообщения из кафки в формате json с возможностью получения пустого результата
     *
     * @param topic название топика
     * @param clazz тип для возвращаемого значения
     * @return В случае нахождения сообщения возвращается объект типа <Т>, иначе null
     */
    public <T> KafkaRecord<T> readFirstFromJsonWithEmptyResult(Topic topic, Class<T> clazz) {
        KafkaRecord<String> record = readFirstBySearchWords(topic, emptyList(), true);

        return Objects.isNull(record) ? null : new KafkaRecord<T>(record, OrigamiHelper.getObjectFromJson(record.getValue(), clazz));
    }

    /**
     * Метод для вычитывания первого сообщения из кафки в формате json с возможностью получения пустого результата
     *
     * @param topic      название топика
     * @param searchWord слово поиска
     * @param clazz      тип для возвращаемого значения
     * @return В случае нахождения сообщения возвращается объект типа <Т>, иначе null
     */
    public <T> KafkaRecord<T> readFirstFromJsonWithEmptyResult(Topic topic, String searchWord, Class<T> clazz) {
        KafkaRecord<String> record = readFirstBySearchWords(topic, singletonList(searchWord), true);

        return Objects.isNull(record) ? null : new KafkaRecord<T>(record, OrigamiHelper.getObjectFromJson(record.getValue(), clazz));
    }

    /**
     * Метод для вычитывания первого сообщения из кафки в формате json с возможностью получения пустого результата.
     * Вернется первое сообщение по одному из переданных ключей
     *
     * @param topic       название топика
     * @param searchWords слова поиска
     * @param clazz       тип для возвращаемого значения
     * @return В случае нахождения сообщения возвращается объект типа <Т>, иначе null
     */
    public <T> KafkaRecord<T> readFirstFromJsonWithEmptyResult(Topic topic, List<String> searchWords, Class<T> clazz) {
        KafkaRecord<String> record = readFirstBySearchWords(topic, searchWords, true);

        return Objects.isNull(record) ? null : new KafkaRecord<T>(record, OrigamiHelper.getObjectFromJson(record.getValue(), clazz));
    }

    /**
     * Метод для вычитывания любого первого сообщения из кафки в формате json
     *
     * @param topic название топика
     * @param clazz тип для возвращаемого значения
     * @return Возвращается объект типа <Т>
     */
    public <T> KafkaRecord<T> readFirstFromJson(Topic topic, Class<T> clazz) {
        KafkaRecord<String> record = readFirstBySearchWords(topic, emptyList(), false);

        return Objects.isNull(record) ? null : new KafkaRecord<T>(record, OrigamiHelper.getObjectFromJson(record.getValue(), clazz));
    }

    /**
     * Метод для вычитывания первого сообщения из кафки в формате json
     *
     * @param topic      название топика
     * @param searchWord слово поиска
     * @param clazz      тип для возвращаемого значения
     * @return Возвращается объект типа <Т>
     */
    public <T> KafkaRecord<T> readFirstFromJson(Topic topic, String searchWord, Class<T> clazz) {
        KafkaRecord<String> record = readFirstBySearchWords(topic, singletonList(searchWord), false);

        return Objects.isNull(record) ? null : new KafkaRecord<T>(record, OrigamiHelper.getObjectFromJson(record.getValue(), clazz));
    }

    /**
     * Метод для вычитывания первого сообщения из кафки в формате json.
     * Вернется первое сообщение по одному из переданных ключей
     *
     * @param topic       название топика
     * @param searchWords слова поиска
     * @param clazz       тип для возвращаемого значения
     * @return Возвращается объект типа <Т>
     */
    public <T> KafkaRecord<T> readFirstFromJson(Topic topic, List<String> searchWords, Class<T> clazz) {
        KafkaRecord<String> record = readFirstBySearchWords(topic, searchWords, false);

        return Objects.isNull(record) ? null : new KafkaRecord<T>(record, OrigamiHelper.getObjectFromJson(record.getValue(), clazz));
    }

    private KafkaRecord<String> readFirstBySearchWords(Topic topic, List<String> searchWords, boolean withEmptyResult) {
        List<String> formattedSearchList = CollectionUtils.isEmpty(searchWords)
                ? emptyList()
                : searchWords.stream().filter(w -> Objects.nonNull(w) && !w.isEmpty()).toList();

        return readFirstBySearchWords(getTopicFullName(topic), formattedSearchList,
                formattedSearchList.stream().map("'%s'"::formatted).collect(Collectors.joining("; ")),
                withEmptyResult);
    }

    @Step("getLangValue:kafka.step.consumer.read.first")
    private KafkaRecord<String> readFirstBySearchWords(String topic, List<String> searchWords, String logValue, boolean withEmptyResult) {
        ConsumerConnection conn = subscribe(topic, true);
        KafkaRecord<String> neededRecord = null;
        int attempt = 0;

        do {
            attempt++;

            if (attempt > 1) {
                waitInMillis(getRetryReadTimeout());
            }

            logAttempt(attempt);

            List<ConsumerRecord<String, String>> records = readRecords(conn.getConsumer(), topic);
            Collections.reverse(records);

            for (ConsumerRecord<String, String> record : records) {
                if (Objects.nonNull(neededRecord)) {
                    break;
                }

                if (CollectionUtils.isEmpty(searchWords)) {
                    neededRecord = new KafkaRecord<>(record);
                } else {
                    if ((Objects.nonNull(record.key()) && searchWords.stream().anyMatch(k -> record.key().contains(k)))
                            || (Objects.nonNull(record.value()) && searchWords.stream().anyMatch(k -> record.value().contains(k)))) {
                        neededRecord = new KafkaRecord<>(record);
                    }
                }
            }
        } while (neededRecord == null && attempt < getRetryMaxAttempts());

        period = null;
        neededPartitions.clear();
        conn.getConsumer().close();

        if (neededRecord == null && !withEmptyResult) {
            fail(getLangValue("kafka.no.records").formatted(logValue));
        } else if (neededRecord == null) {
            attachConsumerMessageToAllure(topic, getLangValue("kafka.no.records").formatted(logValue), 0);

            return null;
        } else {
            attachConsumerMessageToAllure(topic, neededRecord.getFormattedRecord(), 1);
        }

        return neededRecord;
    }

    /**
     * Метод для вычитывания всех сообщений из топика кафки
     *
     * @param topic название топика
     * @return возвращается список строк
     */
    public List<KafkaRecord<String>> readAll(Topic topic) {
        return readAllBySearchWords(topic, emptyList(), false);
    }

    /**
     * Метод для вычитывания всех сообщений из топика кафки
     *
     * @param topic      название топика
     * @param searchWord слово поиска
     * @return возвращается список строк
     */
    public List<KafkaRecord<String>> readAll(Topic topic, String searchWord) {
        return readAllBySearchWords(topic, singletonList(searchWord), false);
    }

    /**
     * Метод для вычитывания всех сообщений из топика кафки
     *
     * @param topic       название топика
     * @param searchWords слова поиска
     * @return возвращается список строк
     */
    public List<KafkaRecord<String>> readAll(Topic topic, List<String> searchWords) {
        return readAllBySearchWords(topic, searchWords, false);
    }

    /**
     * Метод для вычитывания всех сообщений из compact топика кафки
     *
     * @param topic название compact топика
     * @return возвращается список строк
     */
    public List<KafkaRecord<String>> readAllCompact(Topic topic) {
        return getRecordsForCompactTopic(readAllBySearchWords(topic, emptyList(), false));
    }

    /**
     * Метод для вычитывания всех сообщений из compact топика кафки
     *
     * @param topic      название compact топика
     * @param searchWord слово поиска
     * @return возвращается список строк
     */
    public List<KafkaRecord<String>> readAllCompact(Topic topic, String searchWord) {
        return getRecordsForCompactTopic(readAllBySearchWords(topic, singletonList(searchWord), false));
    }

    /**
     * Метод для вычитывания всех сообщений из compact топика кафки
     *
     * @param topic       название compact топика
     * @param searchWords слова поиска
     * @return возвращается список строк
     */
    public List<KafkaRecord<String>> readAllCompact(Topic topic, List<String> searchWords) {
        return getRecordsForCompactTopic(readAllBySearchWords(topic, searchWords, false));
    }

    /**
     * Метод для вычитывания всех сообщений из топика кафки с возможностью получения пустого результата
     *
     * @param topic название топика
     * @return В случае нахождения сообщений возвращается список строк, иначе null
     */
    public List<KafkaRecord<String>> readAllWithEmptyResult(Topic topic) {
        return readAllBySearchWords(topic, emptyList(), true);
    }

    /**
     * Метод для вычитывания всех сообщений из топика кафки с возможностью получения пустого результата
     *
     * @param topic      название топика
     * @param searchWord слово поиска
     * @return В случае нахождения сообщений возвращается список строк, иначе null
     */
    public List<KafkaRecord<String>> readAllWithEmptyResult(Topic topic, String searchWord) {
        return readAllBySearchWords(topic, singletonList(searchWord), true);
    }

    /**
     * Метод для вычитывания всех сообщений из топика кафки с возможностью получения пустого результата
     *
     * @param topic       название топика
     * @param searchWords слова поиска
     * @return В случае нахождения сообщений возвращается список строк, иначе null
     */
    public List<KafkaRecord<String>> readAllWithEmptyResult(Topic topic, List<String> searchWords) {
        return readAllBySearchWords(topic, searchWords, true);
    }

    /**
     * Метод для вычитывания всех сообщений из compact топика кафки с возможностью получения пустого результата
     *
     * @param topic название compact топика
     * @return В случае нахождения сообщений возвращается список строк, иначе null
     */
    public List<KafkaRecord<String>> readAllCompactWithEmptyResult(Topic topic) {
        return getRecordsForCompactTopic(readAllBySearchWords(topic, emptyList(), true));
    }

    /**
     * Метод для вычитывания всех сообщений из compact топика кафки с возможностью получения пустого результата
     *
     * @param topic      название compact топика
     * @param searchWord слово поиска
     * @return В случае нахождения сообщений возвращается список строк, иначе null
     */
    public List<KafkaRecord<String>> readAllCompactWithEmptyResult(Topic topic, String searchWord) {
        return getRecordsForCompactTopic(readAllBySearchWords(topic, singletonList(searchWord), true));
    }

    /**
     * Метод для вычитывания всех сообщений из compact топика кафки с возможностью получения пустого результата
     *
     * @param topic       название compact топика
     * @param searchWords слова поиска
     * @return В случае нахождения сообщений возвращается список строк, иначе null
     */
    public List<KafkaRecord<String>> readAllCompactWithEmptyResult(Topic topic, List<String> searchWords) {
        return getRecordsForCompactTopic(readAllBySearchWords(topic, searchWords, true));
    }

    /**
     * Метод для вычитывания всех сообщений из топика кафки в формате xml
     *
     * @param topic название топика
     * @param clazz тип для возвращаемого значения
     * @return возвращается список строк
     */
    public <T> List<KafkaRecord<T>> readAllFromXml(Topic topic, Class<T> clazz) {
        List<KafkaRecord<String>> records = readAllBySearchWords(topic, emptyList(), false);

        return parseRecords(records, clazz, false);
    }

    /**
     * Метод для вычитывания всех сообщений из топика кафки в формате xml
     *
     * @param topic      название топика
     * @param searchWord слово поиска
     * @param clazz      тип для возвращаемого значения
     * @return возвращается список строк
     */
    public <T> List<KafkaRecord<T>> readAllFromXml(Topic topic, String searchWord, Class<T> clazz) {
        List<KafkaRecord<String>> records = readAllBySearchWords(topic, singletonList(searchWord), false);

        return parseRecords(records, clazz, false);
    }

    /**
     * Метод для вычитывания всех сообщений из топика кафки в формате xml
     *
     * @param topic       название топика
     * @param searchWords слова поиска
     * @param clazz       тип для возвращаемого значения
     * @return возвращается список строк
     */
    public <T> List<KafkaRecord<T>> readAllFromXml(Topic topic, List<String> searchWords, Class<T> clazz) {
        List<KafkaRecord<String>> records = readAllBySearchWords(topic, searchWords, false);

        return parseRecords(records, clazz, false);
    }

    /**
     * Метод для вычитывания всех сообщений из compact топика кафки в формате xml
     *
     * @param topic название compact топика
     * @param clazz тип для возвращаемого значения
     * @return возвращается список строк
     */
    public <T> List<KafkaRecord<T>> readAllCompactFromXml(Topic topic, Class<T> clazz) {
        List<KafkaRecord<String>> records = readAllBySearchWords(topic, emptyList(), false);

        return getRecordsForCompactTopic(parseRecords(records, clazz, false));
    }

    /**
     * Метод для вычитывания всех сообщений из compact топика кафки в формате xml
     *
     * @param topic      название compact топика
     * @param searchWord слово поиска
     * @param clazz      тип для возвращаемого значения
     * @return возвращается список строк
     */
    public <T> List<KafkaRecord<T>> readAllCompactFromXml(Topic topic, String searchWord, Class<T> clazz) {
        List<KafkaRecord<String>> records = readAllBySearchWords(topic, singletonList(searchWord), false);

        return getRecordsForCompactTopic(parseRecords(records, clazz, false));
    }

    /**
     * Метод для вычитывания всех сообщений из compact топика кафки в формате xml
     *
     * @param topic       название compact топика
     * @param searchWords слова поиска
     * @param clazz       тип для возвращаемого значения
     * @return возвращается список строк
     */
    public <T> List<KafkaRecord<T>> readAllCompactFromXml(Topic topic, List<String> searchWords, Class<T> clazz) {
        List<KafkaRecord<String>> records = readAllBySearchWords(topic, searchWords, false);

        return getRecordsForCompactTopic(parseRecords(records, clazz, false));
    }

    /**
     * Метод для вычитывания всех сообщений из топика кафки в формате xml с возможностью получения пустого результата
     *
     * @param topic название топика
     * @param clazz тип для возвращаемого значения
     * @return В случае нахождения сообщений возвращается список строк, иначе null
     */
    public <T> List<KafkaRecord<T>> readAllFromXmlWithEmptyResult(Topic topic, Class<T> clazz) {
        List<KafkaRecord<String>> records = readAllBySearchWords(topic, emptyList(), true);

        return parseRecords(records, clazz, false);
    }

    /**
     * Метод для вычитывания всех сообщений из топика кафки в формате xml с возможностью получения пустого результата
     *
     * @param topic      название топика
     * @param searchWord слово поиска
     * @param clazz      тип для возвращаемого значения
     * @return В случае нахождения сообщений возвращается список строк, иначе null
     */
    public <T> List<KafkaRecord<T>> readAllFromXmlWithEmptyResult(Topic topic, String searchWord, Class<T> clazz) {
        List<KafkaRecord<String>> records = readAllBySearchWords(topic, singletonList(searchWord), true);

        return parseRecords(records, clazz, false);
    }

    /**
     * Метод для вычитывания всех сообщений из топика кафки в формате xml с возможностью получения пустого результата
     *
     * @param topic       название топика
     * @param searchWords слова поиска
     * @param clazz       тип для возвращаемого значения
     * @return В случае нахождения сообщений возвращается список строк, иначе null
     */
    public <T> List<KafkaRecord<T>> readAllFromXmlWithEmptyResult(Topic topic, List<String> searchWords, Class<T> clazz) {
        List<KafkaRecord<String>> records = readAllBySearchWords(topic, searchWords, true);

        return parseRecords(records, clazz, false);
    }

    /**
     * Метод для вычитывания всех сообщений из compact топика кафки в формате xml с возможностью получения пустого результата
     *
     * @param topic название compact топика
     * @param clazz тип для возвращаемого значения
     * @return В случае нахождения сообщений возвращается список строк, иначе null
     */
    public <T> List<KafkaRecord<T>> readAllCompactFromXmlWithEmptyResult(Topic topic, Class<T> clazz) {
        List<KafkaRecord<String>> records = readAllBySearchWords(topic, emptyList(), true);

        return getRecordsForCompactTopic(parseRecords(records, clazz, false));
    }

    /**
     * Метод для вычитывания всех сообщений из compact топика кафки в формате xml с возможностью получения пустого результата
     *
     * @param topic      название compact топика
     * @param searchWord слово поиска
     * @param clazz      тип для возвращаемого значения
     * @return В случае нахождения сообщений возвращается список строк, иначе null
     */
    public <T> List<KafkaRecord<T>> readAllCompactFromXmlWithEmptyResult(Topic topic, String searchWord, Class<T> clazz) {
        List<KafkaRecord<String>> records = readAllBySearchWords(topic, singletonList(searchWord), true);

        return getRecordsForCompactTopic(parseRecords(records, clazz, false));
    }

    /**
     * Метод для вычитывания всех сообщений из compact топика кафки в формате xml с возможностью получения пустого результата
     *
     * @param topic       название compact топика
     * @param searchWords слова поиска
     * @param clazz       тип для возвращаемого значения
     * @return В случае нахождения сообщений возвращается список строк, иначе null
     */
    public <T> List<KafkaRecord<T>> readAllCompactFromXmlWithEmptyResult(Topic topic, List<String> searchWords, Class<T> clazz) {
        List<KafkaRecord<String>> records = readAllBySearchWords(topic, searchWords, true);

        return getRecordsForCompactTopic(parseRecords(records, clazz, false));
    }

    /**
     * Метод для вычитывания всех сообщений из топика кафки в формате json
     *
     * @param topic название топика
     * @param clazz тип для возвращаемого значения
     * @return возвращается список строк
     */
    public <T> List<KafkaRecord<T>> readAllFromJson(Topic topic, Class<T> clazz) {
        List<KafkaRecord<String>> records = readAllBySearchWords(topic, emptyList(), false);

        return parseRecords(records, clazz, true);
    }

    /**
     * Метод для вычитывания всех сообщений из топика кафки в формате json
     *
     * @param topic      название топика
     * @param searchWord слово поиска
     * @param clazz      тип для возвращаемого значения
     * @return возвращается список строк
     */
    public <T> List<KafkaRecord<T>> readAllFromJson(Topic topic, String searchWord, Class<T> clazz) {
        List<KafkaRecord<String>> records = readAllBySearchWords(topic, singletonList(searchWord), false);

        return parseRecords(records, clazz, true);
    }

    /**
     * Метод для вычитывания всех сообщений из топика кафки в формате json
     *
     * @param topic       название топика
     * @param searchWords слова поиска
     * @param clazz       тип для возвращаемого значения
     * @return возвращается список строк
     */
    public <T> List<KafkaRecord<T>> readAllFromJson(Topic topic, List<String> searchWords, Class<T> clazz) {
        List<KafkaRecord<String>> records = readAllBySearchWords(topic, searchWords, false);

        return parseRecords(records, clazz, true);
    }

    /**
     * Метод для вычитывания всех сообщений из compact топика кафки в формате json
     *
     * @param topic название compact топика
     * @param clazz тип для возвращаемого значения
     * @return возвращается список строк
     */
    public <T> List<KafkaRecord<T>> readAllCompactFromJson(Topic topic, Class<T> clazz) {
        List<KafkaRecord<String>> records = readAllBySearchWords(topic, emptyList(), false);

        return getRecordsForCompactTopic(parseRecords(records, clazz, true));
    }

    /**
     * Метод для вычитывания всех сообщений из compact топика кафки в формате json
     *
     * @param topic      название compact топика
     * @param searchWord слово поиска
     * @param clazz      тип для возвращаемого значения
     * @return возвращается список строк
     */
    public <T> List<KafkaRecord<T>> readAllCompactFromJson(Topic topic, String searchWord, Class<T> clazz) {
        List<KafkaRecord<String>> records = readAllBySearchWords(topic, singletonList(searchWord), false);

        return getRecordsForCompactTopic(parseRecords(records, clazz, true));
    }

    /**
     * Метод для вычитывания всех сообщений из compact топика кафки в формате json
     *
     * @param topic       название compact топика
     * @param searchWords слова поиска
     * @param clazz       тип для возвращаемого значения
     * @return возвращается список строк
     */
    public <T> List<KafkaRecord<T>> readAllCompactFromJson(Topic topic, List<String> searchWords, Class<T> clazz) {
        List<KafkaRecord<String>> records = readAllBySearchWords(topic, searchWords, false);

        return getRecordsForCompactTopic(parseRecords(records, clazz, true));
    }

    /**
     * Метод для вычитывания всех сообщений из топика кафки в формате json с возможностью получения пустого результата
     *
     * @param topic название топика
     * @param clazz тип для возвращаемого значения
     * @return В случае нахождения сообщений возвращается список строк, иначе null
     */
    public <T> List<KafkaRecord<T>> readAllFromJsonWithEmptyResult(Topic topic, Class<T> clazz) {
        List<KafkaRecord<String>> records = readAllBySearchWords(topic, emptyList(), true);

        return parseRecords(records, clazz, true);
    }

    /**
     * Метод для вычитывания всех сообщений из топика кафки в формате json с возможностью получения пустого результата
     *
     * @param topic      название топика
     * @param searchWord слово поиска
     * @param clazz      тип для возвращаемого значения
     * @return В случае нахождения сообщений возвращается список строк, иначе null
     */
    public <T> List<KafkaRecord<T>> readAllFromJsonWithEmptyResult(Topic topic, String searchWord, Class<T> clazz) {
        List<KafkaRecord<String>> records = readAllBySearchWords(topic, singletonList(searchWord), true);

        return parseRecords(records, clazz, true);
    }

    /**
     * Метод для вычитывания всех сообщений из топика кафки в формате json с возможностью получения пустого результата
     *
     * @param topic       название топика
     * @param searchWords слова поиска
     * @param clazz       тип для возвращаемого значения
     * @return В случае нахождения сообщений возвращается список строк, иначе null
     */
    public <T> List<KafkaRecord<T>> readAllFromJsonWithEmptyResult(Topic topic, List<String> searchWords, Class<T> clazz) {
        List<KafkaRecord<String>> records = readAllBySearchWords(topic, searchWords, true);

        return parseRecords(records, clazz, true);
    }

    /**
     * Метод для вычитывания всех сообщений из compact топика кафки в формате json с возможностью получения пустого результата
     *
     * @param topic название compact топика
     * @param clazz тип для возвращаемого значения
     * @return В случае нахождения сообщений возвращается список строк, иначе null
     */
    public <T> List<KafkaRecord<T>> readAllCompactFromJsonWithEmptyResult(Topic topic, Class<T> clazz) {
        List<KafkaRecord<String>> records = readAllBySearchWords(topic, emptyList(), true);

        return getRecordsForCompactTopic(parseRecords(records, clazz, true));
    }

    /**
     * Метод для вычитывания всех сообщений из compact топика кафки в формате json с возможностью получения пустого результата
     *
     * @param topic      название compact топика
     * @param searchWord слово поиска
     * @param clazz      тип для возвращаемого значения
     * @return В случае нахождения сообщений возвращается список строк, иначе null
     */
    public <T> List<KafkaRecord<T>> readAllCompactFromJsonWithEmptyResult(Topic topic, String searchWord, Class<T> clazz) {
        List<KafkaRecord<String>> records = readAllBySearchWords(topic, singletonList(searchWord), true);

        return getRecordsForCompactTopic(parseRecords(records, clazz, true));
    }

    /**
     * Метод для вычитывания всех сообщений из compact топика кафки в формате json с возможностью получения пустого результата
     *
     * @param topic       название compact топика
     * @param searchWords слова поиска
     * @param clazz       тип для возвращаемого значения
     * @return В случае нахождения сообщений возвращается список строк, иначе null
     */
    public <T> List<KafkaRecord<T>> readAllCompactFromJsonWithEmptyResult(Topic topic, List<String> searchWords, Class<T> clazz) {
        List<KafkaRecord<String>> records = readAllBySearchWords(topic, searchWords, true);

        return getRecordsForCompactTopic(parseRecords(records, clazz, true));
    }

    private List<KafkaRecord<String>> readAllBySearchWords(Topic topic, List<String> searchWords, boolean withEmptyResult) {
        List<String> formattedSearchList = CollectionUtils.isEmpty(searchWords)
                ? emptyList()
                : searchWords.stream().filter(w -> Objects.nonNull(w) && !w.isEmpty()).toList();

        return readAllBySearchWords(getTopicFullName(topic), formattedSearchList,
                formattedSearchList.stream().map("'%s'"::formatted).collect(Collectors.joining("; ")),
                withEmptyResult);
    }

    @Step("getLangValue:kafka.step.consumer.read.all")
    private List<KafkaRecord<String>> readAllBySearchWords(String topic, List<String> searchWords, String logValue, boolean withEmptyResult) {
        ConsumerConnection conn = subscribe(topic, true);
        List<KafkaRecord<String>> neededRecords = new ArrayList<>();
        int attempt = 0;

        do {
            attempt++;

            if (attempt > 1) {
                waitInMillis(getRetryReadTimeout());
            }

            logAttempt(attempt);

            for (ConsumerRecord<String, String> record : readRecords(conn.getConsumer(), topic)) {
                if (CollectionUtils.isEmpty(searchWords)) {
                    neededRecords.add(new KafkaRecord<>(record));
                } else {
                    if ((Objects.nonNull(record.key()) && searchWords.stream().anyMatch(k -> record.key().contains(k)))
                            || (Objects.nonNull(record.value()) && searchWords.stream().anyMatch(k -> record.value().contains(k)))) {
                        neededRecords.add(new KafkaRecord<>(record));
                    }
                }
            }
        } while (neededRecords.isEmpty() && attempt < getRetryMaxAttempts());

        period = null;
        neededPartitions.clear();
        conn.getConsumer().close();

        if (neededRecords.isEmpty() && !withEmptyResult) {
            fail(getLangValue("kafka.no.records").formatted(logValue));
        } else if (neededRecords.isEmpty()) {
            attachConsumerMessageToAllure(topic, getLangValue("kafka.no.records").formatted(logValue), 0);
        } else {
            String result = neededRecords.stream()
                    .map(KafkaRecord::getFormattedRecord)
                    .collect(Collectors.joining(",\n", "[\n", "\n]"))
                    .replaceAll("\n", "\n\t")
                    .replaceAll("\t]$", "]");

            attachConsumerMessageToAllure(topic, result, neededRecords.size());
        }

        return neededRecords;
    }

    private <T> List<KafkaRecord<T>> parseRecords(List<KafkaRecord<String>> records, Class<T> clazz, boolean isJson) {
        List<KafkaRecord<T>> objRecords = new ArrayList<>();
        List<String> errKeys = new ArrayList<>();
        List<KafkaObject<T>> errRecords = new ArrayList<>();

        for (KafkaRecord<String> record : records) {
            KafkaObject<T> currentObj = isJson ? getObjectFromJson(record.getValue(), clazz) : getObjectFromXml(record.getValue(), clazz);

            if (Objects.isNull(currentObj.getValue()) && Objects.nonNull(record.getValue())) {
                errKeys.add(Objects.isNull(record.getKey()) || record.getKey().isBlank()
                        ? "key: null, offset: %d".formatted(record.getOffset())
                        : record.getKey());
                errRecords.add(currentObj);
            } else {
                objRecords.add(new KafkaRecord<T>(record, currentObj.getValue()));
            }
        }

        if (!errRecords.isEmpty()) {
            log.info(getLangValue("kafka.not.parsed.records"), String.join("; ", errKeys));
            List<String> formattedErrors = errRecords.stream()
                    .map(o -> "%s\n".formatted(o.getException().getMessage()))
                    .distinct()
                    .collect(Collectors.toList());

            if (formattedErrors.size() > 3) {
                formattedErrors = formattedErrors.stream().map(e -> e.substring(0, 250)).toList();
            }

            log.info("{}", String.join("", formattedErrors));
        }

        return objRecords;
    }

    private void logAttempt(int attempt) {
        if (isLocal() || isLoggingEnabled()) {
            log.info(getLangValue("kafka.attempt"), attempt);
        }
    }

    @Deprecated
    private void rewriteStepName(Topic topic, String key) {
        getLifecycle().updateStep(getLifecycle().getCurrentTestCaseOrStep().get(),
                step -> step.setName(String.format("%s \"%s\"", step.getName().replaceAll("%topicName%", topic.getTopic()), key)));
    }

    private List<ConsumerRecord<String, String>> readRecords(Consumer<String, String> consumer, String topic) {
        long neededTimePeriod = Objects.isNull(period) ? 0 : Instant.now().toEpochMilli() - period;
        ConsumerRecords<String, String> consumerRecords = consumer.poll(DURATION);
        List<ConsumerRecord<String, String>> records = StreamSupport
                .stream(consumerRecords.records(topic).spliterator(), false)
                .collect(Collectors.toList());
        long countMessages = getNumberOfMessages(topic);
        long readStartTime = Instant.now().toEpochMilli();
        int emptyReadCount = 0;

        while (true) {
            int recordsCount = (int) records.stream()
                    .filter(r -> r.timestamp() < readStartTime)
                    .count();

            if (recordsCount < countMessages) {
                consumerRecords = consumer.poll(emptyReadCount > 5 ? DURATION_SECOND : DURATION);
                List<ConsumerRecord<String, String>> subRecords = StreamSupport
                        .stream(consumerRecords.records(topic).spliterator(), false)
                        .collect(Collectors.toList());
                records.addAll(subRecords);

                emptyReadCount = subRecords.isEmpty() ? ++emptyReadCount : 0;

                if (emptyReadCount == 10) {
                    break;
                }
            } else {
                break;
            }
        }

//        Set<TopicPartition> partitions = new HashSet<>(consumerRecords.partitions());
//        List<ConsumerRecord<String, String>> records = StreamSupport
//                .stream(consumerRecords.records(topic).spliterator(), false)
//                .collect(Collectors.toList());
//
//        while (true) {
//            consumerRecords = consumer.poll(DURATION);
//            List<ConsumerRecord<String, String>> subRecords =
//                    StreamSupport.stream(consumerRecords.records(topic).spliterator(), false).collect(Collectors.toList());
//            records.addAll(subRecords);
//
//            List<ConsumerRecord<String, String>> oldRecords = subRecords.stream()
//                    .filter(r -> r.timestamp() < readStartTime)
//                    .collect(Collectors.toList());
//
//            if (subRecords.isEmpty() || (partitions.containsAll(consumerRecords.partitions()) && oldRecords.isEmpty())) {
//                break;
//            }
//
//            partitions.addAll(consumerRecords.partitions());
//        }

        return records.stream()
                .filter(r -> r.timestamp() >= neededTimePeriod)
                .sorted(Comparator.comparing(ConsumerRecord::offset))
                .collect(Collectors.toList());
    }

    /**
     * Метод для подписки на топик с возможностью отписаться в нужный момент(unsubscribeAndGetResults, unsubscribeWhenGetMessage)
     *
     * @param topic название топика
     */
    public void subscribe(Topic topic) {
        subscribe(topic, null);
    }

    /**
     * Метод для подписки на топик с возможностью отписаться в нужный момент(unsubscribeAndGetResults, unsubscribeWhenGetMessage)
     *
     * @param topic название топика
     * @param clazz тип для возвращаемого значения при осуществлении отписки
     */
    @Step("getLangValue:kafka.step.consumer.subscribe")
    public void subscribe(Topic topic, Class clazz) {
        ConsumerConnection conn = subscribe(getTopicFullName(topic), false);
        this.subscribeTopicTask.addSubscribe(conn, clazz, getTopicFullName(topic));
    }

    /**
     * Метод для отписки от топика кафки с возможностью получения пустого списка результатов
     *
     * @param topic название топика
     * @return В случае нахождения сообщений возвращается список строк List<String>
     */
    public List unsubscribeAndGetResults(Topic topic) {
        return unsubscribeWhenGetResult(topic, null, emptyList(), 0, true);
    }

    /**
     * Метод для отписки от топика кафки с возможностью получения пустого списка результатов
     *
     * @param topic      название топика
     * @param searchWord слово поиска
     * @return В случае нахождения сообщений возвращается список строк List<String>
     */
    public List unsubscribeAndGetResults(Topic topic, String searchWord) {
        return unsubscribeWhenGetResult(topic, null, singletonList(searchWord), 0, true);
    }

    /**
     * Метод для отписки от топика кафки с возможностью получения пустого списка результатов
     *
     * @param topic       название топика
     * @param searchWords слова поиска
     * @return В случае нахождения сообщений возвращается список строк List<String>
     */
    public List unsubscribeAndGetResults(Topic topic, List<String> searchWords) {
        return unsubscribeWhenGetResult(topic, null, searchWords, 0, true);
    }

    /**
     * Метод для отписки от топика кафки и получения списка записей в формате json
     *
     * @param topic название топика
     * @return В случае нахождения сообщений возвращается список List<T> (класс переданный в subscribe)
     */
    public List unsubscribeAndGetResultsFromJson(Topic topic) {
        return unsubscribeWhenGetResult(topic, true, emptyList(), 0, true);
    }

    /**
     * Метод для отписки от топика кафки и получения списка записей в формате json
     *
     * @param topic      название топика
     * @param searchWord слово поиска
     * @return В случае нахождения сообщений возвращается список List<T> (класс переданный в subscribe)
     */
    public List unsubscribeAndGetResultsFromJson(Topic topic, String searchWord) {
        return unsubscribeWhenGetResult(topic, true, singletonList(searchWord), 0, true);
    }

    /**
     * Метод для отписки от топика кафки и получения списка записей в формате json
     *
     * @param topic       название топика
     * @param searchWords слова поиска
     * @return В случае нахождения сообщений возвращается список List<T> (класс переданный в subscribe)
     */
    public List unsubscribeAndGetResultsFromJson(Topic topic, List<String> searchWords) {
        return unsubscribeWhenGetResult(topic, true, searchWords, 0, true);
    }

    /**
     * Метод для отписки от топика кафки и получения списка записей в формате xml
     *
     * @param topic название топика
     * @return В случае нахождения сообщений возвращается список List<T> (класс переданный в subscribe)
     */
    public List unsubscribeAndGetResultsFromXml(Topic topic) {
        return unsubscribeWhenGetResult(topic, false, emptyList(), 0, true);
    }

    /**
     * Метод для отписки от топика кафки и получения списка записей в формате xml
     *
     * @param topic      название топика
     * @param searchWord слово поиска
     * @return В случае нахождения сообщений возвращается список List<T> (класс переданный в subscribe)
     */
    public List unsubscribeAndGetResultsFromXml(Topic topic, String searchWord) {
        return unsubscribeWhenGetResult(topic, false, singletonList(searchWord), 0, true);
    }

    /**
     * Метод для отписки от топика кафки и получения списка записей в формате xml
     *
     * @param topic       название топика
     * @param searchWords слова поиска
     * @return В случае нахождения сообщений возвращается список List<T> (класс переданный в subscribe)
     */
    public List unsubscribeAndGetResultsFromXml(Topic topic, List<String> searchWords) {
        return unsubscribeWhenGetResult(topic, false, searchWords, 0, true);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений
     * При неполучении сообщения в течении 5 сек тест будет провален
     *
     * @param topic название топика
     * @return В случае нахождения сообщений возвращается список строк List<String>
     */
    public List unsubscribeWhenGetMessage(Topic topic) {
        return unsubscribeWhenGetResult(topic, null, emptyList(), getRetryWaitingTime(), false);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений по заданному слову поиска
     * При неполучении сообщения в течении 5 сек тест будет провален
     *
     * @param topic      название топика
     * @param searchWord слово поиска
     * @return В случае нахождения сообщений возвращается список строк List<String>
     */
    public List unsubscribeWhenGetMessage(Topic topic, String searchWord) {
        return unsubscribeWhenGetResult(topic, null, singletonList(searchWord), getRetryWaitingTime(), false);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений по заданным словам поиска
     * При неполучении сообщения в течении 5 сек тест будет провален
     *
     * @param topic       название топика
     * @param searchWords слова поиска
     * @return В случае нахождения сообщений возвращается список строк List<String>
     */
    public List unsubscribeWhenGetMessage(Topic topic, List<String> searchWords) {
        return unsubscribeWhenGetResult(topic, null, searchWords, getRetryWaitingTime(), false);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений в формате json
     * При неполучении сообщения в течении 5 сек тест будет провален
     *
     * @param topic название топика
     * @return В случае нахождения сообщений возвращается список List<T> (класс переданный в subscribe)
     */
    public List unsubscribeWhenGetJsonMessage(Topic topic) {
        return unsubscribeWhenGetResult(topic, true, emptyList(), getRetryWaitingTime(), false);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений в формате json по заданному слову поиска
     * При неполучении сообщения в течении 5 сек тест будет провален
     *
     * @param topic      название топика
     * @param searchWord слово поиска
     * @return В случае нахождения сообщений возвращается список List<T> (класс переданный в subscribe)
     */
    public List unsubscribeWhenGetJsonMessage(Topic topic, String searchWord) {
        return unsubscribeWhenGetResult(topic, true, singletonList(searchWord), getRetryWaitingTime(), false);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений в формате json по заданным словам поиска
     * При неполучении сообщения в течении 5 сек тест будет провален
     *
     * @param topic       название топика
     * @param searchWords слова поиска
     * @return В случае нахождения сообщений возвращается список List<T> (класс переданный в subscribe)
     */
    public List unsubscribeWhenGetJsonMessage(Topic topic, List<String> searchWords) {
        return unsubscribeWhenGetResult(topic, true, searchWords, getRetryWaitingTime(), false);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений в формате xml
     * При неполучении сообщения в течении 5 сек тест будет провален
     *
     * @param topic название топика
     * @return В случае нахождения сообщений возвращается список List<T> (класс переданный в subscribe)
     */
    public List unsubscribeWhenGetXmlMessage(Topic topic) {
        return unsubscribeWhenGetResult(topic, false, emptyList(), getRetryWaitingTime(), false);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений в формате xml по заданному слову поиска
     * При неполучении сообщения в течении 5 сек тест будет провален
     *
     * @param topic      название топика
     * @param searchWord слово поиска
     * @return В случае нахождения сообщений возвращается список List<T> (класс переданный в subscribe)
     */
    public List unsubscribeWhenGetXmlMessage(Topic topic, String searchWord) {
        return unsubscribeWhenGetResult(topic, false, singletonList(searchWord), getRetryWaitingTime(), false);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений в формате xml по заданным словам поиска
     * При неполучении сообщения в течении 5 сек тест будет провален
     *
     * @param topic       название топика
     * @param searchWords слова поиска
     * @return В случае нахождения сообщений возвращается список List<T> (класс переданный в subscribe)
     */
    public List unsubscribeWhenGetXmlMessage(Topic topic, List<String> searchWords) {
        return unsubscribeWhenGetResult(topic, false, searchWords, getRetryWaitingTime(), false);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений
     * При неполучении сообщения в течении waitingTime (мс) тест будет провален
     *
     * @param topic       название топика
     * @param waitingTime максимальное время ожидания сообщения(мс)
     * @return В случае нахождения сообщений возвращается список строк List<String>
     */
    public List unsubscribeWhenGetMessage(Topic topic, long waitingTime) {
        return unsubscribeWhenGetResult(topic, null, emptyList(), waitingTime, false);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений по заданному слову поиска
     * При неполучении сообщения в течении waitingTime (мс) тест будет провален
     *
     * @param topic       название топика
     * @param searchWord  ключ сообщения
     * @param waitingTime максимальное время ожидания сообщения(мс)
     * @return В случае нахождения сообщений возвращается список строк List<String>
     */
    public List unsubscribeWhenGetMessage(Topic topic, String searchWord, long waitingTime) {
        return unsubscribeWhenGetResult(topic, null, singletonList(searchWord), waitingTime, false);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений по заданным словам поиска
     * При неполучении сообщения в течении waitingTime (мс) тест будет провален
     *
     * @param topic       название топика
     * @param searchWords слова поиска
     * @param waitingTime максимальное время ожидания сообщения(мс)
     * @return В случае нахождения сообщений возвращается список строк List<String>
     */
    public List unsubscribeWhenGetMessage(Topic topic, List<String> searchWords, long waitingTime) {
        return unsubscribeWhenGetResult(topic, null, searchWords, waitingTime, false);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений в формате json
     * При неполучении сообщения в течении waitingTime (мс) тест будет провален
     *
     * @param topic       название топика
     * @param waitingTime максимальное время ожидания сообщения(мс)
     * @return В случае нахождения сообщений возвращается список List<T> (класс переданный в subscribe)
     */
    public List unsubscribeWhenGetJsonMessage(Topic topic, long waitingTime) {
        return unsubscribeWhenGetResult(topic, true, emptyList(), waitingTime, false);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений в формате json по заданному слову поиска
     * При неполучении сообщения в течении waitingTime (мс) тест будет провален
     *
     * @param topic       название топика
     * @param searchWord  ключ сообщения
     * @param waitingTime максимальное время ожидания сообщения(мс)
     * @return В случае нахождения сообщений возвращается список List<T> (класс переданный в subscribe)
     */
    public List unsubscribeWhenGetJsonMessage(Topic topic, String searchWord, long waitingTime) {
        return unsubscribeWhenGetResult(topic, true, singletonList(searchWord), waitingTime, false);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений в формате json по заданным словам поиска
     * При неполучении сообщения в течении waitingTime (мс) тест будет провален
     *
     * @param topic       название топика
     * @param searchWords слова поиска
     * @param waitingTime максимальное время ожидания сообщения(мс)
     * @return В случае нахождения сообщений возвращается список List<T> (класс переданный в subscribe)
     */
    public List unsubscribeWhenGetJsonMessage(Topic topic, List<String> searchWords, long waitingTime) {
        return unsubscribeWhenGetResult(topic, true, searchWords, waitingTime, false);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений в формате xml
     * При неполучении сообщения в течении waitingTime (мс) тест будет провален
     *
     * @param topic       название топика
     * @param waitingTime максимальное время ожидания сообщения(мс)
     * @return В случае нахождения сообщений возвращается список List<T> (класс переданный в subscribe)
     */
    public List unsubscribeWhenGetXmlMessage(Topic topic, long waitingTime) {
        return unsubscribeWhenGetResult(topic, false, emptyList(), waitingTime, false);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений в формате xml по заданному слову поиска
     * При неполучении сообщения в течении waitingTime (мс) тест будет провален
     *
     * @param topic       название топика
     * @param searchWord  ключ сообщения
     * @param waitingTime максимальное время ожидания сообщения(мс)
     * @return В случае нахождения сообщений возвращается список List<T> (класс переданный в subscribe)
     */
    public List unsubscribeWhenGetXmlMessage(Topic topic, String searchWord, long waitingTime) {
        return unsubscribeWhenGetResult(topic, false, singletonList(searchWord), waitingTime, false);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений в формате xml по заданным словам поиска
     * При неполучении сообщения в течении waitingTime (мс) тест будет провален
     *
     * @param topic       название топика
     * @param searchWords слова поиска
     * @param waitingTime максимальное время ожидания сообщения(мс)
     * @return В случае нахождения сообщений возвращается список List<T> (класс переданный в subscribe)
     */
    public List unsubscribeWhenGetXmlMessage(Topic topic, List<String> searchWords, long waitingTime) {
        return unsubscribeWhenGetResult(topic, false, searchWords, waitingTime, false);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений
     * При неполучении сообщения в течении 5 сек тест не будет провален
     *
     * @param topic название топика
     * @return В случае нахождения сообщений возвращается список строк List<String>
     */
    public List unsubscribeWhenGetMessageWithEmptyResult(Topic topic) {
        return unsubscribeWhenGetResult(topic, null, emptyList(), getRetryWaitingTime(), true);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений по заданному слову поиска
     * При неполучении сообщения в течении 5 сек тест не будет провален
     *
     * @param topic      название топика
     * @param searchWord слово поиска
     * @return В случае нахождения сообщений возвращается список строк List<String>
     */
    public List unsubscribeWhenGetMessageWithEmptyResult(Topic topic, String searchWord) {
        return unsubscribeWhenGetResult(topic, null, singletonList(searchWord), getRetryWaitingTime(), true);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений по заданным словам поиска
     * При неполучении сообщения в течении 5 сек тест не будет провален
     *
     * @param topic       название топика
     * @param searchWords слова поиска
     * @return В случае нахождения сообщений возвращается список строк List<String>
     */
    public List unsubscribeWhenGetMessageWithEmptyResult(Topic topic, List<String> searchWords) {
        return unsubscribeWhenGetResult(topic, null, searchWords, getRetryWaitingTime(), true);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений в формате json
     * При неполучении сообщения в течении 5 сек тест не будет провален
     *
     * @param topic название топика
     * @return В случае нахождения сообщений возвращается список List<T> (класс переданный в subscribe)
     */
    public List unsubscribeWhenGetJsonMessageWithEmptyResult(Topic topic) {
        return unsubscribeWhenGetResult(topic, true, emptyList(), getRetryWaitingTime(), true);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений в формате json по заданному слову поиска
     * При неполучении сообщения в течении 5 сек тест не будет провален
     *
     * @param topic      название топика
     * @param searchWord слово поиска
     * @return В случае нахождения сообщений возвращается список List<T> (класс переданный в subscribe)
     */
    public List unsubscribeWhenGetJsonMessageWithEmptyResult(Topic topic, String searchWord) {
        return unsubscribeWhenGetResult(topic, true, singletonList(searchWord), getRetryWaitingTime(), true);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений в формате json по заданным словам поиска
     * При неполучении сообщения в течении 5 сек тест не будет провален
     *
     * @param topic       название топика
     * @param searchWords слова поиска
     * @return В случае нахождения сообщений возвращается список List<T> (класс переданный в subscribe)
     */
    public List unsubscribeWhenGetJsonMessageWithEmptyResult(Topic topic, List<String> searchWords) {
        return unsubscribeWhenGetResult(topic, true, searchWords, getRetryWaitingTime(), true);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений в формате xml
     * При неполучении сообщения в течении 5 сек тест не будет провален
     *
     * @param topic название топика
     * @return В случае нахождения сообщений возвращается список List<T> (класс переданный в subscribe)
     */
    public List unsubscribeWhenGetXmlMessageWithEmptyResult(Topic topic) {
        return unsubscribeWhenGetResult(topic, false, emptyList(), getRetryWaitingTime(), true);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений в формате xml по заданному слову поиска
     * При неполучении сообщения в течении 5 сек тест не будет провален
     *
     * @param topic      название топика
     * @param searchWord слово поиска
     * @return В случае нахождения сообщений возвращается список List<T> (класс переданный в subscribe)
     */
    public List unsubscribeWhenGetXmlMessageWithEmptyResult(Topic topic, String searchWord) {
        return unsubscribeWhenGetResult(topic, false, singletonList(searchWord), getRetryWaitingTime(), true);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений в формате xml по заданным словам поиска
     * При неполучении сообщения в течении 5 сек тест не будет провален
     *
     * @param topic       название топика
     * @param searchWords слова поиска
     * @return В случае нахождения сообщений возвращается список List<T> (класс переданный в subscribe)
     */
    public List unsubscribeWhenGetXmlMessageWithEmptyResult(Topic topic, List<String> searchWords) {
        return unsubscribeWhenGetResult(topic, false, searchWords, getRetryWaitingTime(), true);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений
     * При неполучении сообщения в течении waitingTime (мс) тест не будет провален
     *
     * @param topic       название топика
     * @param waitingTime максимальное время ожидания сообщения(мс)
     * @return В случае нахождения сообщений возвращается список строк List<String>
     */
    public List unsubscribeWhenGetMessageWithEmptyResult(Topic topic, long waitingTime) {
        return unsubscribeWhenGetResult(topic, null, emptyList(), waitingTime, true);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений по заданному слову поиска
     * При неполучении сообщения в течении waitingTime (мс) тест не будет провален
     *
     * @param topic       название топика
     * @param searchWord  ключ сообщения
     * @param waitingTime максимальное время ожидания сообщения(мс)
     * @return В случае нахождения сообщений возвращается список строк List<String>
     */
    public List unsubscribeWhenGetMessageWithEmptyResult(Topic topic, String searchWord, long waitingTime) {
        return unsubscribeWhenGetResult(topic, null, singletonList(searchWord), waitingTime, true);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений по заданным словам поиска
     * При неполучении сообщения в течении waitingTime (мс) тест не будет провален
     *
     * @param topic       название топика
     * @param searchWords слова поиска
     * @param waitingTime максимальное время ожидания сообщения(мс)
     * @return В случае нахождения сообщений возвращается список строк List<String>
     */
    public List unsubscribeWhenGetMessageWithEmptyResult(Topic topic, List<String> searchWords, long waitingTime) {
        return unsubscribeWhenGetResult(topic, null, searchWords, waitingTime, true);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений в формате json
     * При неполучении сообщения в течении waitingTime (мс) тест не будет провален
     *
     * @param topic       название топика
     * @param waitingTime максимальное время ожидания сообщения(мс)
     * @return В случае нахождения сообщений возвращается список List<T> (класс переданный в subscribe)
     */
    public List unsubscribeWhenGetJsonMessageWithEmptyResult(Topic topic, long waitingTime) {
        return unsubscribeWhenGetResult(topic, true, emptyList(), waitingTime, true);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений в формате json по заданному слову поиска
     * При неполучении сообщения в течении waitingTime (мс) тест не будет провален
     *
     * @param topic       название топика
     * @param searchWord  ключ сообщения
     * @param waitingTime максимальное время ожидания сообщения(мс)
     * @return В случае нахождения сообщений возвращается список List<T> (класс переданный в subscribe)
     */
    public List unsubscribeWhenGetJsonMessageWithEmptyResult(Topic topic, String searchWord, long waitingTime) {
        return unsubscribeWhenGetResult(topic, true, singletonList(searchWord), waitingTime, true);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений в формате json по заданным словам поиска
     * При неполучении сообщения в течении waitingTime (мс) тест не будет провален
     *
     * @param topic       название топика
     * @param searchWords слова поиска
     * @param waitingTime максимальное время ожидания сообщения(мс)
     * @return В случае нахождения сообщений возвращается список List<T> (класс переданный в subscribe)
     */
    public List unsubscribeWhenGetJsonMessageWithEmptyResult(Topic topic, List<String> searchWords, long waitingTime) {
        return unsubscribeWhenGetResult(topic, true, searchWords, waitingTime, true);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений в формате xml
     * При неполучении сообщения в течении waitingTime (мс) тест не будет провален
     *
     * @param topic       название топика
     * @param waitingTime максимальное время ожидания сообщения(мс)
     * @return В случае нахождения сообщений возвращается список List<T> (класс переданный в subscribe)
     */
    public List unsubscribeWhenGetXmlMessageWithEmptyResult(Topic topic, long waitingTime) {
        return unsubscribeWhenGetResult(topic, false, emptyList(), waitingTime, true);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений в формате xml по заданному слову поиска
     * При неполучении сообщения в течении waitingTime (мс) тест не будет провален
     *
     * @param topic       название топика
     * @param searchWord  ключ сообщения
     * @param waitingTime максимальное время ожидания сообщения(мс)
     * @return В случае нахождения сообщений возвращается список List<T> (класс переданный в subscribe)
     */
    public List unsubscribeWhenGetXmlMessageWithEmptyResult(Topic topic, String searchWord, long waitingTime) {
        return unsubscribeWhenGetResult(topic, false, singletonList(searchWord), waitingTime, true);
    }

    /**
     * Метод для отписки от топика кафки при получении сообщения или списка сообщений в формате xml по заданным словам поиска
     * При неполучении сообщения в течении waitingTime (мс) тест не будет провален
     *
     * @param topic       название топика
     * @param searchWords слова поиска
     * @param waitingTime максимальное время ожидания сообщения(мс)
     * @return В случае нахождения сообщений возвращается список List<T> (класс переданный в subscribe)
     */
    public List unsubscribeWhenGetXmlMessageWithEmptyResult(Topic topic, List<String> searchWords, long waitingTime) {
        return unsubscribeWhenGetResult(topic, false, searchWords, waitingTime, true);
    }

    private List unsubscribeWhenGetResult(Topic topic, Boolean isJson, List<String> searchWords, long waitingTime, boolean withEmptyResult) {
        List<String> formattedSearchList = CollectionUtils.isEmpty(searchWords)
                ? emptyList()
                : searchWords.stream().filter(w -> Objects.nonNull(w) && !w.isEmpty()).toList();

        return unsubscribeWhenGetResult(getTopicFullName(topic), isJson, formattedSearchList,
                formattedSearchList.stream().map("'%s'"::formatted).collect(Collectors.joining("; ")),
                waitingTime, withEmptyResult);
    }

    @Step("getLangValue:kafka.step.consumer.unsubscribe.after.key")
    private List unsubscribeWhenGetResult(String topic, Boolean isJson, List<String> searchWords, String logValue,
                                          long waitingTime, boolean withEmptyResult) {
        List<KafkaRecord<String>> records;
        boolean needUnsubscribed = false;
        long startTime = System.currentTimeMillis();
        SubscribeResult subscribeResult;
        List<String> notFoundedSearchWords = new ArrayList<>(searchWords);

        do {
            subscribeResult = this.subscribeTopicTask.unsubscribe(topic, needUnsubscribed).get(0);
            Stream<ConsumerRecord<String, String>> recordStream = List.copyOf(subscribeResult.getRecords()).stream();

            if (!CollectionUtils.isEmpty(searchWords)) {
                recordStream = recordStream.filter(r ->
                        (Objects.nonNull(r.key()) && searchWords.stream().anyMatch(k -> r.key().contains(k)))
                                || (Objects.nonNull(r.value()) && searchWords.stream().anyMatch(k -> r.value().contains(k))));
            }

            records = recordStream.map(KafkaRecord::new).collect(Collectors.toList());
            List<String> foundValues = Stream.concat(records.stream().map(KafkaRecord::getKey), records.stream().map(KafkaRecord::getValue))
                    .filter(Objects::nonNull)
                    .toList();

            notFoundedSearchWords = notFoundedSearchWords.stream()
                    .filter(w -> foundValues.stream().noneMatch(v -> v.contains(w)))
                    .toList();

            if (!records.isEmpty() && notFoundedSearchWords.isEmpty()) {
                needUnsubscribed = true;
                subscribeResult = this.subscribeTopicTask.unsubscribe(topic, needUnsubscribed).get(0);
            }
        } while (!needUnsubscribed && System.currentTimeMillis() - startTime < waitingTime);

        neededPartitions.clear();

        if (records.isEmpty()) {
            this.subscribeTopicTask.unsubscribe(topic, true);
            String message = getLangValue("kafka.no.records.while.subscribe").formatted(topic, logValue);

            attachConsumerMessageToAllure(topic, message, 0);

            if (!withEmptyResult) {
                fail(message);
            }
        } else {
            String result = records.stream()
                    .map(KafkaRecord::getFormattedRecord)
                    .collect(Collectors.joining(",\n", "[\n", "\n]"))
                    .replaceAll("\n", "\n\t")
                    .replaceAll("\t]$", "]");

            attachConsumerMessageToAllure(topic, result, records.size());

            if (!notFoundedSearchWords.isEmpty()){
                String notFoundedWords = notFoundedSearchWords.stream().map("'%s'"::formatted).collect(Collectors.joining("; "));
                String message = getLangValue("kafka.no.records.while.subscribe").formatted(topic, notFoundedWords);

                log.info(message);

                if (!withEmptyResult) {
                    fail(message);
                }
            }
        }

        if (Objects.isNull(subscribeResult.getMappingClass()) || Objects.isNull(isJson)) {
            return records;
        }

        return parseRecords(records, subscribeResult.getMappingClass(), isJson);
    }

    private static <T> List<KafkaRecord<T>> getRecordsForCompactTopic(List<KafkaRecord<T>> records) {
        Map<String, KafkaRecord<T>> mapRecords = new HashMap<>();

        for (KafkaRecord<T> record : records) {
            mapRecords.put(record.getKey(), record);
        }

        return new ArrayList<>(mapRecords.values()).stream()
                .filter(r -> Objects.nonNull(r.getValue()))
                .collect(Collectors.toList());
    }

    private static <T> KafkaObject<T> getObjectFromXml(String xml, Class<T> clazz) {
        KafkaObject<T> object = new KafkaObject<T>();

        if (xml == null) {
            return object;
        }

        String unescapedXml = xml.trim();
        Pattern pattern = Pattern.compile("^(<\\?xml[\\s\\w\\d=\".-]+\\?>)?(<.*>)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(unescapedXml);
        String cleanXml = null;

        if (matcher.find()) {
            cleanXml = matcher.group(2);
        }

        try {
            return object.setValue(JAXB.unmarshal(new StringReader(cleanXml), clazz));
        } catch (Exception e) {
            return object.setException(e);
        }
    }

    private static <T> KafkaObject<T> getObjectFromJson(String json, Class<T> clazz) {
        KafkaObject<T> object = new KafkaObject<T>();

        if (json == null) {
            return object;
        }

        try {
            return object.setValue(new ObjectMapper().registerModule(new JavaTimeModule())
                    .registerModule(new JodaModule())
                    .readValue(json.trim(), clazz));
        } catch (IOException e) {
            return object.setException(e);
        }
    }

    private long getRetryWaitingTime() {
        if (Objects.nonNull(retryWaitingTime)) {
            return retryWaitingTime;
        } else if (Objects.nonNull(properties.getRetryWaitingTime())) {
            return properties.getRetryWaitingTime();
        } else {
            return RETRY_DEFAULT_WAITING_TIME;
        }
    }

    private int getRetryMaxAttempts() {
        if (Objects.nonNull(retryMaxAttempts)) {
            return retryMaxAttempts;
        } else if (Objects.nonNull(properties.getRetryMaxAttempts())) {
            return properties.getRetryMaxAttempts();
        } else {
            return RETRY_DEFAULT_MAX_ATTEMPTS;
        }
    }

    private long getRetryReadTimeout() {
        if (Objects.nonNull(retryReadTimeout)) {
            return retryReadTimeout;
        } else if (Objects.nonNull(properties.getRetryReadTimeout())) {
            return properties.getRetryReadTimeout();
        } else {
            return RETRY_DEFAULT_READ_TIMEOUT;
        }
    }
}