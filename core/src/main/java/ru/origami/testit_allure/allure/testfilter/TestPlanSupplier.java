package ru.origami.testit_allure.allure.testfilter;

import java.util.Optional;

/**
 * Marker interface for all test plan suppliers.
 */
@FunctionalInterface
public interface TestPlanSupplier {

    /**
     * Supply test plan.
     *
     * @return the test plan.
     */
    Optional<TestPlan> supply();

}
