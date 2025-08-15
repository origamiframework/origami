package ru.origami.testit_allure.test_it.testit.listener;

import ru.origami.testit_allure.test_it.testit.models.TestResult;

public interface AdapterListener extends DefaultListener {
    default void beforeTestStop(final TestResult result) {
        //do nothing
    }
}
