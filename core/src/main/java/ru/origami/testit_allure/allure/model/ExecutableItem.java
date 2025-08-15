package ru.origami.testit_allure.allure.model;

/**
 * Marker interface for model objects that holds information about
 * executable items, like test results, fixture results or steps.
 *
 * @see TestResult
 * @see FixtureResult
 * @see StepResult
 */
public interface ExecutableItem extends WithAttachments,
        WithParameters, WithStatusDetails, WithSteps {

}
