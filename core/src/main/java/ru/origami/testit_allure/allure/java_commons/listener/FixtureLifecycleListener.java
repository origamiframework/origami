package ru.origami.testit_allure.allure.java_commons.listener;

import ru.origami.testit_allure.allure.model.FixtureResult;

/**
 * Notifies about Allure test fixtures lifecycle events.
 *
 * @since 2.0
 */
public interface FixtureLifecycleListener extends LifecycleListener {

    default void beforeFixtureStart(final FixtureResult result) {
        //do nothing
    }

    default void afterFixtureStart(final FixtureResult result) {
        //do nothing
    }

    default void beforeFixtureUpdate(final FixtureResult result) {
        //do nothing
    }

    default void afterFixtureUpdate(final FixtureResult result) {
        //do nothing
    }

    default void beforeFixtureStop(final FixtureResult result) {
        //do nothing
    }

    default void afterFixtureStop(final FixtureResult result) {
        //do nothing
    }

}
