package ru.origami.rest.attachment.bytes;

import ru.origami.rest.models.EContentType;
import ru.origami.testit_allure.allure.attachment.AttachmentRenderer;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static ru.origami.rest.models.EContentType.TEXT;

public class RestAssuredBytesAttachmentRenderer implements AttachmentRenderer<ResponseBytesAttachment> {

    @Override
    public BytesAttachmentContent render(final ResponseBytesAttachment data) {
        EContentType contentType = TEXT;
        byte[] content = "Empty body".getBytes(StandardCharsets.UTF_8);

        if (Objects.nonNull(data.getBody())) {
            if (data.getBody().length != 0) {
                content = data.getBody();
                contentType = EContentType.getContentTypeByType(data.getContentType());
            }
        }

        return BytesAttachmentContent.Builder()
                .setByteContent(content)
                .setContentType(contentType.getContentType())
                .setFileExtension(contentType.getFileExtension())
                .build();
    }
}
