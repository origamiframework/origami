package ru.origami.kafka.utils;

import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.fail;
import static ru.origami.common.environment.Language.getLangValue;

/**
 * Класс для подключения тестовых данных для Кафки
 */
public class TestDataKafka {

    private static final String KAFKA_TEST_DATA_PACKAGE = "kafka.test.data.package";
    private static final String KAFKA_TEST_DATA_DEFAULT_PACKAGE = "src/main/resources/test_data/kafka";

    @Getter
    private String value;

    private String fileName;

    public TestDataKafka(String fileName) {
        this.fileName = fileName;
        String pathToTestData = Objects.isNull(System.getProperty(KAFKA_TEST_DATA_PACKAGE))
                ? KAFKA_TEST_DATA_DEFAULT_PACKAGE
                : System.getProperty(KAFKA_TEST_DATA_PACKAGE);

        try {
            this.value = Files
                    .lines(Paths.get(String.format("%s/%s", pathToTestData, fileName)))
                    .reduce("", String::concat);
        } catch (IOException e) {
            fail(getLangValue("kafka.test.data.read.error").formatted(e.getMessage()));
        }
    }

    @Override
    public String toString() {
        return String.format("%s", fileName);
    }
}
