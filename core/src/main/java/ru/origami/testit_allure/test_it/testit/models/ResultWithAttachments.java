package ru.origami.testit_allure.test_it.testit.models;

import java.util.List;

/**
 * The marker interface for model objects with attachments.
 */
public interface ResultWithAttachments {
    /**
     * Gets attachments.
     *
     * @return the attachments
     */
    List<String> getAttachments();
}
