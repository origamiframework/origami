# Origami Framework

* [Описание](#описание)
* [Подключение](#подключение)
* [Конфигурация](#конфигурация)

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
так же подтягивается origami-framework-core.
```XML
    <parent>
        <groupId>ru.origamiframework</groupId>
        <artifactId>origami-framework-parent</artifactId>
        <version>${LAST_VERSION}</version>
        <relativePath/>
    </parent>
```

## Конфигурация

Создать файл конфигурации <b>resources/origami.properties</b> со следующим содержимым (представленный конфиг рабочий):

```PROPERTIES
    stand=dev
    language=ru
    logging.enabled=false
    test.timezone=UTC
    hibernate.excel.result.enabled=false
    kafka.test.data.package=src/main/resources/test_data/kafka
    testit.url=https://10.21.18.5
    testit.private.token=cEF3eXNFZW0wdFFMTjMwTmp6
    testit.project.id=9d5382a8-c592-419f-8e7c-6d9c1ace0d01
    testit.configuration.id=9eac5ebe-60eb-45ea-8eae-1eba128b81e4
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

