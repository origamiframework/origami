package ru.origami.testit_allure.allure.model;

/**
 * Test stages.
 */
public enum Stage {

    /**
     * Scheduled stage.
     */
    SCHEDULED("scheduled"),
    /**
     * Running stage.
     */
    RUNNING("running"),
    /**
     * Finished stage.
     */
    FINISHED("finished"),
    /**
     * Pending stage.
     */
    PENDING("pending"),
    /**
     * Interrupted stage.
     */
    INTERRUPTED("interrupted");

    private final String value;

    Stage(final String v) {
        value = v;
    }

    /**
     * From value stage.
     *
     * @param v the v
     * @return the stage
     */
    public static Stage fromValue(final String v) {
        for (Stage c : Stage.values()) {
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
