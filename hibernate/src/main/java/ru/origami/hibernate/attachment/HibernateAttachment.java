package ru.origami.hibernate.attachment;

import lombok.extern.slf4j.Slf4j;
import ru.origami.hibernate.models.QueryParameter;
import ru.origami.testit_allure.allure.java_commons.Attachment;
import ru.origami.testit_allure.annotations.Description;
import ru.origami.testit_allure.annotations.Step;

import java.util.List;
import java.util.Objects;

import static ru.origami.common.environment.Language.getLangValue;
import static ru.origami.hibernate.attachment.QueryFormatter.getSqlQuery;
import static ru.origami.hibernate.attachment.QueryResultFormatter.getResult;
import static ru.origami.hibernate.attachment.QueryResultFormatter.getResultOfUpdate;
import static ru.origami.common.environment.Environment.*;
import static ru.origami.testit_allure.test_it.testit.aspects.StepAspect.TEST_IT_ATTACHMENT_TECH_STEP_VALUE;

@Slf4j
public class HibernateAttachment {

    @Attachment(value = "sql.query")
    public static byte[] attachSqlQueryToAllure(String queryString, List<QueryParameter> parameters) {
        String query = Objects.requireNonNull(getSqlQuery(queryString, parameters), getLangValue("hibernate.null.attachment"));
        attachQueryToTestIT(query);
        showSql(query);

        return query.getBytes();
    }

    public static void attachSqlResultToAllure(List<Object> resultObject) {
        if (isExcelResultEnabled()) {
            attachSqlResult(resultObject);
        }
    }

    @Attachment(value = "sql.result", fileExtension = "xlsx")
    private static byte[] attachSqlResult(List<Object> resultObject) {
        byte[] result = getResult(resultObject);

        if (result == null) {
            result = getLangValue("hibernate.empty.result").getBytes();
        }

        return result;
    }

    @Attachment(value = "sql.result")
    public static byte[] attachSqlResultToAllure(String query, int resultRows) {
        String result = getResultOfUpdate(query, resultRows);
        showResult(result);

        return result.getBytes();
    }

    @Attachment(value = "{0}")
    public static String attachExceptionToAllure(String exception, String stackTrace) {
        return stackTrace;
    }

    public static void showSql(String query) {
        if (isLocal() || isLoggingEnabled()) {
            log.info("{}:\n{}\n", getLangValue("hibernate.query.word"), query);
        }
    }

    public static void showResult(String result) {
        if ((isLocal() || isLoggingEnabled()) && Objects.nonNull(result)) {
            log.info("{}: {}", getLangValue("hibernate.result.word"), result);
        }
    }

    @Step(TEST_IT_ATTACHMENT_TECH_STEP_VALUE)
    @Description("{0}")
    private static void attachQueryToTestIT(String query) {
    }
}
