package ru.origami.testit_allure.allure.java_commons;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static ru.origami.testit_allure.allure.java_commons.util.ResultsUtils.OWNER_LABEL_NAME;

/**
 * This annotation used to specify owner for test case.
 *
 * @see Lead
 * @see LabelAnnotation
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@LabelAnnotation(name = OWNER_LABEL_NAME)
public @interface Owner {

    String value();

}
