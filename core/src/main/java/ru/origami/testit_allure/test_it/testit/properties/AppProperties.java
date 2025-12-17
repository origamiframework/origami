package ru.origami.testit_allure.test_it.testit.properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import static ru.origami.common.environment.Environment.ORIGAMI_PROPERTIES_FILE;

public class AppProperties {

    public static final String URL = "testit.url";
    public static final String PRIVATE_TOKEN = "testit.private.token";
    public static final String PROJECT_ID = "testit.project.id";
    public static final String CONFIGURATION_ID = "testit.configuration.id";
    public static final String TEST_RUN_ID = "testit.test.run.id";
    public static final String TEST_RUN_NAME = "testit.test.run.name";
    public static final String ADAPTER_MODE = "testit.adapter.mode";
    private static final String ENV_PREFIX = "testit.tms";
    private static final String CONFIG_FILE = "CONFIG_FILE";
    private static final Logger log = LoggerFactory.getLogger(AppProperties.class);

    public AppProperties() {
    }

    public static Properties loadProperties() {
        String configFile = getConfigFileName();

        Properties properties = new Properties();
        loadPropertiesFrom(Thread.currentThread().getContextClassLoader(), properties, configFile);
        loadPropertiesFrom(ClassLoader.getSystemClassLoader(), properties, configFile);

//        if (!String.valueOf(properties.get(PRIVATE_TOKEN)).equals("null")) {
//            log.warn("The configuration file specifies a private token. It is not safe. Use TMS_PRIVATE_TOKEN environment variable");
//        }

        properties.putAll(loadPropertiesFromEnv());
        properties.putAll(loadPropertiesFromCli());

        return properties;
    }

    private static void loadPropertiesFrom(final ClassLoader classLoader, final Properties properties, String fileName) {
        try (InputStream stream = classLoader.getResourceAsStream(fileName)) {
            if (stream != null) {
                properties.load(stream);
//                return;
            }
        } catch (IOException e) {
            log.error("Exception while read properties: {}", e.getMessage());
        }

//        throw new RuntimeException(String.format("Config file '%s' not found", fileName));
    }

    private static Map<String, String> loadPropertiesFromEnv() {
        Map<String, String> map = new HashMap<>();

        String url = System.getenv(String.format("%s_URL", ENV_PREFIX));
        if (url != null) {
            map.put(URL, url);
        }

        String token = System.getenv(String.format("%s_PRIVATE_TOKEN", ENV_PREFIX));
        if (token != null) {
            map.put(PRIVATE_TOKEN, token);
        }

        String project = System.getenv(String.format("%s_PROJECT_ID", ENV_PREFIX));
        if (project != null) {
            map.put(PROJECT_ID, project);
        }

        String config = System.getenv(String.format("%s_CONFIGURATION_ID", ENV_PREFIX));
        if (config != null) {
            map.put(CONFIGURATION_ID, config);
        }

        String testRunId = System.getenv(String.format("%s_TEST_RUN_ID", ENV_PREFIX));
        if (testRunId != null) {
            map.put(TEST_RUN_ID, testRunId);
        }

        String testRunName = System.getenv(String.format("%s_TEST_RUN_NAME", ENV_PREFIX));
        if (testRunName != null) {
            map.put(TEST_RUN_NAME, testRunName);
        }

        String adapterMode = System.getenv(String.format("%s_ADAPTER_MODE", ENV_PREFIX));
        if (adapterMode != null) {
            map.put(ADAPTER_MODE, adapterMode);
        }

        return map;
    }

    private static Map<String, String> loadPropertiesFromCli() {
        Map<String, String> map = new HashMap<>();
        Properties systemProperties = System.getProperties();

        String url = systemProperties.getProperty(String.format("%sUrl", ENV_PREFIX.toLowerCase()));
        if (url != null) {
            map.put(URL, url);
        }

        String token = systemProperties.getProperty(String.format("%sPrivateToken", ENV_PREFIX.toLowerCase()));
        if (token != null) {
            map.put(PRIVATE_TOKEN, token);
        }

        String project = systemProperties.getProperty(String.format("%sProjectId", ENV_PREFIX.toLowerCase()));
        if (project != null) {
            map.put(PROJECT_ID, project);
        }

        String config = systemProperties.getProperty(String.format("%sConfigurationId", ENV_PREFIX.toLowerCase()));
        if (config != null) {
            map.put(CONFIGURATION_ID, config);
        }

        String testRunId = systemProperties.getProperty(String.format("%sTestRunId", ENV_PREFIX.toLowerCase()));
        if (testRunId != null) {
            map.put(TEST_RUN_ID, testRunId);
        }

        String testRunName = systemProperties.getProperty(String.format("%sTestRunName", ENV_PREFIX.toLowerCase()));
        if (testRunName != null) {
            map.put(TEST_RUN_NAME, testRunName);
        }

        String adapterMode = systemProperties.getProperty(String.format("%sAdapterMode", ENV_PREFIX.toLowerCase()));
        if (adapterMode != null) {
            map.put(ADAPTER_MODE, adapterMode);
        }

        return map;
    }

    private static String getConfigFileName() {
        Properties systemProperties = System.getProperties();
        String fileNameFromCli = systemProperties.getProperty(String.format("%sConfigFile", ENV_PREFIX.toLowerCase()));

        if (fileNameFromCli != null) {
            return fileNameFromCli;
        }

        String fileNameFromEnv = System.getenv(String.format("%s%s", ENV_PREFIX, CONFIG_FILE.toUpperCase(Locale.getDefault())));

        if (fileNameFromEnv != null) {
            return fileNameFromEnv;
        }

        return ORIGAMI_PROPERTIES_FILE;
    }
}
