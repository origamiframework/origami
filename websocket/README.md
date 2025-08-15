# WebSocket

* [Подключение](#Подключение)
* [WsSteps](#WsSteps)
* [WsTopic](#WsTopic)
* [Основной Readme](../README.md)

## Подключение

Необходимо добавить зависимость в pom.xml в проекте:
```XML
    <dependency>
        <groupId>ru.origamiframework</groupId>
        <artifactId>origami-framework-websocket</artifactId>
    </dependency>
```

## WsSteps

### Использование

Необходимо унаследоваться от [WsSteps](src/main/java/ru/origami/websocket/WsSteps.java)
с указанием в конструкторе параметров для подключения.
<br/> Параметр withWss: true при https, иначе false
<br/>Пример:
```JAVA
public class SourceStatusesWsSteps extends WsSteps {

    public SourceStatusesWsSteps() {
        properties = WsProperties.Builder()
                .setUrl(Environment.get("websocket.url"))
                .setPort(Environment.get("websocket.port"))
                .setEndpoint(Environment.get("websocket.endpoint"))
                .setWithWss(Boolean.parseBoolean(Environment.getWithNullValue("websocket.with.wss")))
                .build();
    }
}
```

### Методы

Осуществление подписки на топик

    subscribe(WsTopic wsTopic);
    subscribe(WsTopic wsTopic, String token);

<br/>Осуществление подписки на топик с маппингом результата на переданный класс

    subscribe(WsTopic wsTopic, Class<T> clazz)
    subscribe(WsTopic wsTopic, Class<T> clazz, String token);

<br/>Осуществление отписки от топика и получение результата. В случае переданного класса вернется список из объектов переданного класса.
В ином случае вернется List<String>

    unsubscribeAndGetResults(WsTopic wsTopic)

<br/>Методы для отписки от топика при получении сообщения или списка сообщений.
При неполучении сообщения в течении 5 сек или заданного интервала тест будет провален.

    unsubscribeWhenGetMessage(WsTopic wsTopic)

    unsubscribeWhenGetMessage(WsTopic wsTopic, long waitingTime)

<br/>Аналогичные приведенным выше методы, но с возможностью получения пустого результата (не найдено сообщение в топике)

    unsubscribeWhenGetMessageWithEmptyResult(WsTopic wsTopic)

    unsubscribeWhenGetMessageWithEmptyResult(WsTopic wsTopic, long waitingTime)

<br/>Осуществление отписки от топика без получения результата.

    unsubscribe(WsTopic wsTopic)

## WsTopic

Представляет собой Enum, который содержит названия топиков.

### Использование

Необходимо унаследоваться от [WsTopic](src/main/java/ru/origami/websocket/models/WsTopic.java)
<br/>Пример:
```JAVA
@Getter
@AllArgsConstructor
public enum ERtfWebSocketTopic implements WsTopic {

    SOURCES_STATUSES_TOPIC("/topic/source-statuses");

    private String topic;
}
```
