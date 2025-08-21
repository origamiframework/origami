<br>
<div align="center">
<img src="./core/src/main/resources/img/logo.png" alt="Origami" width="200">

# Origami Framework

![Maven Central](https://img.shields.io/maven-central/v/ru.origamiframework/origami-framework-core.svg)
[![License: Apache-2.0](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Telegram Join](https://img.shields.io/badge/Telegram-Join%20to%20channel-blue?logo=telegram)](https://t.me/origami_framework)
</div>

[Origami Framework](https://origamiframework.ru/) - это фреймворк для автоматизации тестирования на Java, созданный для того,
чтобы упрощать построение тестовой архитектуры и ускорять процесс приемочного и регрессионного тестирования.

С подробным руководством по Origami Framework на текущий момент можно ознакомиться в README.

Нашли ошибку или нужна новая функциональность? Пожалуйста, [cообщайте](https://github.com/origamiframework/origami/issues?state=open) 
о любых ошибках и предложениях.

## Описание

Фреймворк включает в себя:
* [Core](core/README.md)
* [Hibernate](hibernate/README.md)
* [IBM MQ](ibm_mq/README.md)
* [Kafka](kafka/README.md)
* [Rest](rest/README.md)
* [WebSocket](websocket/README.md)
* [Selenide](selenide/README.md)

## Подключение

Необходимо добавить родителя в pom.xml в проекте. При этом в проект автоматически подтянутся все необходимые зависимости,
так же подтягивается Core([origami-framework-core](core/README.md)).
```XML
    <parent>
        <groupId>ru.origamiframework</groupId>
        <artifactId>origami-framework-parent</artifactId>
        <version>1.4.3</version>
        <relativePath/>
    </parent>
```

## Конфигурация

Создать файл конфигурации <b>resources/origami.properties</b> со следующим содержимым:

```PROPERTIES
    stand=dev
    language=ru
    logging.enabled=false
    test.timezone=UTC
    hibernate.excel.result.enabled=false
    kafka.test.data.package=src/main/resources/test_data/kafka
    testit.url=https://111.111.111.111
    testit.private.token=token
    testit.project.id=project-id
    testit.configuration.id=configuration-id
    testit.adapter.mode=2
    testit.enable.result=false
    web.browser.name=chrome
    web.timeout=5000
    web.page.load.timeout=10000
```

<b>Параметры:</b>
- <b>stand</b> - стенд, на котором осуществляется запуск тестов.
- <b>language</b> - язык логирования и внутренних шагов для Allure и Test IT. Доступные значения: ru, en. Можно указывать свой файл
- <b>logging.enabled</b> - определяет осуществление логирования при прогоне в CI. По умолчанию: false.
- <b>test.timezone</b> - при наличии параметра будет установлено системное время для запуска тестов.
- <b>hibernate.excel.result.enabled</b> - параметр необходим для прикрепления результата выборки из базы данных в формате Excel.
  По умолчанию: false. Доступен при подключении модуля hibernate.
- <b>kafka.test.data.package</b> - путь до пакета с файлами для [TestDataKafka](kafka/src/main/java/ru/origami/kafka/utils/TestDataKafka.java).
  По умолчанию: <i>src/main/resources/test_data/kafka</i>. Доступен при подключении модуля kafka.
- <b>testit.private.token</b> - токен технического пользователя qa_testit.
- <b>testit.enable.result</b> - при значении true тестовый прогон будет прикреплен к Test IT. При значении false или отсутствии параметра
  тестовый прогон не будет загружен в Test IT
- <b>web.site.url</b> - базовый URL-адрес
- <b>web.browser.name</b> - браузер для использования. По умолчанию: chrome
- <b>web.timeout</b> - таймаут в миллисекундах для провала теста, если условия все еще не выполнены. По умолчанию: 5000
- <b>web.page.load.timeout</b> - таймаут загрузки веб-страницы (в миллисекундах). По умолчанию: 10000

При дублировании какого либо параметра в файле параметров для конкретного стенда(например в dev.json) - значение
будет переопределено.

Про заполнение параметров Test IT можно почитать [официальную документацию Test IT](https://github.com/testit-tms/adapters-java/tree/main/testit-adapter-junit5).

