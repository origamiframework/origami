package ru.origami.rest;

import com.google.common.base.Preconditions;
import io.restassured.filter.log.LogDetail;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import ru.origami.rest.models.RequestSpecBuilder;
import ru.origami.testit_allure.annotations.Step;

import static org.hamcrest.Matchers.*;
import static ru.origami.common.environment.Environment.*;
import static ru.origami.common.environment.Language.getLangValue;

/**
 * Шаги для работы с http запросами.
 *
 * <p>Для использования необходимо:
 * <p>1. Унаследовать CommonSteps от класса {@link RestSteps}
 * <p>2. Необходимо использовать метод получения базового RequestSpecBuilder: {@link #getRequestSpecBuilder()}
 * В CommonSteps реализуются методы "сборщики" базовых RequestSpecBuilder.
 * <pre>{@code
 *     public class CommonSteps extends RestSteps {
 *
 *          protected RequestSpecBuilder getRtfRequestSpecBuilder() {
 *              return getRequestSpecBuilder()
 *                 .setBaseUri(Environment.get("base.url"))
 *                 .setPort(Integer.parseInt(Environment.get("rtf.port")))
 *                 .addHeader("Content-Type","application/json; charset=UTF-8");
 *          }
 *     }}</pre>
 * <p>3. Необходимо унаследоваться от CommonSteps.
 * Унаследованный класс должен содержать методы для работы с конкретной системой, который содержит методы получения RequestSpec для методов сервиса.
 * <pre>{@code
 *     public class CommonRtfSteps extends CommonSteps {
 *
 *          protected RequestSpecification getRtfServiceMethodRequestSpec() {
 *              return getRtfRequestSpecBuilder()
 *                 .setBasePath(Environment.get("..."))
 *                 .addQueryParam("param", 123)
 *                 .addHeader("header", "header")
 *                 .build();
 *          }
 *      }}</pre>
 * <p>4. Необходимо унаследоваться от класса с общими шагами системы.
 * Унаследованный класс будет содержать методы, доступные в тесте (методы, выполняющие get, post, put..., методы с проверками).
 * <pre>{@code
 *     public class Service123Steps extends CommonRtfSteps {
 *
 *          @Step("Отправить запрос POST /v1/method")
 *          public Response getMethod() {
 *              return getRtfServiceMethodRequestSpec().get();
 *          }
 * }}</pre>
 *
 * <p>{@link RestSteps} содержит методы, для проверки статус кодов ответов.
 *
 * <p>Метод {@link #then()}, доступный в классе с шагами проверок. Возвращает последний response для дальнейшей работы с ним.
 */
public class RestSteps {

    private AllureRestAssured allureRestAssured = new AllureRestAssured();

    /**
     *
     * @return последний полученный response
     */
    protected ValidatableResponse then() {
        return lastResponse().then();
    }

    protected Response lastResponse() {
        return Preconditions.checkNotNull(allureRestAssured.getResponse(), getLangValue("rest.response.created.error"));
    }

    /**
     * Метод для получения базового RequestSpecBuilder
     *
     * @return базовый RequestSpecBuilder
     */
    protected RequestSpecBuilder getRequestSpecBuilder() {
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder(allureRestAssured);

        if (isLocal() || isLoggingEnabled()) {
            requestSpecBuilder.log(LogDetail.ALL);
        }

        return requestSpecBuilder;
    }

    @Step("getLangValue:rest.step.response.body.should.be.empty")
    public void responseBodyShouldBeEmpty() {
        then().assertThat().body("size()", is(0));
    }

    @Step("getLangValue:rest.step.response.contain.empty.list")
    public void responseShouldContainAnEmptyList() {
        then().assertThat().body("$", hasSize(0));
    }

    @Step("getLangValue:rest.step.response.by.path.contain.empty.list")
    public void responseByPathShouldContainAnEmptyList(String path) {
        then().assertThat().body(path, hasSize(0));
    }

    @Step("getLangValue:rest.step.response.body.with.no.content")
    public void responseBodyShouldBeWithNoContent() {
        then().assertThat().body(emptyString());
    }

    @Step("getLangValue:rest.step.response.body.by.path.should.be.empty")
    public void responseBodyByPathShouldBeEmpty(String path) {
        then().assertThat().body(path, is(0));
    }

    @Step("getLangValue:rest.step.response.body.by.path.should.be.not.empty")
    public void responseBodyByPathShouldBeNotEmpty(String path) {
        then().assertThat().body(path, notNullValue());
    }

    @Step("getLangValue:rest.step.response.time.seconds.should.be.greater.than")
    public void responseTimeInSecondsShouldBeGreaterThan(int seconds) {
        then().time(greaterThan(seconds * 1000L));
    }

    @Step("getLangValue:rest.step.response.time.seconds.should.be.less.than")
    public void responseTimeInSecondsShouldBeLessThan(int seconds) {
        then().time(lessThan(seconds * 1000L));
    }

    @Step("getLangValue:rest.step.response.time.ms.should.be.greater.than")
    public void responseTimeShouldBeGreaterThan(long milliseconds) {
        then().time(greaterThan(milliseconds));
    }

    @Step("getLangValue:rest.step.response.time.ms.should.be.less.than")
    public void responseTimeShouldBeLessThan(long milliseconds) {
        then().time(lessThan(milliseconds));
    }

    @Step("getLangValue:rest.step.response.status.code, 200")
    public void responseShouldBeSuccess() {
        then().assertThat().statusCode(200);
    }

    @Step("getLangValue:rest.step.response.status.code, 201")
    public void responseShouldBeSuccessCreated() {
        then().assertThat().statusCode(201);
    }

    @Step("getLangValue:rest.step.response.status.code, 203")
    public void responseShouldBeSuccessNonAuthoritativeInformation() {
        then().assertThat().statusCode(203);
    }

    @Step("getLangValue:rest.step.response.status.code, 204")
    public void responseShouldBeSuccessNoContent() {
        then().assertThat().statusCode(204);
    }

    @Step("getLangValue:rest.step.response.status.code, 206")
    public void responseShouldBeSuccessPartialContent() {
        then().assertThat().statusCode(206);
    }

    @Step("getLangValue:rest.step.response.status.code, 300")
    public void responseShouldBeMultipleChoice() {
        then().assertThat().statusCode(300);
    }

    @Step("getLangValue:rest.step.response.status.code, 301")
    public void responseShouldBeMovedPermanently() {
        then().assertThat().statusCode(301);
    }

    @Step("getLangValue:rest.step.response.status.code, 302")
    public void responseShouldBeFound() {
        then().assertThat().statusCode(302);
    }

    @Step("getLangValue:rest.step.response.status.code, 303")
    public void responseShouldBeSeeOther() {
        then().assertThat().statusCode(303);
    }

    @Step("getLangValue:rest.step.response.status.code, 400")
    public void responseShouldBeBadRequest() {
        then().assertThat().statusCode(400);
    }

    @Step("getLangValue:rest.step.response.status.code, 401")
    public void responseShouldBeUnauthorized() {
        then().assertThat().statusCode(401);
    }

    @Step("getLangValue:rest.step.response.status.code, 403")
    public void responseShouldBeForbidden() {
        then().assertThat().statusCode(403);
    }

    @Step("getLangValue:rest.step.response.status.code, 404")
    public void responseShouldBeNotFound() {
        then().assertThat().statusCode(404);
    }

    @Step("getLangValue:rest.step.response.status.code, 406")
    public void responseShouldBeNotAcceptable() {
        then().assertThat().statusCode(406);
    }

    @Step("getLangValue:rest.step.response.status.code, 409")
    public void responseShouldBeConflict() {
        then().assertThat().statusCode(409);
    }

    @Step("getLangValue:rest.step.response.status.code, 410")
    public void responseShouldBeGone() {
        then().assertThat().statusCode(410);
    }

    @Step("getLangValue:rest.step.response.status.code, 422")
    public void responseShouldBeUnprocessableEntity() {
        then().assertThat().statusCode(422);
    }

    @Step("getLangValue:rest.step.response.status.code, 424")
    public void responseShouldBeFailedDependency() {
        then().assertThat().statusCode(424);
    }

    @Step("getLangValue:rest.step.response.status.code, 425")
    public void responseShouldBeTooEarly() {
        then().assertThat().statusCode(425);
    }

    @Step("getLangValue:rest.step.response.status.code, 428")
    public void responseShouldBePreconditionRequired() {
        then().assertThat().statusCode(428);
    }

    @Step("getLangValue:rest.step.response.status.code, 500")
    public void responseShouldBeInternalServerError() {
        then().assertThat().statusCode(500);
    }

    @Step("getLangValue:rest.step.response.status.code, 501")
    public void responseShouldBeNotImplemented() {
        then().assertThat().statusCode(501);
    }

    @Step("getLangValue:rest.step.response.status.code, 502")
    public void responseShouldBeBadGateway() {
        then().assertThat().statusCode(502);
    }

    @Step("getLangValue:rest.step.response.status.code, 503")
    public void responseShouldBeServiceUnavailable() {
        then().assertThat().statusCode(503);
    }

    @Step("getLangValue:rest.step.response.status.code, 504")
    public void responseShouldBeGatewayTimeout() {
        then().assertThat().statusCode(504);
    }
}
