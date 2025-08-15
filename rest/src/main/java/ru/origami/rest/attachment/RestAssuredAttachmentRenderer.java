package ru.origami.rest.attachment;

import ru.origami.rest.models.EContentType;
import ru.origami.testit_allure.allure.attachment.AttachmentData;
import ru.origami.testit_allure.allure.attachment.AttachmentRenderer;
import ru.origami.testit_allure.allure.attachment.DefaultAttachmentContent;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

import static ru.origami.rest.models.EContentType.*;

public class RestAssuredAttachmentRenderer implements AttachmentRenderer<AttachmentData> {

    @Override
    public DefaultAttachmentContent render(final AttachmentData data) {
        if (data instanceof RequestAttachment) {
            RequestAttachment reqData = (RequestAttachment) data;
            EContentType contentType = TEXT;
            String content;

            if (reqData.getCurl() == null && reqData.getBody() == null) {
                Map<String, String> value = null;

                if (!reqData.getHeaders().isEmpty()) {
                    value = reqData.getHeaders();
                } else if (!reqData.getCookies().isEmpty()) {
                    value = reqData.getCookies();
                }

                content = getContent(contentType, value);
            } else if (reqData.getCurl() == null && reqData.getBody() != null) {
                if (XML.getContentType().equals(reqData.getContentType()) || reqData.getBody().startsWith("<")) {
                    contentType = XML;
                } else {
                    contentType = JSON;
                }

//                content = getBodyContent(contentType, reqData.getBody());
                content = reqData.getBody();
            } else {
                content = reqData.getCurl();
            }

            return new DefaultAttachmentContent(content, contentType.getContentType(), contentType.getFileExtension());
        } else {
            ResponseAttachment resData = (ResponseAttachment) data;
            EContentType contentType = TEXT;
            String content = null;

            if (!resData.getHeaders().isEmpty()) {
                content = getContent(contentType, resData.getHeaders());
            } else if (Objects.nonNull(resData.getBody())) {
                if (resData.getBody().isEmpty()) {
                    content = "Empty Body";
                } else {
                    if (XML.getContentType().equals(resData.getContentType()) || resData.getBody().startsWith("<")) {
                        contentType = XML;
                    } else {
                        contentType = JSON;
                    }

//                    content = getBodyContent(contentType, resData.getBody());
                    content = resData.getBody();
                }
            }

            return new DefaultAttachmentContent(content, contentType.getContentType(), contentType.getFileExtension());
        }
    }

    private String getContent(EContentType contentType, Object value) {
        try {
            return contentType.getMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(value);
        } catch (IOException e) {
            return value.toString();
        }
    }

    private String getBodyContent(EContentType contentType, String value) {
        try {
            return getContent(contentType, contentType.getMapper().readValue(normalizeHtmlSymbols(value), Object.class));
        } catch (IOException e) {
            return getContent(contentType, value);
        }
    }

    private static String normalizeHtmlSymbols(String value) {
        return value.replaceAll("(\\\\)? ", " ")
                .replaceAll("(\\\\)?\u00a0", " ")
                .replaceAll("\\\\\n", "\\\\n");
    }
}
