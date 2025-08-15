package ru.origami.rest.attachment.bytes;

import ru.origami.testit_allure.allure.attachment.AttachmentProcessor;
import ru.origami.testit_allure.allure.attachment.AttachmentRenderer;
import ru.origami.testit_allure.allure.java_commons.Allure;
import ru.origami.testit_allure.allure.java_commons.AllureLifecycle;

public class RestAssuredBytesAttachmentProcessor implements AttachmentProcessor<ResponseBytesAttachment> {

    private final AllureLifecycle lifecycle;

    public RestAssuredBytesAttachmentProcessor() {
        this(Allure.getLifecycle());
    }

    public RestAssuredBytesAttachmentProcessor(final AllureLifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    @Override
    public void addAttachment(final ResponseBytesAttachment attachmentData,
                              final AttachmentRenderer<ResponseBytesAttachment> renderer) {
        final BytesAttachmentContent content = (BytesAttachmentContent) renderer.render(attachmentData);

        lifecycle.addAttachment(
                attachmentData.getName(),
                content.getContentType(),
                content.getFileExtension(),
                content.getByteContent()
        );
    }
}
