package ru.origami.common.parallel;

import ru.origami.common.environment.Environment;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EnvironmentContext {

    private static final Map<Class<?>, TestEnvironment> CLASS_ENV_MAP = new ConcurrentHashMap<>();

    private static final Map<Class<?>, Boolean> IS_TEST_CLASS_CACHE = new ConcurrentHashMap<>();

    private static final Set<String> EXCLUDED_PACKAGE_PREFIXES = Set.of(
            "ru.origami.",
            "java.",
            "javax.",
            "jdk.",
            "org.junit.",
            "org.testcontainers.",
            "org.apache.maven.",
            "org.gradle.",
            "com.intellij.",
            "sun."
    );

    public static TestEnvironment getCurrent() {
        Class<?> testClass = findTestClassFromStackTrace();

        if (testClass == null) {
            throw new IllegalStateException("Cannot determine test class from stack trace. " +
                    "Make sure test classes have @Test, @ParameterizedTest, etc.");
        }

        return CLASS_ENV_MAP.computeIfAbsent(testClass, clazz ->
                Environment.getParallelEnvironmentPool().acquire());
    }

    private static Class<?> findTestClassFromStackTrace() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();

        for (StackTraceElement element : stack) {
            String className = element.getClassName();

            if (isExcludedByPackage(className)) {
                continue;
            }

            Class<?> clazz = loadClass(className);

            if (clazz == null) {
                continue;
            }

            if (isTestClass(clazz)) {
                return clazz;
            }
        }

        return null;
    }

    private static boolean isExcludedByPackage(String className) {
        for (String prefix : EXCLUDED_PACKAGE_PREFIXES) {
            if (className.startsWith(prefix)) {
                return true;
            }
        }

        return false;
    }

    private static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static boolean isTestClass(Class<?> clazz) {
        return IS_TEST_CLASS_CACHE.computeIfAbsent(clazz, cls -> {
            for (Method method : cls.getDeclaredMethods()) {
                if (method.isAnnotationPresent(org.junit.jupiter.api.Test.class) ||
                        method.isAnnotationPresent(org.junit.jupiter.params.ParameterizedTest.class) ||
                        method.isAnnotationPresent(org.junit.jupiter.api.RepeatedTest.class) ||
                        method.isAnnotationPresent(org.junit.jupiter.api.TestFactory.class)) {
                    return true;
                }
            }

            if (java.lang.reflect.Modifier.isAbstract(cls.getModifiers())) {
                return false;
            }

            return false;
        });
    }

    public static void releaseForClass(Class<?> testClass) {
        TestEnvironment env = CLASS_ENV_MAP.remove(testClass);

        if (env != null) {
            Environment.getParallelEnvironmentPool().release(env);
        }
    }
}