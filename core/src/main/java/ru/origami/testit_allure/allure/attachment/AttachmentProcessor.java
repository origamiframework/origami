package ru.origami.testit_allure.allure.attachment;

/**
 * @param <T> the type of attachment data.
 */
public interface AttachmentProcessor<T extends AttachmentData> {

    void addAttachment(T attachmentData, AttachmentRenderer<T> renderer);
}
