package ru.origami.testit_allure.allure.java_commons.listener;

import ru.origami.testit_allure.allure.model.StepResult;

/**
 * Notifies about Allure step lifecycle events.
 *
 * @since 2.0
 */
public interface StepLifecycleListener extends LifecycleListener {

    default void beforeStepStart(final StepResult result) {
        //do nothing
    }

    default void afterStepStart(final StepResult result) {
        //do nothing
    }

    default void beforeStepUpdate(final StepResult result) {
        //do nothing
    }

    default void afterStepUpdate(final StepResult result) {
        //do nothing
    }

    default void beforeStepStop(final StepResult result) {
        //do nothing
    }

    default void afterStepStop(final StepResult result) {
        //do nothing
    }

}
