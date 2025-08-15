package ru.origami.testit_allure.allure.java_commons.listener;

import ru.origami.testit_allure.allure.model.TestResult;

/**
 * Listener that notifies about Allure Lifecycle events.
 *
 * @since 2.0
 */
public interface TestLifecycleListener extends LifecycleListener {

    default void beforeTestSchedule(final TestResult result) {
        //do nothing
    }

    default void afterTestSchedule(final TestResult result) {
        //do nothing
    }

    default void beforeTestUpdate(final TestResult result) {
        //do nothing
    }

    default void afterTestUpdate(final TestResult result) {
        //do nothing
    }

    default void beforeTestStart(final TestResult result) {
        //do nothing
    }

    default void afterTestStart(final TestResult result) {
        //do nothing
    }

    default void beforeTestStop(final TestResult result) {
        //do nothing
    }

    default void afterTestStop(final TestResult result) {
        //do nothing
    }

    default void beforeTestWrite(final TestResult result) {
        //do nothing
    }

    default void afterTestWrite(final TestResult result) {
        //do nothing
    }

}
