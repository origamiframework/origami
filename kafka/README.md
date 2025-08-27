# Origami Kafka

* [Подключение](#подключение)
* [Таблица совместимости](#таблица-совместимости-sasl-механизмов-и-security-protocol)
* [Properties](#properties)
* [Producer](#producer)
* [Consumer](#consumer)
* [Topic](#topic)
* [TestDataKafka](#testdatakafka)
* [Параметры](#параметры)
* [Основной Readme](../README.md)

## Подключение

Необходимо добавить зависимость в pom.xml в проекте:
```XML
    <dependency>
        <groupId>ru.origamiframework</groupId>
        <artifactId>origami-framework-kafka</artifactId>
    </dependency>
```

## Таблица совместимости SASL механизмов и Security Protocol

| Sasl Mechanism    | PLAINTEXT | SASL_PLAINTEXT | SSL  | SASL_SSL |
|-------------------|:---------:|:--------------:|:----:|:--------:|
| — (нет SASL)      |    ✔      |      ✗         | ✔    |    ✗     |
| PLAIN             |    ✗      |      ✔         | ✗    |    ✔     |
| SCRAM-SHA-256     |    ✗      |      ✔         | ✗    |    ✔     |
| SCRAM-SHA-512     |    ✗      |      ✔         | ✗    |    ✔     |
| GSSAPI (Kerberos) |    ✗      |      ✔         | ✗    |    ✔     |
| OAUTHBEARER       |    ✗      |      ✔         | ✗    |    ✔     |
| EXTERNAL          |    ✗      |      ✗         | ✔    |    ✔     |

## Properties

- **setBootstrapServers()** - список адресов Kafka-брокеров
- **setGroupId()** - имя группы консьюмеров, которые работают вместе
- **addSecurityProtocol()** - протокол безопасности для подключения клиента. Опционально с указанием regexp для названия стенда.
При запуске тестов на стендах, для которых указан протокол, будет использован данный протокол,
для запусков на остальных стендов будет использован протокол без указания regexp.
- **addSaslMechanism()** - определяет, какой механизм аутентификации SASL (Simple Authentication and Security Layer) будет использоваться клиентом Kafka
- **setUsername()** - имя пользователя для аутентификации
- **setPassword()** - пароль пользователя для аутентификации
- **setClientId()** - идентификатор приложения (client), зарегистрированного во внешней системе авторизации (например, OAuth-сервере)
- **setClientSecret()** - секретный ключ (пароль), привязанный к этому clientId
- **setSaslOauthBearerTokenEndpoint()** - это URL-адрес, по которому Kafka-клиент будет запрашивать OAuth 2.0 access token
у внешнего сервера авторизации, указывая свои clientId, clientSecret. Этот токен затем используется для аутентификации с Kafka-брокером
- **setSaslOauthBearerJwksEndpoint()** - это URL, по которому Kafka-брокер (или клиент) получает открытые ключи для проверки подписи JWT-токенов
- **setSslTruststoreLocation()** - путь к truststore-файлу, в котором хранятся сертификаты доверенных центров сертификации (CA certificates)
- **setSslTruststorePassword()** - пароль для доступа к truststore-файлу
- **setSaslKerberosServiceName()** - определяет имя Kerberos-сервиса, под которым зарегистрирован Kafka-брокер в Kerberos KDC (Key Distribution Center)
- **setSslKeystoreLocation()** - путь к файлу keystore (обычно формата JKS или PKCS12), в котором хранятся приватный ключ и сертификат клиента (или брокера)
- **setSslKeystorePassword()** - пароль для доступа ко всему keystore-файлу (для его открытия)
- **setSslKeyPassword()** - пароль, который защищает приватный ключ внутри keystore
- **setTopicPrefix()** - префикс будет добавлен перед названием топика кафки. Необходим для обхода различий в названии топиков на разных стендах.
- **setTopicPostfix()** - постфикс будет добавлен после названия топика кафки. Необходим для обхода различий в названии топиков на разных стендах
- **setRetryWaitingTime()** - установка общего времени ожидания для попыток чтения
- **setRetryMaxAttempts()** - установка попыток чтения
- **setRetryReadTimeout()** - установка времени ожидания между попытками чтения

## Producer

### Использование

Необходимо унаследоваться от [ProducerSteps](src/main/java/ru/origami/kafka/ProducerSteps.java) с указанием в конструкторе параметров для подключения.
<br/>Пример:
```JAVA
public class KafkaDboProducerSteps extends ProducerSteps {

    public KafkaDboProducerSteps() {
        properties = new Properties.Builder()
                .setBootstrapServers(Environment.get("kafka.bootstrap-servers"))
                .addSecurityProtocol(ESecurityProtocol.PLAINTEXT, "local.*")
                .addSecurityProtocol(ESecurityProtocol.SASL_SSL, "dev")
                .addSaslMechanism(ESaslMechanism.PLAIN, "dev")
                .setUsername(Environment.get("kafka.username"))
                .setPassword(Environment.get("kafka.password"))
                .setSslTruststoreLocation(Environment.getWithNullValue("kafka.ssl.truststore.location"))
                .setSslTruststorePassword(Environment.getWithNullValue("kafka.ssl.truststore.password"))
                .build();
    }
}
```

### Методы

<br/>Метод для задания партиции для отправки

    setPartition(int partition)

<br/>Отправка сообщения в топик

    sendMessage(Topic topic, String message);

    sendMessage(Topic topic, String message, List<Header> headers);

    sendMessage(Topic topic, String key, String message);

    sendMessage(Topic topic, String key, String message, List<Header> headers);

    sendMessageAsJson(Topic topic, Object message);

    sendMessageAsJson(Topic topic, Object message, List<Header> headers);

    sendMessageAsJson(Topic topic, String key, Object message);

    sendMessageAsJson(Topic topic, String key, Object message, List<Header> headers);

Отправка объекта в топик в формате JSON

    sendMessageAsJson(Topic topic, Object message);

    sendMessageAsJson(Topic topic, String key, Object message);


## Consumer

### Использование

Необходимо унаследоваться от [ConsumerSteps](src/main/java/ru/origami/kafka/ConsumerSteps.java) с указанием в конструкторе параметров для подключения.
<br/>Пример:
```JAVA
public class KafkaDboConsumerSteps extends ConsumerSteps {

    public KafkaDboConsumerSteps() {
        properties = new Properties.Builder()
                .setBootstrapServers(Environment.get("kafka.bootstrap-servers"))
                .addSecurityProtocol(ESecurityProtocol.SASL_PLAINTEXT)
                .addSaslMechanism(ESaslMechanism.OAUTHBEARER)
                .setClientId(Environment.get("kafka.client.id"))
                .setClientSecret(Environment.get("kafka.client.secret"))
                .setSaslOauthBearerTokenEndpoint(Environment.get("kafka.sasl.oauth.bearer.token.endpoint"))
                .setSaslOauthBearerJwksEndpoint(Environment.get("kafka.sasl.oauth.bearer.jwks.endpoint"))
                .setSslTruststoreLocation(Environment.getWithNullValue("kafka.ssl.truststore.location"))
                .setSslTruststorePassword(Environment.getWithNullValue("kafka.ssl.truststore.password"))
                .build();
    }
}
```

### Методы

В результате выполнения метода вернется объект [KafkaRecord](src/main/java/ru/origami/kafka/models/KafkaRecord.java),
который будет содержать ключ, само сообщение, временную метку сообщения, заголовки и тд.

<br/>Метод для задания глубины временного интервала вычитки сообщения для методов, начинающихся с readFirst... и readAll...

    setPeriod(Long period)

<br/>Методы для задания партиции или списка партиций

    setPartition(int partition)

    setPartitions(Integer... partitions)

<br/>Получение сообщения/й из топика по слову/ам поиска. Поиск будет произведен как по телу сообщения, так и по ключу сообщения

    readFirst(Topic topic)

    readFirst(Topic topic, String searchWord)

    readFirst(Topic topic, List<String> searchWords)

    readAll(Topic topic)

    readAll(Topic topic, String searchWord)

    readAll(Topic topic, List<String> searchWords)

<br/>Методы с возможность получения пустого результата

    readFirstWithEmptyResult(Topic topic)

    readFirstWithEmptyResult(Topic topic, String searchWord)

    readFirstWithEmptyResult(Topic topic, List<String> searchWords)

    readAllWithEmptyResult(Topic topic, String searchWord)

    .....

<br/>Аналогичные методы, но с маппингом сообщения из <b>XML/JSON</b>

    readFirstFromXml(Topic topic, Class<T> clazz)

    readFirstFromXml(Topic topic, String searchWord, Class<T> clazz)

    .....

    readFirstFromJson(Topic topic, Class<T> clazz)

    readFirstFromJson(Topic topic, String searchWord, Class<T> clazz)

    .....

    readAllFromXml(Topic topic, Class<T> clazz)

    readAllFromJson(Topic topic, Class<T> clazz)

    .....

<br/>Аналогичные методы с вычиткой из compact топика

    readAllCompact(Topic topic)

    readAllCompact(Topic topic, String searchWord)

    readAllCompact(Topic topic, List<String> searchWords)

    .....

<br/>Методы для подписки на топик с возможностью отписаться в нужный момент(unsubscribeAndGetResults, unsubscribeWhenGetMessage)

    subscribe(Topic topic)

<br/>Аналогичный метод подписки, но с получением найденного по слову поиска сообщения в формате <b>XML/JSON</b>

    subscribe(Topic topic, Class clazz)

<br/>Метод для отписки от топика кафки с возможностью получения пустого списка результатов. Вернется список строк

    unsubscribeAndGetResults(Topic topic)

    unsubscribeAndGetResults(Topic topic, String searchWord)

    unsubscribeAndGetResults(Topic topic, List<String> searchWords)

<br/>Аналогичные методы для отписки от топика кафки, но с получением сообщений в формате <b>XML/JSON</b>

    unsubscribeAndGetResultsFromJson(Topic topic)

    unsubscribeAndGetResultsFromXml(Topic topic)

    .....

<br/>Методы для отписки от топика кафки при получении сообщения или списка сообщений по заданному слову поиска. 
При неполучении сообщения в течении 5 сек или заданного интервала тест будет провален.

    unsubscribeWhenGetMessage(Topic topic)

    unsubscribeWhenGetMessage(Topic topic, String searchWord)

    unsubscribeWhenGetMessage(Topic topic, List<String> searchWords)

    unsubscribeWhenGetJsonMessage(Topic topic)

    .....

    unsubscribeWhenGetXmlMessage(Topic topic)

    .....

    unsubscribeWhenGetMessage(Topic topic, long waitingTime)

    .....

    unsubscribeWhenGetJsonMessage(Topic topic, long waitingTime)

    .....

    unsubscribeWhenGetXmlMessage(Topic topic, long waitingTime)

    .....

<br/>Аналогичные приведенным выше методы, но с возможностью получения пустого результата (не найдено сообщение в топике)

    unsubscribeWhenGetMessageWithEmptyResult(Topic topic)

    .....

## Topic

Представляет собой Enum, который содержит названия топиков.

### Использование

Необходимо унаследоваться от [Topic](src/main/java/ru/origami/kafka/models/Topic.java)
<br/>Пример:
```JAVA
@Getter
@AllArgsConstructor
public enum ESystemKafkaTopic implements Topic {

    DBO_STATUS("dbo.status"),
    DBO_DOCS("dbo.docs");

    private String topic;
}
```

## TestDataKafka

Необходим для прочтения текстовых файлов(<i>txt, xml, json, etc</i>)
с возможностью дальнейшего редактирования/использования в тестах.
<br/>[TestDataKafka](src/main/java/ru/origami/kafka/utils/TestDataKafka.java) должен использоваться в <i>ParameterizedTest</i>.  

### Использование

Необходимо добавить property:"***kafka.test.data.package***",
которое содержит путь до пакета с текстовыми файлами.

Пример использование в тесте:
```JAVA
@ParameterizedTest(name = "Отправка сообщения в topic: dbo.docs. Позитивный сценарий")
@CsvSource({"doc-list-add-rq.xml"})
public class successSendToDboLoanIssueDocs(TestDataKafka xml) {
    realization();   
}
```

Файл "doc-list-add-rq.xml" будет вычитан из директории, которая указана в системных параметрах.
<br/>После можно получить содержимое файла в тесте из объекта TestDataKafka:

    xml.getValue()

## Параметры

<b>kafka.test.data.package</b> - путь до пакета с файлами для [TestDataKafka](src/main/java/ru/origami/kafka/utils/TestDataKafka.java).
По умолчанию: <i>src/main/resources/test_data/kafka</i>.