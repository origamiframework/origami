package ru.origami.test_containers.parallel;

import org.junit.jupiter.api.extension.*;
import ru.origami.common.parallel.EnvironmentContext;
import ru.origami.common.parallel.TestEnvironment;
import ru.origami.test_containers.TestContainersLauncher;

import static ru.origami.common.environment.Environment.EXECUTION_PARALLEL;
import static ru.origami.common.environment.Environment.TEST_CONTAINERS_ENABLED;

public class EnvironmentIsolationExtension implements AfterEachCallback {

//    @Override
//    public void interceptTestMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> context,
//                                    ExtensionContext extensionContext) throws Throwable {
//        if ("true".equalsIgnoreCase(TEST_CONTAINERS_ENABLED) && "true".equalsIgnoreCase(EXECUTION_PARALLEL)) {
//            EnvironmentPool pool = TestContainersLauncher.getEnvironmentPool();
//            TestEnvironment env = pool.acquire();
//
//            try {
//                EnvironmentContext.setCurrent(env);
//                invocation.proceed();
//            } finally {
//                EnvironmentContext.clear();
//                pool.release(env);
//            }
//        } else {
//            invocation.proceed();
//        }
//    }

//    @Override
//    public void beforeEach(ExtensionContext context) throws InterruptedException {
//        if ("true".equalsIgnoreCase(TEST_CONTAINERS_ENABLED) && "true".equalsIgnoreCase(EXECUTION_PARALLEL)) {
//            ru.origami.common.parallel.EnvironmentPool pool = TestContainersLauncher.getEnvironmentPool();
//            TestEnvironment env = pool.acquire();
//            EnvironmentContext.setCurrent(env);
//        }
//    }

    @Override
    public void afterEach(ExtensionContext context) {
        if ("true".equalsIgnoreCase(TEST_CONTAINERS_ENABLED) && "true".equalsIgnoreCase(EXECUTION_PARALLEL)) {
//            TestEnvironment env = EnvironmentContext.getCurrent();
//
//            if (env != null) {
                EnvironmentContext.clear();
//                TestContainersLauncher.getEnvironmentPool().release(env);
//            }
        }
    }
}
