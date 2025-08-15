package ru.origami.testit_allure.allure.java_commons;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static ru.origami.testit_allure.allure.java_commons.util.ResultsUtils.ALLURE_ID_LABEL_NAME;

/**
 * Used by Allure Enterprise to link test cases with related test methods.
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@LabelAnnotation(name = ALLURE_ID_LABEL_NAME)
public @interface AllureId {

    String value();

}
