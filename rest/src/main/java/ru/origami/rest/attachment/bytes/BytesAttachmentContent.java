package ru.origami.rest.attachment.bytes;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.origami.testit_allure.allure.attachment.AttachmentContent;

@Getter
@Builder(builderMethodName = "Builder", setterPrefix = "set")
@ToString
public class BytesAttachmentContent implements AttachmentContent {

    private final String content;

    private final byte[] byteContent;

    private final String contentType;

    private final String fileExtension;
}
