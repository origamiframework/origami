package ru.origami.testit_allure.allure.model;

import java.util.List;

/**
 * The marker interface for model objects with links.
 *
 * @see TestResult
 * @see FixtureResult
 * @see StepResult
 * @see ExecutableItem
 */
public interface WithLinks {

    /**
     * Gets links.
     *
     * @return the links
     */
    List<Link> getLinks();

}
