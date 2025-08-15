package ru.origami.testit_allure.allure.model;

import java.util.List;

/**
 * The marker interface for model objects with attachments.
 *
 * @see TestResult
 * @see FixtureResult
 * @see StepResult
 * @see ExecutableItem
 */
public interface WithAttachments {

    /**
     * Gets attachments.
     *
     * @return the attachments
     */
    List<Attachment> getAttachments();

}
