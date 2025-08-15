package ru.origami.testit_allure.allure.java_commons;

import ru.origami.testit_allure.annotations.LinkAnnotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static ru.origami.testit_allure.allure.java_commons.util.ResultsUtils.TMS_LINK_TYPE;

/**
 * Used to link tests with test cases in external test management system.
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@LinkAnnotation(allureType = TMS_LINK_TYPE)
@Repeatable(TmsLinks.class)
public @interface TmsLink {

    String value();

}
