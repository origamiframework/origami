package ru.origami.testit_allure.allure.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * The model object that could be used to pass additional metadata to test results.
 * Note that labels with empty (blank) name will be omitted during report generation.
 *
 * @see ru.origami.testit_allure.allure.model.TestResult
 */
public class Label implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String value;

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param value the value
     * @return self for method chaining
     */
    public Label setName(final String value) {
        this.name = value;
        return this;
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets value.
     *
     * @param value the value
     * @return self for method chaining
     */
    public Label setValue(final String value) {
        this.value = value;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Label label = (Label) o;
        return Objects.equals(name, label.name) && Objects.equals(value, label.value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }
}
