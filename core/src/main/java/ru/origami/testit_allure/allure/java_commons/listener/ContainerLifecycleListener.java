package ru.origami.testit_allure.allure.java_commons.listener;

import ru.origami.testit_allure.allure.model.TestResultContainer;

/**
 * Notifies about Allure test container lifecycle.
 *
 * @since 2.0
 */
public interface ContainerLifecycleListener extends LifecycleListener {

    default void beforeContainerStart(final TestResultContainer container) {
        //do nothing
    }

    default void afterContainerStart(final TestResultContainer container) {
        //do nothing
    }

    default void beforeContainerUpdate(final TestResultContainer container) {
        //do nothing
    }

    default void afterContainerUpdate(final TestResultContainer container) {
        //do nothing
    }

    default void beforeContainerStop(final TestResultContainer container) {
        //do nothing
    }

    default void afterContainerStop(final TestResultContainer container) {
        //do nothing
    }

    default void beforeContainerWrite(final TestResultContainer container) {
        //do nothing
    }

    default void afterContainerWrite(final TestResultContainer container) {
        //do nothing
    }

}
