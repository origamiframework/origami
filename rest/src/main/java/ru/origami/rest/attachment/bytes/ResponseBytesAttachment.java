package ru.origami.rest.attachment.bytes;

import lombok.Getter;
import ru.origami.testit_allure.allure.attachment.AttachmentData;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static ru.origami.common.environment.Language.getLangValue;

@Getter
public class ResponseBytesAttachment implements AttachmentData {

    private final String name;

    private final String url;

    private final byte[] body;

    private final int responseCode;

    private final String contentType;

    private final Map<String, String> headers;

    private final Map<String, String> cookies;

    public ResponseBytesAttachment(Builder builder) {
        this.name = builder.getName();
        this.url = builder.getUrl();
        this.body = builder.getBody();
        this.responseCode = builder.getResponseCode();
        this.contentType = builder.getContentType();
        this.headers = builder.getHeaders();
        this.cookies = builder.getCookies();
    }

    @Getter
    @SuppressWarnings("PMD.AvoidFieldNameMatchingMethodName")
    public static final class Builder {

        private final String name;

        private String url;

        private int responseCode;

        private String contentType;

        private byte[] body;

        private final Map<String, String> headers = new HashMap<>();

        private final Map<String, String> cookies = new HashMap<>();

        private Builder(final String name) {
            this.name = Objects.requireNonNull(name, getLangValue("rest.name.null.error"));
        }

        public static Builder create(final String attachmentName) {
            return new Builder(attachmentName);
        }

        public Builder setUrl(final String url) {
            this.url = Objects.requireNonNull(url, getLangValue("rest.url.null.error"));

            return this;
        }

        public Builder setResponseCode(final int responseCode) {
            this.responseCode = responseCode;

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

        public Builder setBody(final byte[] body) {
            this.body = Objects.requireNonNull(body, getLangValue("rest.body.null.error"));

            return this;
        }

        public Builder setContentType(final String contentType) {
            this.contentType = contentType;

            return this;
        }

        public ResponseBytesAttachment build() {
            return new ResponseBytesAttachment(this);
        }
    }
}
