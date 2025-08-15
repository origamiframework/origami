package ru.origami.testit_allure.allure.model;

import java.util.List;

/**
 * The marker interface for model objects that could be parameterised.
 *
 * @see TestResult
 * @see FixtureResult
 * @see StepResult
 * @see ExecutableItem
 */
public interface WithParameters {

    /**
     * Gets parameters.
     *
     * @return the parameters
     */
    List<Parameter> getParameters();

}
