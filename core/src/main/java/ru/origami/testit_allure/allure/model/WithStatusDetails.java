package ru.origami.testit_allure.allure.model;

/**
 * The marker interface for model objects with status details.
 *
 * @see TestResult
 * @see FixtureResult
 * @see StepResult
 * @see ExecutableItem
 * @see WithStatus
 */
public interface WithStatusDetails extends WithStatus {

    /**
     * Gets status details.
     *
     * @return the status details
     */
    StatusDetails getStatusDetails();

}
