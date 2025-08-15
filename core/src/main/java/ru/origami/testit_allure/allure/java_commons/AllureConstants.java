package ru.origami.testit_allure.allure.java_commons;

@SuppressWarnings({"unused", "PMD.ClassNamingConventions"})
public final class AllureConstants {

    public static final String TEST_RESULT_FILE_SUFFIX = "-result.json";

    public static final String TEST_RESULT_FILE_GLOB = "*-result.json";

    public static final String TEST_RESULT_CONTAINER_FILE_SUFFIX = "-container.json";

    public static final String TEST_RESULT_CONTAINER_FILE_GLOB = "*-container.json";

    public static final String ATTACHMENT_FILE_SUFFIX = "-attachment";

    public static final String ATTACHMENT_FILE_GLOB = "*-attachment*";

    private AllureConstants() {
        throw new IllegalStateException("Do not instance");
    }
}
