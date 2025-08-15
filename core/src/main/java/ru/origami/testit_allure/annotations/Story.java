package ru.origami.testit_allure.annotations;

import ru.origami.testit_allure.allure.java_commons.LabelAnnotation;

import java.lang.annotation.*;

import static ru.origami.testit_allure.allure.java_commons.util.ResultsUtils.STORY_LABEL_NAME;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Repeatable(Stories.class)
@LabelAnnotation(name = STORY_LABEL_NAME)
public @interface Story {

    String value();
}
