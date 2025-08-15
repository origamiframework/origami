package ru.origami.websocket.attachment;

import ru.origami.testit_allure.allure.java_commons.Attachment;
import ru.origami.testit_allure.annotations.Description;
import ru.origami.testit_allure.annotations.Step;

import static ru.origami.testit_allure.test_it.testit.aspects.StepAspect.TEST_IT_ATTACHMENT_TECH_STEP_VALUE;

public class WsAttachment {

    @Attachment(value = "{0}.results")
    @Step(TEST_IT_ATTACHMENT_TECH_STEP_VALUE)
    @Description("{1}")
    public static String attachResultsToAllure(String topic, String message) {
        return message;
    }
}
