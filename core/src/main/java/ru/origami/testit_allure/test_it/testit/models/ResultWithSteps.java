package ru.origami.testit_allure.test_it.testit.models;

import java.util.List;

/**
 * The marker interface for model objects with steps.
 */
public interface ResultWithSteps {

    /**
     * Gets steps.
     *
     * @return the steps
     */
    List<StepResult> getSteps();
}
