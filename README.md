<br>
<div align="center">
<img src="./core/src/main/resources/img/logo.png" alt="Origami" width="200">

# Origami Framework

![Maven Central](https://img.shields.io/maven-central/v/ru.origamiframework/origami-framework-parent.svg)
[![License: Apache-2.0](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)
[![Telegram Join](https://img.shields.io/badge/Telegram-Join%20channel-blue?logo=telegram)](https://t.me/origamiframework)
</div>

[Origami Framework](https://origamiframework.ru/) - это фреймворк для автоматизации тестирования на Java, созданный для того,
чтобы упрощать построение тестовой архитектуры и ускорять процесс приемочного и регрессионного тестирования.

С подробным руководством по Origami Framework можно ознакомиться на официальном сайте https://origamiframework.ru/.

## Описание

Фреймворк включает в себя:
* [Core](core/README.md)
* [Hibernate](hibernate/README.md)
* [Kafka](kafka/README.md)
* [RestAssured](rest/README.md)
* [WebSocket](websocket/README.md)
* [Selenide](selenide/README.md)
* [IBM MQ](ibm_mq/README.md)
* [Test Containers](test_containers/README.md)

## С чего начать?

Полное руководство по началу работы можно найти в разделе [С чего начать?](https://origamiframework.ru/start.html).

Для работы с Origami Framework рекомендуется использовать версию Java 17+(автоматически устанавливается при подключении origami-framework-parent).
При работе с [Maven](https://maven.apache.org/) необходимо добавить родителя в pom.xml в Вашем тестовом проекте
```XML
    <parent>
        <groupId>ru.origamiframework</groupId>
        <artifactId>origami-framework-parent</artifactId>
        <version>1.5.0</version>
        <relativePath/>
    </parent>
```

Использование [Maven](https://maven.apache.org/) не является обязательным. Так же можно запускать тесты, например,
с помощью [Gradle](https://gradle.org/).

Далее необходимо создать файл конфигурации <b>resources/origami.properties</b> со следующим содержимым:

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
- <b>stand</b> - стенд, на котором осуществляется запуск тестов (CI/CD переменная: STAND)
- <b>language</b> - язык логирования и внутренних шагов Allure и Test IT. Доступные значения: ru, en. Можно указывать свой файл
- <b>logging.enabled</b> - определяет осуществление логирования при прогоне в CI. По умолчанию: <i>false</i> (CI/CD переменная: LOGGING_ENABLED)
- <b>test.timezone</b> - при наличии параметра будет установлено системное время для запуска тестов.
- <b>hibernate.excel.result.enabled</b> - параметр необходим для прикрепления результата выборки из базы данных в формате Excel. 
 По умолчанию: <i>false</i>. Доступен при подключении модуля <i>hibernate</i> (CI/CD переменная: HIBERNATE_EXCEL_RESULT_ENABLED)
- <b>kafka.test.data.package</b> - путь до пакета с файлами для TestDataKafka. По умолчанию: <i>src/main/resources/test_data/kafka</i>. Доступен при подключении модуля <i>kafka</i>.
- <b>testit.url</b> - ваш адрес Test IT
- <b>testit.private.token</b> - токен пользователя Test IT
- <b>testit.project.id</b> - id проекта Test IT
- <b>testit.configuration.id</b> - id конфигурации Test IT
- <b>testit.adapter.mode</b> - режим работы адаптера Test IT
- <b>testit.enable.result</b> - при значении <i>true</i> тестовый прогон будет загружен в Test IT (CI/CD переменная: TEST_IT_ENABLE_RESULT)
- <b>web.site.url</b> - базовый URL-адрес
- <b>web.browser.name</b> - браузер для использования. По умолчанию: <i>chrome</i> (CI/CD переменная: WEB_BROWSER_NAME)
- <b>web.timeout</b> - таймаут в миллисекундах для провала теста, если условия все еще не выполнены. По умолчанию: <i>5000</i>
- <b>web.page.load.timeout</b> - таймаут загрузки веб-страницы (в миллисекундах). По умолчанию: <i>10000</i></p>

Так же вы можете воспользоваться архетипом для быстрого создания проекта [Origami Archetype](https://github.com/origamiframework/origami-archetype).

## Примеры

Раздел с примерами все еще дорабатывается и пополняется. Примеры можно найти в отдельном 
репозитории [Origami Samples](https://github.com/origamiframework/origami-samples).

## Помощь

Нашли ошибку или нужна новая функциональность?

Пожалуйста, [cообщайте](https://github.com/origamiframework/origami/issues?state=open)
о любых ошибках и предложениях.