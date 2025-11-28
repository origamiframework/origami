# Origami Core

* [Подключение](#подключение)
* [Аннотации](#аннотации)
* [Task](#task)
* [Asserts](#asserts)
* [Environment](#environment)
* [OrigamiHelper](#origamihelper)
* [Allure](#allure)
* [Основной Readme](../README.md)


## Подключение

Подключается автоматически при добавлении parent в pom проекта.

## Аннотации

Чтобы тесты в Test IT и Allure отчетах отображались с указанными шагами, описанием и заголовками
необходимо использовать универсальные аннотации (замена Allure и Test IT аннотаций)

### Аннотации класса
```JAVA
    @Epic("Получение котировок offline")
    @Feature("Получение списка инструментов")
    @Story("Получение списка инструментов")
    @DisplayName("GET /symbols. Получение списка инструментов")
    @Link(name = "Документация метода GET /symbols",
            url = "https://..../confluence/pages/viewpage.action?pageId=123")
    public class GetSymbols {
        ...
    }
```

<i>@Epic, @Feature</i> - стандартные Allure аннотации, в Test IT не используются.
<br><i>@Story</i> - заменяет Allure аннотацию <i>@Story</i>. В Test IT не было аналога, используется
в качестве значения пакета для тестовых классов.
<br><i>@DisplayName</i> - заменяет Allure аннотацию <i>@DisplayName</i>. Заменяет Test IT аннотации - <i>@DisplayName, @Title</i>.
<br><i>@Link</i> - заменяет Allure аннотацию <i>@Link</i>. Заменяет Test IT аннотацию - <i>@Link</i>.
Для <i>@Link</i> можно выбрать тип ссылки отдельно для Allure(allureType) и отдельно для Test IT(testItType).

### Аннотации теста

#### Тест
```JAVA
    @Test
    @DisplayName("Вызов метода GET /symbols. Позитивный сценарий")
    @Description("Проверяем, что на вызов метода вернулся успешный ответ")
    @WorkItemIds("270492")
    public void successGetSymbols() {
        ...
    }
```

<i>@DisplayName</i> - заменяет Allure аннотацию <i>@DisplayName</i>. Заменяет Test IT аннотации - <i>@DisplayName, @Title</i>.
<br><i>@Description</i> - заменяет Allure аннотацию <i>@Description</i>. Заменяет Test IT аннотацию - <i>@Description</i>.
<br><i>@WorkItemIds</i> - стандартная Test IT аннотация, не используется в Allure.
Указывается id теста (или id нескольких тестов - <i>@WorkItemIds({"270492", "270493"})</i>), который необходимо пометить как автоматизированный.

#### Параметризованный тест
```JAVA
    @ParameterizedTest(name = "Вызов метода POST /manualQuotes/file с отрицательным {0}. Позитивный сценарий")
    @Description("Проверяем, что на вызов метода вернулся успешный ответ")
    @CsvSource({"bid, GBP/RUB_TOM", "ask, GBP/USD_TOD"})
    public void successPostManualFileWithNegativeValue(String value, String symbol) {
        ...
    }
```

Для параметризованного теста так же можно указать <i>@DisplayName</i> - будет влиять только на отображение названия группы тестов в IDE.
<br>Значение для теста в Test IT берется из <i>@ParameterizedTest</i> + параметризованные данные.

#### Шаг
```JAVA
    @Step("Отправляем запрос GET /symbols")
    public Response getSymbols() {
        ...
    }
```

<i>@Step</i> - заменяет Allure аннотацию <i>@Step</i>. Заменяет Test IT аннотации - <i>@Step, @Title</i>.

### Аннотации предусловий/постусловий

```JAVA
    @BeforeAll
    @DisplayName("Получение валютных пар")
    public void initSymbols() {
        ...
    }
```

Ранее в Allure для методов предусловий/постусловий бралось название из метода. Теперь при указании данной аннотации будет взято ее значение.

### Добавление описания в Test IT для вложений

В Allure при добавлении аннотации <i>@Attachment</i> к шагу будет добавлено вложение(например: sql запрос, сообщение в очередь и тд.).
<br/>Для Test IT реализована аналогичная возможность добавления данных вложений к шагам. Вложения будут добавлены в виде <i>Описания</i> к шагу
при условии, что у шага отсутствует свое описание(<i>@Description</i>).
<br/>
<br/>К методу необходимо добавить аннотацию <i>@Step</i> со значением <i>TEST_IT_ATTACHMENT_TECH_STEP_VALUE</i>. С данным значением
новый шаг не будет создан ни в Allure, ни в Test IT. А описание будет прикреплено к шагу в Test IT, который создавался до вызова данного метода.
<br/>Так же необходимо добавить аннотацию <i>@Description</i>, которая будет содержать описание, которое будет прикреплено к шагу.

```JAVA
    @Step(TEST_IT_ATTACHMENT_TECH_STEP_VALUE)
    @Description("{0}")
    private static void attachQueryToTestIT(String query) {
    }
```

## Task

Позволяет выполнить задачу, которая возвращает результат из отдельного потока.
Может вызвать исключение.

[Документация для использования](src/main/java/ru/origami/common/task/Task.java).

### Пример

Реализация класса с методами, которые будут выполняться в отдельных потоках
```Java
public class ServiceMethodTask extends Task {

    public ServiceMethodTask(String methodName, Object... args) {
        super(methodName, args);
    }

    @Step("Отправляем запрос POST ...")
    private Response post(Long id) {
        ServiceSteps serviceSteps = new ServiceSteps();
        Response response = serviceSteps.post(id);
        serviceSteps.responseShouldBeSuccess();

        return response;
    }

    @Step("Отправка сообщение в топик")
    private void postKafka(TestDataKafka xml1, TestDataKafka xml2) {
        realization();
    }
}
```

Использование вышеприведенных методов в шаге теста
```Java
    @Step("Отправляем запрос POST ... c отправкой сообщения в topic")
    public Response postMethodWithKafkaCall(Long id, TestDataKafka xml1, TestDataKafka xml2) {
        ServiceMethodTask postTask = new ServiceMethodTask("post", id);
        ServiceMethodTask kafkaTask = new ServiceMethodTask("postKafka", xml1, xml2);

        postTask.submit();
        waitResult(5);
        kafkaTask.submit();

        return postTask.get();
    }
```

## Asserts

[Asserts](src/main/java/ru/origami/common/asserts/Asserts.java) включает в себя необходимые проверки для использования в тестах.

## Environment

[Environment](src/main/java/ru/origami/common/environment/Environment.java) необходим для работы с параметрами.
<br/>Вычитывает параметры из **origami.properties**, из **/config/{stand}.json**, где stand - системное свойство.
<br/>Так же вычитывает параметры из **statics/routs.json** и **statics/any.json** (необязательные).
<br/>Существует возможность указать пользовательские файлы конфигураций. Для этого в **origami.properties** необходимо
указать пути до таких файлов. Название параметра должно начинаться с **custom.properties.file.path**
Если какие-либо параметры определены и в пользовательском файле и в файле stand, то возьмется значение из stand файла.

```
    custom.properties.file.path=auth.json
    custom.properties.file.path.2=common.json
    custom.properties.file.path.3=custom.json
```

<br/>Существует возможность задать глобальный системный параметр для **ssl.trust.store**. Для этого необходимо
в **origami.properties** указать путь и пароль от сертификата. Возможно указать как полный путь от корня проекта, так и путь
конкретно в resources. В примере приведены оба варианта.

```
    global.ssl.trust.store.location=cacerts
    global.ssl.trust.store.password=changeit
    ИЛИ
    global.ssl.trust.store.location=src/main/resources/cacerts
    global.ssl.trust.store.password=changeit
```
Так же в CI/CD можно переопределить данные переменные. Названия будут GLOBAL_SSL_TRUST_STORE_LOCATION и
 GLOBAL_SSL_TRUST_STORE_PASSWORD соответственно.

<br/>Существует возможность глобально отключить проверку сертификатов для всех HTTPS-соединений

```
    disable.ssl.verification=true
```
Так же в CI/CD можно переопределить данную переменную. Название для CI/CD: DISABLE_SSL_VERIFICATION.

### Методы

Получение значения параметра по ключу

    get(String key)
    getInt(String key)

Получение значения параметра по ключу или вернется null при отсутствии значения

    getWithNullValue(String key)
    getIntWithNullValue(String key)

Параметры из origami.properties будут добавлены в системные параметры.

## OrigamiHelper

[OrigamiHelper](src/main/java/ru/origami/common/common/OrigamiHelper.java) содержит общие методы, такие как ожидания, работа с xml/json и тд.

## Allure

Чтобы задать шаблон URL для ссылок типа issue, необязательно создавать отдельный файл **allure.properties** и в нем прописывать значение параметра.
В **origami.properties** можно задать аналогичный параметр **allure.link.issue.pattern**.

Для генерации отчета в один html файл необходимо запустить **mvn allure:report**. Сгенерированный файл появится по пути
**/target/site/allure-maven-plugin/index.html**

Для генерации excel отчета на основе allure необходимо выполнить команду **mvn exec:java@allure-excel**. Сгенерированный 
файл появится по пути **/target/allure-report_dd_MM_yyyy.xlsx**