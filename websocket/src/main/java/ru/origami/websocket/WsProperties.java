package ru.origami.websocket;

import lombok.Builder;
import lombok.Getter;

import java.util.Objects;

@Getter
@Builder(builderMethodName = "Builder", setterPrefix = "set")
public class WsProperties {

    private String url;

    private String port;

    private String endpoint;

    @Builder.Default
    private boolean withWss = false;

    @Override
    public String toString() {
        return Objects.isNull(port)
                ? String.format("%s/%s", url, endpoint)
                : String.format("%s:%s/%s", url, port, endpoint);
    }
}
