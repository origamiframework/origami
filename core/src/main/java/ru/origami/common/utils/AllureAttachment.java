package ru.origami.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.origami.testit_allure.allure.java_commons.Attachment;
import ru.origami.testit_allure.annotations.Description;
import ru.origami.testit_allure.annotations.Step;

import static ru.origami.testit_allure.test_it.testit.aspects.StepAspect.TEST_IT_ATTACHMENT_TECH_STEP_VALUE;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AllureAttachment {

    @Attachment(value = "{0}.fail")
    @Step(TEST_IT_ATTACHMENT_TECH_STEP_VALUE)
    @Description("{0} {1}")
    public static byte[] attachFailMethodToAllure(String methodName, String trace) {
        return trace.getBytes();
    }
}
