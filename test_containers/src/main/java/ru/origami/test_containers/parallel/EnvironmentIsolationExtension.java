package ru.origami.test_containers.parallel;

import org.junit.jupiter.api.extension.*;
import ru.origami.common.parallel.EnvironmentContext;

public class EnvironmentIsolationExtension implements AfterAllCallback {

    @Override
    public void afterAll(ExtensionContext context) {
        Class<?> testClass = context.getRequiredTestClass();
        EnvironmentContext.releaseForClass(testClass);
    }
}
