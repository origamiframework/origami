package ru.origami.testit_allure.annotations;

import java.lang.annotation.*;

/**
 * Wrapper annotation for {@link LinkAnnotation}.
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface LinkAnnotations {

    LinkAnnotation[] value();

}
