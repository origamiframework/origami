package ru.origami.testit_allure.allure.testfilter;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@JsonTypeName("1.0")
@SuppressWarnings({"TypeName", "PMD.CyclomaticComplexity"})
public class TestPlanV1_0 implements TestPlan, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * List of tests to run.
     */
    private List<TestCase> tests;

    /**
     * Gets the value of the tests property.
     *
     * @return tests.
     */
    public List<TestCase> getTests() {
        return tests;
    }

    /**
     * Sets the value of the tests property.
     *
     * @param tests the value to set.
     * @return current instance.
     */
    public TestPlanV1_0 setTests(final List<TestCase> tests) {
        this.tests = tests;
        return this;
    }

    /**
     * @param allureId value of @AllureId annotation, if not set pass null value
     * @param selector selector of test method
     * @return true if test case with given allureId or selection is selected, false otherwise.
     */
    public boolean isSelected(final String allureId, final String selector) {
        return getTests().stream().map(test -> new String[]{test.getId(), test.getSelector()})
                .flatMap(array -> Arrays.stream(array).filter(Objects::nonNull))
                .anyMatch(item -> item.equals(selector) || item.equals(allureId));
    }

    /**
     * Test plan test case. At least one of {@link #id} and {@link #selector} should be specified.
     */
    public static class TestCase implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * Test case id. Can be specified by providing {@code AS_ID} label.
         */
        private String id;

        /**
         * Selector that can be used to run test case. Some sort of unique id or full name of test.
         */
        private String selector;

        /**
         * Gets the value of the id property.
         *
         * @return id
         */
        public String getId() {
            return id;
        }

        /**
         * Sets the value of the id property.
         *
         * @param id the id to set.
         * @return current instance.
         */
        public TestCase setId(final String id) {
            this.id = id;
            return this;
        }

        /**
         * Gets the value of the selector property.
         *
         * @return id
         */
        public String getSelector() {
            return selector;
        }

        /**
         * Sets the value of the selector property.
         *
         * @param selector the selector to set.
         * @return current instance.
         */
        public TestCase setSelector(final String selector) {
            this.selector = selector;
            return this;
        }
    }
}
