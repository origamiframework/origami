package ru.origami.testit_allure.allure.attachment;

import ru.origami.testit_allure.allure.java_commons.Allure;
import ru.origami.testit_allure.allure.java_commons.AllureLifecycle;

import java.nio.charset.StandardCharsets;

public class DefaultAttachmentProcessor implements AttachmentProcessor<AttachmentData> {

    private final AllureLifecycle lifecycle;

    public DefaultAttachmentProcessor() {
        this(Allure.getLifecycle());
    }

    public DefaultAttachmentProcessor(final AllureLifecycle lifecycle) {
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
