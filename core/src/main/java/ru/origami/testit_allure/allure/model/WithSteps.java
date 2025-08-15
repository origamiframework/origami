package ru.origami.testit_allure.allure.model;

import java.util.List;

/**
 * The marker interface for model objects with steps.
 *
 * @see TestResult
 * @see FixtureResult
 * @see StepResult
 * @see ExecutableItem
 */
public interface WithSteps {

    /**
     * Gets steps.
     *
     * @return the steps
     */
    List<StepResult> getSteps();

}
