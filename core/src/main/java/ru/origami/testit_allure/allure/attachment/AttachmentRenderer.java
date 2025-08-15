package ru.origami.testit_allure.allure.attachment;

/**
 * @param <T> the type of attachment data
 */
@SuppressWarnings("PMD.AvoidUncheckedExceptionsInSignatures")
public interface AttachmentRenderer<T extends AttachmentData> {

    AttachmentContent render(T attachmentData) throws AttachmentRenderException;
}
