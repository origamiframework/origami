package ru.origami.testit_allure.allure.testfilter;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Common interface for all test plan implementations.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "version",
        defaultImpl = TestPlanUnknown.class
)
@JsonSubTypes({
        @JsonSubTypes.Type(TestPlanV1_0.class)
})
public interface TestPlan {
}
