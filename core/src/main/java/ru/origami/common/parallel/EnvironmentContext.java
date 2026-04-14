package ru.origami.common.parallel;

public class EnvironmentContext {

    private static final ThreadLocal<TestEnvironment> current = new ThreadLocal<>();

    public static void setCurrent(TestEnvironment env) {
        current.set(env);
    }

    public static TestEnvironment getCurrent() {
        return current.get();
    }

    public static void clear() {
        current.remove();
    }
}
