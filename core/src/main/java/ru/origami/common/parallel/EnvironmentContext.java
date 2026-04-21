package ru.origami.common.parallel;

import ru.origami.common.environment.Environment;

public class EnvironmentContext {

    private static final ThreadLocal<TestEnvironment> current = new ThreadLocal<>();

    public static void setCurrent(TestEnvironment env) {
        current.set(env);
    }

    public static TestEnvironment getCurrent() {
        TestEnvironment env = current.get();

        if (env == null) {
            env = Environment.getParallelEnvironmentPool().acquire();
            current.set(env);
        }

        return env;
    }

    public static void clear() {
        TestEnvironment env = current.get();

        if (env != null) {
            Environment.getParallelEnvironmentPool().release(env);
            current.remove();
        }
    }
}
