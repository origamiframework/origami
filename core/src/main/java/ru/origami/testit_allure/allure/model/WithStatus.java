package ru.origami.testit_allure.allure.model;

/**
 * The marker interface for model objects with status.
 *
 * @see TestResult
 * @see FixtureResult
 * @see StepResult
 * @see ExecutableItem
 */
public interface WithStatus {

    /**
     * Gets status.
     *
     * @return the status
     */
    Status getStatus();

}
