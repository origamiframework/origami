# Origami RestAssured

* [Подключение](#подключение)
* [Использование](#использование)
* [Методы](#методы)
* [Основной Readme](../README.md)

## Подключение

Необходимо добавить зависимость в pom.xml в проекте:
```XML
    <dependency>
        <groupId>ru.origamiframework</groupId>
        <artifactId>origami-framework-rest</artifactId>
    </dependency>
```

## Использование

Необходимо унаследовать CommonSteps от [RestSteps](src/main/java/ru/origami/rest/RestSteps.java).
<br/>Необходимо использовать метод получения базового RequestSpecBuilder: <b>getRequestSpecBuilder()</b>
<br/>В CommonSteps реализуются методы "сборщики" базовых RequestSpecBuilder.
<br/>Пример:
```JAVA
public class CommonSteps extends RestSteps {

    protected RequestSpecBuilder getRtfRequestSpecBuilder() {
        return getRequestSpecBuilder()
                .setBaseUri(Environment.get("base.url"))
                .setPort(Integer.parseInt(Environment.get("rtf.port")))
                .addHeader("Accept", "application/json, text/plain, */*")
                .addHeader("Content-Type", "application/json; charset=UTF-8");
    }
}
```

Далее необходимо унаследоваться от CommonSteps. Унаследованный класс должен содержать методы для работы с конкретной системой,
который содержит методы получения RequestSpec для методов сервиса.
<br/>Пример:
```JAVA
public class CommonRtfSteps extends CommonSteps {

    protected RequestSpec getRtfServiceMethodRequestSpec() {
        return getRtfRequestSpecBuilder()
                .setBasePath(Environment.get("..."))
                .addQueryParam("param", 123)
                .addHeader("header", "header")
                .build();
    }
}
```

Далее необходимо унаследоваться от класса с общими шагами системы. Унаследованный класс будет содержать методы,
доступные в тесте (методы, выполняющие get, post, put..., методы с проверками).
<br/>Пример:
```JAVA
public class Service123Steps extends CommonRtfSteps {

    @Step("Отправить запрос POST /v1/method")
    public Response getMethod() {
        return getRtfServiceMethodRequestSpec().get();
    }
}
```

## Методы

RestSteps содержит методы, для проверки статус кодов ответов.
<br/>Примеры:
<br/>Статус код 200:
```JAVA
    someSteps.responseShouldBeSuccess();
```

Статус код 404:
```JAVA
    someSteps.responseShouldBeNotFound();
```

Статус код 500:
```JAVA
    someSteps.responseShouldBeInternalServerError();
```

Метод <b>then()</b>, доступный в классе с шагами проверок. Возвращает последний response для дальнейшей работы с ним.
```JAVA
@Step("Проверка ...")
public void errorShouldBePresentInResponse() {
        then().assertThat()
            .body("size()", is(1))
            .body("error.size()", is(4));
        }
```
