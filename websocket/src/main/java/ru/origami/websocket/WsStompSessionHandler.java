package ru.origami.websocket;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import ru.origami.websocket.models.WsTopic;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static ru.origami.common.OrigamiHelper.*;
import static ru.origami.common.environment.Environment.*;
import static ru.origami.common.environment.Language.getLangValue;

/**
 * Класс реализущий <code>StompSessionHandlerAdapter</code>.
 * Установив соединение, осуществляется подписка на топик
 */
@Slf4j
@Getter
public class WsStompSessionHandler<T> extends StompSessionHandlerAdapter {

    private List<T> results = new ArrayList<>();

    private Class mappingClass;

    private List<String> stringResults = new ArrayList<>();

    @Setter
    private StompSession session;

    private Throwable exception;

    public WsStompSessionHandler(Class cl) {
        this.mappingClass = cl;
    }

    @Override
    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
        if (isLocal() || isLoggingEnabled()) {
            log.debug(getLangValue("websocket.new.connect"), session.getSessionId());
        }
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload,
                                Throwable exception) {
        if (isLocal() || isLoggingEnabled()) {
            log.error(getLangValue("websocket.exception"), exception.getMessage(), exception);
        }

        if (Objects.isNull(this.exception)) {
            this.exception = exception;
        }
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return Object.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        try {
            String value = Objects.isNull(payload) ? null : prettyFormat(new String((byte[]) payload, StandardCharsets.UTF_8));

            if (Objects.nonNull(payload)) {
                T result;

                if (mappingClass.equals(Object.class)) {
                    result = (T) value;
                } else if (mappingClass.isEnum()) {
                    result = (T) getEnumFromString(value, mappingClass);
                } else {
                    result = (T) getObjectFromString(value, mappingClass);
                }

                if (Objects.isNull(result)) {
                    result = (T) value;
                }

                results.add(result);
                stringResults.add(value);
            }

            if (isLocal() || isLoggingEnabled()) {
                log.info(getLangValue("websocket.received"), value);
            }
        } catch (Exception ex) {
            this.exception = ex;
        }
    }

    public List getResultsAndDisconnect(WsTopic wsTopic) {
        List resultsForGet;

        if (!results.isEmpty() && results.get(0).getClass().equals(String.class)) {
            resultsForGet = stringResults;
        } else {
            resultsForGet = results;
        }

        session.disconnect();

        log.info(getLangValue("websocket.disconnect"), wsTopic.getTopic());

        return resultsForGet;
    }

    public List getResults() {
        if (!results.isEmpty() && results.get(0).getClass().equals(String.class)) {
            return stringResults;
        } else {
            return results;
        }
    }

    public void disconnect(WsTopic wsTopic) {
        session.disconnect();
        log.info(getLangValue("websocket.disconnect"), wsTopic.getTopic());
    }

    public String getFormattedResults() {
        return stringResults.stream()
                .collect(Collectors.joining(",\n", "[\n", "\n]"))
                .replaceAll("\n", "\n\t")
                .replaceAll("\t]$", "]");
    }
}
