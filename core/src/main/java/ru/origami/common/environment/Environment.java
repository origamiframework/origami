package ru.origami.common.environment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import ru.origami.common.utils.SslVerification;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.junit.jupiter.api.Assertions.fail;
import static ru.origami.common.environment.Language.*;

@Slf4j
public final class Environment {

    private static final Properties PROPERTIES = new Properties();
    public static final String ORIGAMI_PROPERTIES_FILE = "origami.properties";
    public static final String STAND = "stand";
    private static final String LOGGING_ENABLED = "logging.enabled";
    private static final String HIBERNATE_EXCEL_RESULT_ENABLED = "hibernate.excel.result.enabled";
    private static final String TEST_TIMEZONE = "test.timezone";
    private static final String SSL_TRUSTSTORE_LOCATION = "global.ssl.trust.store.location";
    private static final String SSL_TRUSTSTORE_PASSWORD = "global.ssl.trust.store.password";
    private static final String CUSTOM_PROPERTIES_FILE_MASK = "custom.properties.file.path";
    public static final String SSL_VERIFICATION = "disable.ssl.verification";
    private static final String ALLURE_LINK_ISSUE_PATTERN = "allure.link.issue.pattern";

    static {
        loadOrigamiProperties();
        loadLanguageProperties();
        loadCustomProperties();
        loadSystemProperties();
        setSystemProperties();
        loadStandProperties();
        loadRoutsProperties();
        loadAnyProperties();
        setTimeZone();
        setSslTrustStore();
        disableSslVerification();
        setAllureProperties();
    }

    public static String get(String key) {
        return getPropertyValue(key, false);
    }

    public static int getInt(String key) {
        return Integer.parseInt(getPropertyValue(key, false));
    }

    public static String getWithNullValue(String key) {
        return getPropertyValue(key, true);
    }

    public static Integer getIntWithNullValue(String key) {
        String value = getPropertyValue(key, true);

        if (Objects.nonNull(value)) {
            return Integer.parseInt(value);
        }

        return null;
    }

    private static String getPropertyValue(String key, boolean withNullValue) {
        String propertyValue = null;

        try {
            propertyValue = new String(PROPERTIES.getProperty(key).getBytes(StandardCharsets.UTF_8));
        } catch (NullPointerException npe) {
            if (!withNullValue) {
                fail(getLangValue("env.missing.property").formatted(key));
            }
        }

        return propertyValue;
    }

    public static boolean isLocal() {
        return System.getProperty("is.local") == null || Boolean.parseBoolean(System.getProperty("is.local"));
    }

    public static boolean isLoggingEnabled() {
        return getWithNullValue(LOGGING_ENABLED) != null && Boolean.parseBoolean(get(LOGGING_ENABLED));
    }

    public static boolean isExcelResultEnabled() {
        return getWithNullValue(HIBERNATE_EXCEL_RESULT_ENABLED) != null
                && Boolean.parseBoolean(get(HIBERNATE_EXCEL_RESULT_ENABLED));
    }

    private static String getLocalEnvironment() {
        String systemStand = System.getProperty(STAND);

        if (Objects.isNull(getWithNullValue(STAND)) && Objects.isNull(systemStand)) {
            fail(getLangValue("env.missing.stand.value").formatted(STAND));
        }

        return String.format("config/%s.json", Objects.isNull(systemStand) ? get(STAND) : systemStand);
    }

    private static void loadOrigamiProperties() {
        try (InputStream inputStream = ClassLoader.getSystemClassLoader().getResourceAsStream(ORIGAMI_PROPERTIES_FILE)) {
            if (inputStream == null) {
                fail("Отсутствует файл конфигурации \"%s\"".formatted(ORIGAMI_PROPERTIES_FILE));
            } else if (inputStream.available() == 0) {
                log.info("Пустой файл конфигурации \"{}\"", ORIGAMI_PROPERTIES_FILE);
            } else {
                PROPERTIES.load(inputStream);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadLanguageProperties() {
        List<String> langs = List.of(DEFAULT_LANGUAGE, "en");
        String languageFile = Objects.isNull(System.getProperty(LANGUAGE))
                ? Environment.getWithNullValue(LANGUAGE)
                : System.getProperty(LANGUAGE);

        if (Objects.isNull(languageFile)) {
            languageFile = DEFAULT_LANGUAGE;
        }

        languageFile = languageFile.toLowerCase();

        if (!langs.contains(languageFile)) {
            log.info("Отсутствует локализация \"{}\". Установлена локализация по умолчанию \"{}\"", languageFile, DEFAULT_LANGUAGE);
            languageFile = DEFAULT_LANGUAGE;
        }

        try (InputStream inputStream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream("%s/%s.properties".formatted(LANGUAGE, languageFile))) {
            Language.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadSystemProperties() {
        String systemStand = System.getProperty(STAND);

        if (Objects.nonNull(systemStand)) {
            PROPERTIES.put(STAND, systemStand);
        }
    }

    private static void loadStandProperties() {
        String environment = getLocalEnvironment();

        PROPERTIES.putAll(loadPropertiesByStringPath(environment));

//        try (InputStream inputStream = new FileInputStream(environment)) {
//            if (inputStream.available() == 0) {
//                log.info(getLangValue("env.empty.config.file"), environment.substring(environment.lastIndexOf("/") + 1));
//            } else {
//                PROPERTIES.putAll(getProperties(getJsonNode(inputStream), null));
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * Загрузка пользовательских файлов конфигурации
     */
    private static void loadCustomProperties() {
        Properties customProperties = new Properties();

        for (String propName : PROPERTIES.stringPropertyNames()) {
            if (propName.startsWith(CUSTOM_PROPERTIES_FILE_MASK)) {
                customProperties.putAll(loadPropertiesByStringPath(get(propName)));
                log.info(getLangValue("env.custom.properties.loaded"), get(propName));
            }
        }

        PROPERTIES.putAll(customProperties);
    }

    /**
     * routs содержит исключительно роуты
     */
    private static void loadRoutsProperties() {
        PROPERTIES.putAll(loadPropertiesByStringPath("statics/routs.json"));
    }

    /**
     * any - все что не роут и не зависит от окружения стенда: текст, константы, таймауты ожиданий и тд
     */
    private static void loadAnyProperties() {
        PROPERTIES.putAll(loadPropertiesByStringPath("statics/any.json"));
    }

    private static Properties loadPropertiesByStringPath(String path) {
        Properties prop = new Properties();
        String logPath = path.startsWith("config") ? path.substring(path.lastIndexOf("/") + 1) : path;

        try (InputStream resourceStream = Environment.class.getClassLoader().getResourceAsStream(path)) {
            if (Objects.isNull(resourceStream)) {
                if (path.startsWith("config")) {
                    fail(getLangValue("env.fail.not.found.config.file").formatted(logPath));
                } else {
                    log.info(getLangValue("env.not.found.config.file"), logPath);
                }
            } else if (resourceStream.available() == 0) {
                log.info(getLangValue("env.empty.config.file"), logPath);
            } else {
                prop.putAll(getProperties(getJsonNode(resourceStream), null));
            }
        } catch (IOException e) {
            fail(getLangValue("env.fail.load.config.file").formatted(logPath));
        }

        return prop;
    }

    private static Properties getProperties(JsonNode node, String prefix) {
        Properties prop = new Properties();

        if (Objects.nonNull(node)) {
            if (node.isArray()) {
                int i = 0;

                for (JsonNode arrayElement : node) {
                    prop.putAll(getProperties(arrayElement, prefix + "[" + i++ + "]"));
                }
            } else if (node.isObject()) {
                Iterator<String> fieldNames = node.fieldNames();
                String curPrefixWithDot = (prefix == null || prefix.trim().isEmpty()) ? "" : String.format("%s.", prefix);

                while (fieldNames.hasNext()) {
                    String fieldName = fieldNames.next();
                    JsonNode fieldValue = node.get(fieldName);
                    prop.putAll(getProperties(fieldValue, curPrefixWithDot + fieldName));
                }
            } else {
                prop.put(prefix, node.asText());
            }
        }

        return prop;
    }

    private static JsonNode getJsonNode(InputStream fis) {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(fis, StandardCharsets.UTF_8))) {
            if (fis.available() > 0) {
                StringBuilder sb = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }

                return getJsonNode(sb.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static JsonNode getJsonNode(String json) {
        try {
            return new ObjectMapper().readTree(json);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static void setSystemProperties() {
        for (String prop : PROPERTIES.stringPropertyNames()) {
            if (Objects.isNull(System.getProperty(prop))) {
                System.setProperty(prop, getPropertyValue(prop, false));
            }
        }
    }

    private static void setTimeZone() {
        if (Objects.nonNull(getWithNullValue(TEST_TIMEZONE))) {
            System.setProperty("user.timezone", get(TEST_TIMEZONE));
            TimeZone.setDefault(TimeZone.getTimeZone(get(TEST_TIMEZONE)));
        }
    }

    private static void setSslTrustStore() {
        if (Objects.nonNull(getWithNullValue(SSL_TRUSTSTORE_LOCATION))) {
            try {
                File file = new File(get(SSL_TRUSTSTORE_LOCATION));

                if (!file.exists()) {
                    URL fileUrl = Thread.currentThread().getContextClassLoader().getResource(get(SSL_TRUSTSTORE_LOCATION));

                    if (Objects.nonNull(fileUrl)) {
                        file = new File(fileUrl.toURI());
                    } else {
                        fail(getLangValue("env.not.exists.trust.store.file").formatted(get(SSL_TRUSTSTORE_LOCATION), SSL_TRUSTSTORE_LOCATION));
                    }
                }

//                FileUtils.writeByteArrayToFile(file, Thread.currentThread().getContextClassLoader()
//                        .getResourceAsStream("cacerts").readAllBytes());
                System.setProperty("javax.net.ssl.trustStore", file.getAbsolutePath());
                System.setProperty("javax.net.ssl.trustStorePassword", getWithNullValue(SSL_TRUSTSTORE_PASSWORD));

                if (Objects.isNull(getWithNullValue(SSL_TRUSTSTORE_PASSWORD))) {
                    log.info(getLangValue("env.empty.trust.store.password"), SSL_TRUSTSTORE_PASSWORD, SSL_TRUSTSTORE_LOCATION);
                }
            } catch (Exception e) {
                e.printStackTrace();
                fail(getLangValue("env.fail.load.trust.store"));
            }
        }
    }

    private static void disableSslVerification() {
        if ("true".equals(getWithNullValue(SSL_VERIFICATION))) {
            SslVerification.disableSslVerification();
        }
    }

    private static void setAllureProperties() {
        if (Objects.nonNull(getWithNullValue(ALLURE_LINK_ISSUE_PATTERN))) {
            System.setProperty(ALLURE_LINK_ISSUE_PATTERN, get(ALLURE_LINK_ISSUE_PATTERN));
        }
    }
}
