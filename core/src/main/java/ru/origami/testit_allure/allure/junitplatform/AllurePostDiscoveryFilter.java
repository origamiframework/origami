package ru.origami.testit_allure.allure.junitplatform;

import org.junit.platform.engine.FilterResult;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestTag;
import org.junit.platform.launcher.PostDiscoveryFilter;
import ru.origami.testit_allure.allure.java_commons.util.AnnotationUtils;
import ru.origami.testit_allure.allure.testfilter.FileTestPlanSupplier;
import ru.origami.testit_allure.allure.testfilter.TestPlan;
import ru.origami.testit_allure.allure.testfilter.TestPlanV1_0;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.origami.testit_allure.allure.java_commons.util.ResultsUtils.ALLURE_ID_LABEL_NAME;

public class AllurePostDiscoveryFilter implements PostDiscoveryFilter {

    private static final Pattern ID_TAG = Pattern.compile("^@?allure\\.id[:=](?<id>.+)$");

    private final TestPlan testPlan;

    public AllurePostDiscoveryFilter() {
        this(new FileTestPlanSupplier().supply().orElse(null));
    }

    public AllurePostDiscoveryFilter(final TestPlan testPlan) {
        this.testPlan = testPlan;
    }

    @Override
    public FilterResult apply(final TestDescriptor object) {
        if (Objects.isNull(testPlan)) {
            return FilterResult.included("test plan is empty");
        }
        if (!object.getChildren().isEmpty()) {
            return FilterResult.included("filter only applied for tests");
        }

        final String allureId = findAllureId(object);
        final String uniqueId = object.getUniqueId().toString();
        final String fullName = object.getSource()
                .flatMap(AllureJunitPlatformUtils::getFullName)
                .orElse(null);

        return FilterResult.includedIf(
                isIncluded(testPlan, allureId, uniqueId, fullName)
        );
    }

    private boolean isIncluded(final TestPlan testPlan,
                               final String allureId,
                               final String uniqueId,
                               final String fullName) {
        if (testPlan instanceof TestPlanV1_0) {
            final TestPlanV1_0 tp = (TestPlanV1_0) testPlan;
            return Objects.isNull(tp.getTests()) || tp.getTests()
                    .stream()
                    .filter(Objects::nonNull)
                    .anyMatch(tc -> match(tc, allureId, uniqueId, fullName));
        }
        return true;
    }

    @SuppressWarnings("BooleanExpressionComplexity")
    private boolean match(final TestPlanV1_0.TestCase tc,
                          final String allureId,
                          final String uniqueId,
                          final String fullName) {
        return Objects.nonNull(tc.getId()) && tc.getId().equals(allureId)
                || Objects.nonNull(tc.getSelector()) && tc.getSelector().equals(uniqueId)
                || Objects.nonNull(tc.getSelector()) && tc.getSelector().equals(fullName);
    }

    private String findAllureId(final TestDescriptor object) {
        return object.getSource()
                .flatMap(AllureJunitPlatformUtils::getTestMethod)
                .flatMap(this::findAllureId)
                .orElseGet(() -> findAllureId(object.getTags()));
    }

    private String findAllureId(final Collection<TestTag> tags) {
        return tags.stream()
                .map(TestTag::getName)
                .map(t -> {
                    final Matcher matcher = ID_TAG.matcher(t);
                    return matcher.matches()
                            ? Optional.ofNullable(matcher.group("id"))
                            : Optional.<String>empty();
                })
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(Objects::nonNull)
                .findAny()
                .orElse(null);
    }

    private Optional<String> findAllureId(final Method value) {
        return AnnotationUtils.getLabels(value)
                .stream()
                .filter(l -> ALLURE_ID_LABEL_NAME.equals(l.getName()))
                .map(ru.origami.testit_allure.allure.model.Label::getValue)
                .findAny();
    }

}
