package ru.origami.testit_allure.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Description {

    String value() default "";

    boolean useJavaDoc() default false;

}
