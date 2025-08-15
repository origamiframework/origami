# Selenide

Содержание:
* [Подключение](#Подключение)
* [Параметры](#параметры)
* [Основной Readme](../README.md)

## Подключение

Необходимо добавить зависимость в pom.xml в проекте:
```XML
    <dependency>
        <groupId>ru.origamiframework</groupId>
        <artifactId>origami-framework-selenide</artifactId>
    </dependency>
```

## Параметры

- <b>web.site.url</b> - базовый URL-адрес
- <b>web.browser.name</b> - браузер для использования. По умолчанию: chrome
- <b>web.timeout</b> - таймаут в миллисекундах для провала теста, если условия все еще не выполнены. По умолчанию: 5000
- <b>web.page.load.timeout</b> - таймаут загрузки веб-страницы (в миллисекундах). По умолчанию: 10000