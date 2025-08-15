package ru.origami.testit_allure.annotations;

import java.lang.annotation.*;

import static ru.origami.testit_allure.allure.java_commons.util.ResultsUtils.CUSTOM_LINK_TYPE;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
@Repeatable(LinkAnnotations.class)
public @interface LinkAnnotation {

    String DEFAULT_VALUE = "$$$$$$$$__value__$$$$$$$$";

    String value() default DEFAULT_VALUE;

    String allureType() default CUSTOM_LINK_TYPE;

    String url() default "";
}
