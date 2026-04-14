package ru.origami.test_containers.parallel;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import ru.origami.common.parallel.EnvironmentContext;
import ru.origami.common.parallel.TestEnvironment;
import ru.origami.test_containers.TestContainersLauncher;

import java.lang.reflect.Method;

import static ru.origami.common.environment.Environment.EXECUTION_PARALLEL;

public class EnvironmentIsolationExtension implements InvocationInterceptor {

    @Override
    public void interceptTestMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> context,
                                    ExtensionContext extensionContext) throws Throwable {
        if ("true".equalsIgnoreCase(EXECUTION_PARALLEL)) {
            EnvironmentPool pool = TestContainersLauncher.getEnvironmentPool();
            TestEnvironment env = pool.acquire();

            try {
                EnvironmentContext.setCurrent(env);
                invocation.proceed();
            } finally {
                EnvironmentContext.clear();
                pool.release(env);
            }
        } else {
            invocation.proceed();
        }
    }
}
