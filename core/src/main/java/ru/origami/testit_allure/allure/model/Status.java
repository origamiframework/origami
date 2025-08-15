package ru.origami.testit_allure.allure.model;

/**
 * Test statuses.
 *
 * @see ru.origami.testit_allure.allure.model.WithStatus
 */
public enum Status {

    /**
     * Marks tests that have some failed checks (assertions).
     */
    FAILED("failed"),
    /**
     * Marks tests with unexpected failures during test execution.
     */
    BROKEN("broken"),
    /**
     * Marks passed tests.
     */
    PASSED("passed"),
    /**
     * Marks skipped/interrupted tests.
     */
    SKIPPED("skipped");

    private final String value;

    Status(final String v) {
        value = v;
    }

    /**
     * From value status.
     *
     * @param v the v
     * @return the status
     */
    public static Status fromValue(final String v) {
        for (Status c : Status.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

    /**
     * Value string.
     *
     * @return the string
     */
    public String value() {
        return value;
    }

}
