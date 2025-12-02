# Журнал изменений (origami-framework-parent)

### 1.4.5

- Core: изменен метод isLocal() для корректной работы с различными CI/CD
- Core: в Asserts при падении добавлено название поля
- Core: реализован CartesianSource
- Core: расширен набор паттернов EDateFormat для работы с датой и временем
- Core: изменен алгоритм инициализации Environment
- Core: расширен ряд глобальных параметров под идентичное название в CI/CD
- Kafka: исправлена плавающая ошибка ConcurrentModificationException при отписке
- Test Containers: добавлена реализация
- Selenide: оптимизация работы с драйвером и отображения ошибок

### 1.4.4

- Изменение работы с подписью и javadoc

### 1.4.3

- Правки по фреймворку
- Kafka: расширение retry
- IBM MQ: добавлена возможность оставить port=null

### 1.4.2

- Deploy to mvn repo

### 1.4.1

- Core: доработки для параметра allure.link.issue.pattern

### 1.4.0

- Rest: убран BROWSER_COMPATIBLE для multipart/form-data

### 1.3.9

- IBM MQ: приведение классов к актуальным названиям
- Rest: правки работы с multipart/form-data

### 1.3.8

- Core: отключение проверки сертификатов
- Core: доработка локализации
- Rest: отключение проверки сертификатов

### 1.3.7

- Kafka: фикс использования Sasl Mechanism при подключении

### 1.3.6

- Kafka: расширение списка механизмов подключения

### 1.3.5

- Kafka: добавление списка слов поиска для consumer
- Core: добавлена ошибка при некорректности указания параметров в Step
- Hibernate: ап версии + ап postgresql

### 1.3.4

- WebSocket: добавление методов unsubscribeWhen
- Kafka: расширение логирования producer
- Kafka: вывод ошибки при парсинге consumer
- Rest: доработка названия файла в кириллице для multipart/form-data

### 1.3.3

- Core: добавлена возможность генерации allure отчета в один html file
- Core: добавлена возможность генерации allure отчета в excel файл

### 1.3.2

- Kafka: добавлена проперти REQUEST_TIMEOUT_MS_CONFIG = 60000ms в конфиги продюсера 

### 1.3.1

- Hibernate: расширение addParameter и addParameterList + форматирование
- Hibernate: добавлено форматирование UUID

### 1.3.0

- Core: перенос хранения /config/{stand}.json

### 1.2.9

- Core: добавление локализации для внутренних Step

### 1.2.8

- Core: добавлена возможность прочтения пользовательских файлов конфигурации
- Core: реализована возможность задать глобальный системный параметр для ssl.trust.store

### 1.2.7

- Kafka: добавление unsubscribe методов с возможностью получения пустого результата. Работа с партициями
- Hibernate: фикс null value
- WebSocket: добавлен метод unsubscribe без получения результата

### 1.2.6

- Core: реализация OrigamiHelper
- Hibernate: фикс форматтера запросов

### 1.2.5

- Core: изменение TA аннотаций
- Core: добавление локализации

### 1.2.4

- Core: фикс загрузки stand из maven параметров
- Core: проверка на пустой файл конфигурации

### 1.2.3

- Kafka: доработка для null value для compact топика
- Kafka: fix unescapeXml

### 1.2.2

- Core: рефакторинг создания лишнего пакета core
- Удаление лишнего
- Selenide: up версии
- Kafka: добавлены методы для compact топика

### 1.2.1

- Core: добавлены новые asserts и helpers

### 1.2.0

- Rest: добавлены http методы с параметрами
- Core: добавлены Helpers

### 1.1.9

- Core: добавлена зависимость из модуля hibernate
- Common: добавлен XmlAsserts

### 1.1.8

- Kafka: проверка SaslMechanism not null
- Common: перенос общих методов по работе с json/xml
- Rest: обертка для работы с xml

### 1.1.7

- Kafka: добавление headers для producer

### 1.1.6

- POM parent: skip.tests = false

### 1.1.5

- Kafka: добавление атрибутов к KafkaRecord

### 1.1.4

- Core: добавление работы с сертификатами для кафки

### 1.1.3

- Kafka: добавление механизма OAUTHBEARER
- Kafka: фикс отписки

### 1.1.2

- Kafka: фикс subscribe и unsubscribe
- Kafka: правки обычной подписки

### 1.1.1

- Kafka: фикс unsubscribe

### 1.1.0

- Hibernate: фикс DriverSpy

### 1.0.9

- Kafka: добавление subscribe и unsubscribe

### 1.0.8

- Hibernate: доработка форматирования с LEFT/RIGHT JOIN
- Hibernate: доработка форматирования SELECT

### 1.0.7

- Добавлена работа с Selenide

### 1.0.6

- Core: Доработан функционал по работе с system props

### 1.0.5

- Rest: Доработки not null для RequestSpecBuilder

### 1.0.4

- Rest: Перевод rest на RequestSpecBuilder
- Core: Добавлены методы getInt(), getIntWithNullValue()

### 1.0.3

- Hibernate: Доработка запросов update

### 1.0.2

- Добавлены readme
- Изменена работа с конфигами

### 1.0.1

- Добавлена работа с IBM MQ

### 1.0.0

- Инициализация фреймворка