package ru.origami.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.converter.*;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import ru.origami.testit_allure.annotations.Step;
import ru.origami.websocket.models.WsTopic;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.fail;
import static ru.origami.common.environment.Language.getLangValue;
import static ru.origami.testit_allure.allure.java_commons.Allure.getLifecycle;
import static ru.origami.websocket.attachment.WsAttachment.attachResultsToAllure;

/**
 * Класс, реализующий шаги для работы с WebSocket
 */
@Slf4j
public class WsSteps {

    protected WsProperties properties;

    private Map<WsTopic, WsStompSessionHandler> handlers = new HashMap<>();

    private static final Long DEFAULT_WAITING_TIME = 5000L;

    private StompSession connect(WsTopic wsTopic, StompSessionHandler sessionHandler, String token) {
        if (properties == null) {
            handlers.remove(wsTopic);
            fail(getLangValue("websocket.props.null"));
        }

        try {
            StandardWebSocketClient simpleWebSocketClient = new StandardWebSocketClient();
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            Map<String, Object> props = new HashMap<>();
            props.put("org.apache.tomcat.websocket.SSL_CONTEXT", sc);
            simpleWebSocketClient.setUserProperties(props);

            List<Transport> transports = new ArrayList();
            transports.add(new WebSocketTransport(simpleWebSocketClient));

            WebSocketStompClient stompClient = new WebSocketStompClient(new SockJsClient(transports));

            List<MessageConverter> converters = new ArrayList<>();
            converters.add(new MappingJackson2MessageConverter());
            converters.add(new StringMessageConverter());
            converters.add(new SimpleMessageConverter());
            stompClient.setMessageConverter(new CompositeMessageConverter(converters));
            String ws = properties.isWithWss() ? "wss" : "ws";
            String connectionString = Objects.isNull(properties.getPort())
                    ? String.format("%s://%s/%s", ws, properties.getUrl(), properties.getEndpoint())
                    : String.format("%s://%s:%s/%s", ws, properties.getUrl(), properties.getPort(), properties.getEndpoint());
            WebSocketHttpHeaders headers = null;

            if (Objects.nonNull(token)) {
                headers = new WebSocketHttpHeaders();
                headers.add("Authorization", String.format("Bearer %s", token));
            }

            return stompClient.connect(connectionString, headers, sessionHandler).get();
        } catch (Exception e) {
            e.printStackTrace();
            handlers.remove(wsTopic);
            fail(getLangValue("websocket.connect.error").formatted(e.getMessage()));
        }

        return null;
    }

    /**
     * Метод для осуществления подписки на топик
     *
     * @param wsTopic название топика
     */
    public void subscribe(WsTopic wsTopic) {
        subscribe(wsTopic, Object.class, null);
    }

    /**
     * Метод для осуществления подписки на топик
     *
     * @param wsTopic название топика
     * @param token   Bearer токен
     */
    public void subscribe(WsTopic wsTopic, String token) {
        subscribe(wsTopic, Object.class, token);
    }

    /**
     * Метод для осуществления подписки на топик
     *
     * @param wsTopic название топика
     * @param clazz   тип для возвращаемого списка
     */
    public <T> void subscribe(WsTopic wsTopic, Class<T> clazz) {
        subscribe(wsTopic, clazz, null);
    }

    /**
     * Метод для осуществления подписки на топик
     *
     * @param wsTopic название топика
     * @param clazz   тип для возвращаемого списка
     * @param token   Bearer токен
     */
    @Step("getLangValue:websocket.step.subscribe")
    public <T> void subscribe(WsTopic wsTopic, Class<T> clazz, String token) {
        WsStompSessionHandler<T> sessionHandler = createHandler(wsTopic, clazz);
        sessionHandler.setSession(connect(wsTopic, sessionHandler, token));

        try {
            sessionHandler.getSession().subscribe(wsTopic.getTopic(), sessionHandler);
            log.info(getLangValue("websocket.connect.to.topic"), wsTopic.getTopic());
        } catch (Exception ex) {
            ex.printStackTrace();
            handlers.remove(wsTopic);
        }
    }

    /**
     * Метод для осуществления отписки от топика и получения результата
     *
     * @param wsTopic название топика
     * @return Возвращается список полученных сообщений во время подписки. Возвращаемый тип - Class<T> clazz,
     * который был передан при осуществлении подписки либо String
     */
    @Step("getLangValue:websocket.step.unsubscribe.and.get.results")
    public List unsubscribeAndGetResults(WsTopic wsTopic) {
        if (!handlers.containsKey(wsTopic)) {
            fail(getLangValue("websocket.unsubscribe.no.topic.error").formatted(wsTopic.getTopic()));
        }

        WsStompSessionHandler handler = handlers.remove(wsTopic);

        if (Objects.nonNull(handler.getException())) {
            fail(getLangValue("websocket.read.error").formatted(wsTopic.getTopic(), handler.getException().getMessage()));
        }

        attachResultsToAllure(wsTopic.getTopic(), handler.getFormattedResults());

        return handler.getResultsAndDisconnect(wsTopic);
    }

    /**
     * Метод для осуществления отписки от топика при получении сообщения
     * При неполучении сообщения в течении 5 сек тест будет провален
     *
     * @param wsTopic название топика
     * @return В случае нахождения сообщений возвращается список Class<T> clazz
     */
    public List unsubscribeWhenGetMessage(WsTopic wsTopic) {
        return unsubscribeWhenGetResults(wsTopic, DEFAULT_WAITING_TIME, false);
    }

    /**
     * Метод для осуществления отписки от топика при получении сообщения
     * При неполучении сообщения в течении waitingTime (мс) тест будет провален
     *
     * @param wsTopic название топика
     * @param waitingTime максимальное время ожидания сообщения(мс)
     * @return В случае нахождения сообщений возвращается список Class<T> clazz
     */
    public List unsubscribeWhenGetMessage(WsTopic wsTopic, long waitingTime) {
        return unsubscribeWhenGetResults(wsTopic, waitingTime, false);
    }

    /**
     * Метод для осуществления отписки от топика при получении сообщения
     * При неполучении сообщения в течении 5 сек тест не будет провален
     *
     * @param wsTopic название топика
     * @return В случае нахождения сообщений возвращается список Class<T> clazz
     */
    public List unsubscribeWhenGetMessageWithEmptyResult(WsTopic wsTopic) {
        return unsubscribeWhenGetResults(wsTopic, DEFAULT_WAITING_TIME, true);
    }

    /**
     * Метод для осуществления отписки от топика при получении сообщения
     * При неполучении сообщения в течении waitingTime (мс) тест не будет провален
     *
     * @param wsTopic название топика
     * @param waitingTime максимальное время ожидания сообщения(мс)
     * @return В случае нахождения сообщений возвращается список Class<T> clazz
     */
    public List unsubscribeWhenGetMessageWithEmptyResult(WsTopic wsTopic, long waitingTime) {
        return unsubscribeWhenGetResults(wsTopic, waitingTime, true);
    }

    @Step("getLangValue:websocket.step.unsubscribe.and.get.results")
    private List unsubscribeWhenGetResults(WsTopic wsTopic, long waitingTime, boolean withEmptyResult) {
        if (!handlers.containsKey(wsTopic)) {
            fail(getLangValue("websocket.unsubscribe.no.topic.error").formatted(wsTopic.getTopic()));
        }

        WsStompSessionHandler handler = handlers.remove(wsTopic);
        long startTime = System.currentTimeMillis();
        List results;

        do {
            if (Objects.nonNull(handler.getException())) {
                fail(getLangValue("websocket.read.error").formatted(wsTopic.getTopic(), handler.getException().getMessage()));
            }

            results = handler.getResults();
        } while (System.currentTimeMillis() - startTime < waitingTime && results.isEmpty());

        attachResultsToAllure(wsTopic.getTopic(), handler.getFormattedResults());
        handler.disconnect(wsTopic);

        if (results.isEmpty() && !withEmptyResult) {
            fail(getLangValue("websocket.no.records.while.subscribe").formatted(wsTopic.getTopic()));
        }

        return results;
    }

    /**
     * Метод для осуществления отписки от топика, если подписка найдена
     *
     * @param wsTopic название топика
     */
    public void unsubscribe(WsTopic wsTopic) {
        if (handlers.containsKey(wsTopic)) {
            WsStompSessionHandler handler = handlers.remove(wsTopic);
            handler.disconnect(wsTopic);
        }
    }

    private <T> WsStompSessionHandler<T> createHandler(WsTopic wsTopic, Class<T> clazz) {
        if (handlers.containsKey(wsTopic)) {
            fail(getLangValue("websocket.subscribe.already.exist").formatted(wsTopic.getTopic()));
        }

        handlers.put(wsTopic, new WsStompSessionHandler<T>(clazz));

        return handlers.get(wsTopic);
    }

    @Deprecated
    private void rewriteStepName(WsTopic wsTopic) {
        getLifecycle().updateStep(getLifecycle().getCurrentTestCaseOrStep().get(),
                step -> step.setName(step.getName().replaceAll("%wsTopic%", wsTopic.getTopic())));
    }
}
