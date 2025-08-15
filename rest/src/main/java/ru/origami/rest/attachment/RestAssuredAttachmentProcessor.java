package ru.origami.rest.attachment;

import ru.origami.testit_allure.allure.attachment.AttachmentContent;
import ru.origami.testit_allure.allure.attachment.AttachmentData;
import ru.origami.testit_allure.allure.attachment.AttachmentProcessor;
import ru.origami.testit_allure.allure.attachment.AttachmentRenderer;
import ru.origami.testit_allure.allure.java_commons.Allure;
import ru.origami.testit_allure.allure.java_commons.AllureLifecycle;

import java.nio.charset.StandardCharsets;

public class RestAssuredAttachmentProcessor implements AttachmentProcessor<AttachmentData> {

    private final AllureLifecycle lifecycle;

    public RestAssuredAttachmentProcessor() {
        this(Allure.getLifecycle());
    }

    public RestAssuredAttachmentProcessor(final AllureLifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    @Override
    public void addAttachment(final AttachmentData attachmentData,
                              final AttachmentRenderer<AttachmentData> renderer) {
        final AttachmentContent content = renderer.render(attachmentData);

        lifecycle.addAttachment(
                attachmentData.getName(),
                content.getContentType(),
                content.getFileExtension(),
                content.getContent().getBytes(StandardCharsets.UTF_8)
        );
    }
}
