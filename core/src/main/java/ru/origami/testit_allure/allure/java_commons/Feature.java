package ru.origami.testit_allure.allure.java_commons;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static ru.origami.testit_allure.allure.java_commons.util.ResultsUtils.FEATURE_LABEL_NAME;

/**
 * Used to mark tests with feature label.
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Repeatable(Features.class)
@LabelAnnotation(name = FEATURE_LABEL_NAME)
public @interface Feature {

    String value();

}
