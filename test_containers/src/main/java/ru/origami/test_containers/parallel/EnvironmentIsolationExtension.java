package ru.origami.test_containers.parallel;

import org.junit.jupiter.api.extension.*;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import ru.origami.common.parallel.EnvironmentContext;

public class EnvironmentIsolationExtension implements BeforeAllCallback, AfterAllCallback, TestExecutionListener {

    @Override
    public void beforeAll(ExtensionContext context) {
        EnvironmentContext.getCurrent(context.getRequiredTestClass());
    }

    @Override
    public void executionSkipped(TestIdentifier testIdentifier, String reason) {
        if (testIdentifier.isContainer() && !testIdentifier.isTest()) {
            String className = testIdentifier.getUniqueId().replaceAll("^.*\\[class:(.*)\\]$", "$1");

            try {
                Class<?> testClass = Class.forName(className);
                EnvironmentContext.releaseForClass(testClass);
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void afterAll(ExtensionContext context) {
        EnvironmentContext.releaseForClass(context.getRequiredTestClass());
    }
}
