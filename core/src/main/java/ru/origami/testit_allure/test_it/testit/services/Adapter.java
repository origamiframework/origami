package ru.origami.testit_allure.test_it.testit.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.origami.common.environment.Environment;
import ru.origami.testit_allure.test_it.testit.models.LinkItem;
import ru.origami.testit_allure.test_it.testit.models.LinkType;
import ru.origami.testit_allure.test_it.testit.properties.AppProperties;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public final class Adapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(Adapter.class);
    private static AdapterManager adapterManager;
    private static ResultStorage storage;

    private static final String ENABLE_TEST_IT_PROPERTY = "testit.enable.result";
    private static final String CI_ENABLE_TEST_IT_PROPERTY = "TEST_IT_ENABLE_RESULT";

    public static AdapterManager getAdapterManager() throws NoSuchAlgorithmException, KeyManagementException {
        if (Objects.isNull(adapterManager)) {
            Properties appProperties = AppProperties.loadProperties();
            String enable = Environment.getSysEnvPropertyOrDefault(ENABLE_TEST_IT_PROPERTY, CI_ENABLE_TEST_IT_PROPERTY, "false");

            if (!appProperties.isEmpty() && enable.equals("true")) {
                ConfigManager manager = new ConfigManager(appProperties);
                adapterManager = new AdapterManager(manager.getClientConfiguration(), manager.getAdapterConfig());
            }
        }

        return adapterManager;
    }

    public static ResultStorage getResultStorage() {
        if (Objects.isNull(storage)) {
            storage = new ResultStorage();
        }
        return storage;
    }

    /**
     * @deprecated This method is no longer acceptable to compute time between versions.
     * <p> Use {@link Adapter#addLinks(String, String, String, LinkType)} instead.
     */
    @Deprecated
    public static void link(final String title, final String description, final LinkType type, final String url) throws NoSuchAlgorithmException, KeyManagementException {
        final LinkItem link = new LinkItem().setTitle(title).setDescription(description).setType(type).setUrl(url);
        getAdapterManager().updateTestCase(testResult -> testResult.getResultLinks().add(link));
    }

    /**
     * @deprecated This method is no longer acceptable to compute time between versions.
     * <p> Use {@link Adapter#addLinks(String, String, String, LinkType)} instead.
     */
    @Deprecated
    public static void addLink(final String url, final String title, final String description, final LinkType type) throws NoSuchAlgorithmException, KeyManagementException {
        LinkItem link = new LinkItem().setTitle(title)
                .setDescription(description)
                .setType(type)
                .setUrl(url);

        List<LinkItem> links = new ArrayList<>();
        links.add(link);

        addLinks(links);
    }

    public static void addLinks(final String url, final String title, final String description, final LinkType type) throws NoSuchAlgorithmException, KeyManagementException {
        LinkItem link = new LinkItem().setTitle(title)
                .setDescription(description)
                .setType(type)
                .setUrl(url);

        List<LinkItem> links = new ArrayList<>();
        links.add(link);

        addLinks(links);
    }

    public static void addLinks(List<LinkItem> links) throws NoSuchAlgorithmException, KeyManagementException {
        getAdapterManager().updateTestCase(testResult -> testResult.getResultLinks().addAll(links));
    }

    public static void addAttachments(List<String> attachments) throws NoSuchAlgorithmException, KeyManagementException {
        getAdapterManager().addAttachments(attachments);
    }

    public static void addAttachments(String attachment) throws NoSuchAlgorithmException, KeyManagementException {
        List<String> attachments = new ArrayList<>();
        attachments.add(attachment);

        addAttachments(attachments);
    }

    public static void addAttachments(String content, String fileName) throws NoSuchAlgorithmException, KeyManagementException {
        if (fileName == null) {
            fileName = UUID.randomUUID() + "-attachment.txt";
        }

        Path path = Paths.get(fileName);
        try {
            BufferedWriter writer = Files.newBufferedWriter(path, Charset.defaultCharset());
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            LOGGER.error(String.format("Can not write file '%s':", fileName), e);
        }

        addAttachments(fileName);

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            LOGGER.error(String.format("Can not delete file '%s':", fileName), e);
        }
    }

    public static void addAttachments(String fileName, InputStream inputStream) throws NoSuchAlgorithmException, KeyManagementException {
        if (fileName == null) {
            LOGGER.error("Attachment name is empty");
            return;
        }

        Path path = Paths.get(fileName);
        try {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOGGER.error(String.format("Can not write file '%s':", fileName), e);
        }

        addAttachments(fileName);

        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            LOGGER.error(String.format("Can not delete file '%s':", fileName), e);
        }
    }

    /**
     * @deprecated This method is no longer acceptable to compute time between versions.
     * <p> Use {@link Adapter#addAttachments(String attachment)} instead.
     */
    @Deprecated
    public static void addAttachment(String attachment) throws NoSuchAlgorithmException, KeyManagementException {
        List<String> attachments = new ArrayList<>();
        attachments.add(attachment);

        getAdapterManager().addAttachments(attachments);
    }

    public static void addMessage(String message) throws NoSuchAlgorithmException, KeyManagementException {
        getAdapterManager().updateTestCase(testResult -> testResult.setMessage(message));
    }
}
