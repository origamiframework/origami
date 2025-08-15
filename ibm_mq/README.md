# Origami IBM MQ

* [Подключение](#подключение)
* [Producer](#producer)
* [Browser](#browser)
* [Consumer](#consumer)
* [MessageHelper](#messagehelper)
* [Основной Readme](../README.md)

## Подключение

Необходимо добавить зависимость в pom.xml в проекте:
```XML
    <dependency>
        <groupId>ru.origamiframework</groupId>
        <artifactId>origami-framework-ibm-mq</artifactId>
    </dependency>
```

## Producer

### Использование

Необходимо унаследоваться от [IbmMqProducer](src/main/java/ru/origami/ibm_mq/IbmMqProducer.java)
с указанием в конструкторе параметров для подключения.
<br/>Пример:
```JAVA
public class IbmMqCustomProducer extends IbmMqProducer {

    public IbmMqCustomProducer() {
        properties = Properties.Builder()
                .setHost(Environment.get("ibm.mq.host"))
                .setPort(Integer.parseInt(Environment.get("ibm.mq.port")))
                .setQueueManagerName(Environment.get("ibm.mq.queue.manager"))
                .setChannel(Environment.get("ibm.mq.channel"))
                .setUsername(Environment.get("ibm.mq.username"))
                .setPassword(Environment.get("ibm.mq.password"))
                .build();
    }
}
```

### Методы

Отправка сообщения в топик

    send(Queue queue, String messageStr);


## Browser

### Использование

Необходимо унаследоваться от [IbmMqBrowser](src/main/java/ru/origami/ibm_mq/IbmMqBrowser.java)
с указанием в конструкторе параметров для подключения.
<br/>Пример:
```JAVA
public class IbmMqCustomBrowser extends IbmMqBrowser {

    public IbmMqCustomBrowser() {
        properties = Properties.Builder()
                .setHost(Environment.get("ibm.mq.host"))
                .setPort(Integer.parseInt(Environment.get("ibm.mq.port")))
                .setQueueManagerName(Environment.get("ibm.mq.queue.manager"))
                .setChannel(Environment.get("ibm.mq.channel"))
                .setUsername(Environment.get("ibm.mq.username"))
                .setPassword(Environment.get("ibm.mq.password"))
                .build();
    }
}
```

### Методы

В результате выполнения метода вернется Message. Сообщение останется доступно для чтения из очереди.

    read(Queue queue);


## Consumer

### Использование

Необходимо унаследоваться от [IbmMqConsumer](src/main/java/ru/origami/ibm_mq/IbmMqConsumer.java)
с указанием в конструкторе параметров для подключения.
<br/>Пример:
```JAVA
public class IbmMqCustomConsumer extends IbmMqConsumer {

    public IbmMqCustomConsumer() {
        properties = Properties.Builder()
                .setHost(Environment.get("ibm.mq.host"))
                .setPort(Integer.parseInt(Environment.get("ibm.mq.port")))
                .setQueueManagerName(Environment.get("ibm.mq.queue.manager"))
                .setChannel(Environment.get("ibm.mq.channel"))
                .setUsername(Environment.get("ibm.mq.username"))
                .setPassword(Environment.get("ibm.mq.password"))
                .build();
    }
}
```

### Методы

В результате выполнения метода вернется первый Message в очереди и сообщение будет более недоступно в очереди.

    read(Queue queue);


## MessageHelper

[MessageHelper](src/main/java/ru/origami/ibm_mq/utils/MessageHelper.java) содержит вспомогательные методы для работы с Message.

### Методы

В результате выполнения метода из переданного Message будет извлечено сообщение в формате byte[] и возвращено String.

    getMessageBodyFromBytes(Message message);

