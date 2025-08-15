package ru.origami.rest.attachment;

import io.restassured.specification.MultiPartSpecification;
import lombok.Getter;
import ru.origami.testit_allure.allure.attachment.AttachmentData;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static ru.origami.common.environment.Language.getLangValue;

@Getter
public class RequestAttachment implements AttachmentData {

    private final String name;

    private String url;

    private final String method;

    private final String body;

    private final String curl;

    private final Map<String, String> headers;

    private final Map<String, String> cookies;

    private final List<MultiPartSpecification> formData;

    private final String contentType;

    public RequestAttachment(Builder builder) {
        this.name = builder.getName();
        this.url = builder.getUrl();
        this.method = builder.getMethod();
        this.body = builder.getBody();
        this.curl = builder.getCurl();
        this.headers = builder.getHeaders();
        this.cookies = builder.getCookies();
        this.formData = builder.getFormData();
        this.contentType = builder.getContentType();
    }

    @Getter
    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
    public static final class Builder {

        private final String name;

        private String url;

        private String method;

        private String body;

        private final Map<String, String> headers = new HashMap<>();

        private final Map<String, String> cookies = new HashMap<>();

        private String contentType;

        private List<MultiPartSpecification> formData;

        private Builder(final String name) {
            this.name = Objects.requireNonNull(name, getLangValue("rest.name.null.error"));
        }

        private Builder(final String name, final String url) {
            this.name = Objects.requireNonNull(name, getLangValue("rest.name.null.error"));
            this.url = url;
        }

        public static Builder create(final String attachmentName) {
            return new Builder(attachmentName);
        }

        public static Builder create(final String attachmentName, final String url) {
            return new Builder(attachmentName, url);
        }

        public Builder setMethod(final String method) {
            this.method = Objects.requireNonNull(method, getLangValue("rest.method.null.error"));

            return this;
        }

        public Builder setHeader(final String name, final String value) {
            Objects.requireNonNull(name, getLangValue("rest.header.name.null.error"));
            Objects.requireNonNull(value, getLangValue("rest.header.value.null.error"));
            this.headers.put(name, value);

            return this;
        }

        public Builder setHeaders(final Map<String, String> headers) {
            this.headers.putAll(Objects.requireNonNull(headers, getLangValue("rest.headers.null.error")));

            return this;
        }

        public Builder setCookie(final String name, final String value) {
            Objects.requireNonNull(name, getLangValue("rest.cookie.name.null.error"));
            Objects.requireNonNull(value, getLangValue("rest.cookie.value.null.error"));
            this.cookies.put(name, value);

            return this;
        }

        public Builder setCookies(final Map<String, String> cookies) {
            this.cookies.putAll(Objects.requireNonNull(cookies, getLangValue("rest.cookies.null.error")));

            return this;
        }

        public Builder setBody(final String body) {
            this.body = Objects.requireNonNull(body, getLangValue("rest.body.null.error"));

            return this;
        }

        public Builder setContentType(String contentType) {
            this.contentType = contentType;

            return this;
        }

        public Builder setMultipartFormData(List<MultiPartSpecification> multiParts) {
            this.formData = multiParts;

            return this;
        }

        public RequestAttachment build() {
            return new RequestAttachment(this);
        }

        private String getCurl() {
            if (Objects.nonNull(url)) {
                final StringBuilder builder = new StringBuilder("curl -v");

                if (Objects.nonNull(method)) {
                    builder.append(" -X ").append(method);
                }

                builder.append(" '").append(url).append('\'');
                headers.forEach((key, value) -> appendHeader(builder, key, value));
                cookies.forEach((key, value) -> appendCookie(builder, key, value));

                if (Objects.nonNull(body)) {
                    builder.append(" -d '").append(body).append('\'');
                }

                if (Objects.nonNull(formData)) {
                    for (MultiPartSpecification multiPart : formData) {
                        if (Objects.nonNull(multiPart) && multiPart.getContent() instanceof File) {
                            appendFile(builder, multiPart.getControlName(), multiPart.getFileName());
                        } else {
                            appendMultiPart(builder, multiPart.getControlName(), multiPart.getContent().toString());
                        }
                    }
                }

                return builder.toString();
            }

            return null;
        }

        private static void appendHeader(final StringBuilder builder, final String key, final String value) {
            builder.append(" -H '")
                    .append(key)
                    .append(": ")
                    .append(value)
                    .append('\'');
        }

        private static void appendCookie(final StringBuilder builder, final String key, final String value) {
            builder.append(" -b '")
                    .append(key)
                    .append('=')
                    .append(value)
                    .append('\'');
        }

        private static void appendFile(final StringBuilder builder, final String key, final String value) {
            builder.append(" -F '")
                    .append(key)
                    .append("=@")
                    .append(value)
                    .append("\'");
        }

        private static void appendMultiPart(final StringBuilder builder, final String key, final String value) {
            builder.append(" -F '")
                    .append(key)
                    .append("=")
                    .append(value)
                    .append("\'");
        }
    }
}
