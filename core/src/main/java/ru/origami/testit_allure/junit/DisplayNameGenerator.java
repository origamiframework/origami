package ru.origami.testit_allure.junit;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;

import java.lang.reflect.Method;

public class DisplayNameGenerator extends org.junit.jupiter.api.DisplayNameGenerator.Standard {

    @Override
    public String generateDisplayNameForClass(Class<?> testClass) {
        return testClass.isAnnotationPresent(DisplayName.class)
                ? testClass.getAnnotation(DisplayName.class).value()
                : super.generateDisplayNameForClass(testClass);
    }

    @Override
    public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
        if (testMethod.isAnnotationPresent(DisplayName.class)){
            return testMethod.getAnnotation(DisplayName.class).value();
        } else if (testMethod.isAnnotationPresent(ParameterizedTest.class)) {
            return testMethod.getAnnotation(ParameterizedTest.class).name();
        } else {
            return super.generateDisplayNameForMethod(testClass, testMethod);
        }
    }
}
