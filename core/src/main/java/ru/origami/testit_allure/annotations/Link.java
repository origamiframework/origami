package ru.origami.testit_allure.annotations;

import ru.origami.testit_allure.allure.java_commons.util.ResultsUtils;
import ru.origami.testit_allure.test_it.testit.models.LinkType;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@LinkAnnotation
@Repeatable(Links.class)
public @interface Link {

    String value() default "";

    String name() default "";

    String url() default "";

    String allureType() default ResultsUtils.CUSTOM_LINK_TYPE;

    LinkType testItType() default LinkType.REQUIREMENT;
}
