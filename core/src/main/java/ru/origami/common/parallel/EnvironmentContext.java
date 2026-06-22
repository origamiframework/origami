package ru.origami.common.parallel;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.fail;
import static ru.origami.common.environment.Environment.PARALLEL_ENVIRONMENT_POOL;
import static ru.origami.common.environment.Language.getLangValue;

public class EnvironmentContext {

    private static final Map<Class<?>, TestEnvironment> CLASS_ENV_MAP = new ConcurrentHashMap<>();

    private static final Map<Class<?>, Boolean> IS_TEST_CLASS_CACHE = new ConcurrentHashMap<>();

    private static final Map<String, Class<?>> STACK_CLASS_CACHE = new ConcurrentHashMap<>();

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

        if (Objects.isNull(testClass)) {
            fail(getLangValue("test.containers.fail.get.current.test.env"));
        }

        return CLASS_ENV_MAP.computeIfAbsent(testClass, clazz -> PARALLEL_ENVIRONMENT_POOL.acquire());
    }

    public static TestEnvironment getCurrent(Class<?> testClass) {
        if (Objects.nonNull(testClass)) {
            return CLASS_ENV_MAP.computeIfAbsent(testClass, clazz -> PARALLEL_ENVIRONMENT_POOL.acquire());
        } else {
            return getCurrent();
        }
    }

    private static Class<?> findTestClassFromStackTrace() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();

        for (StackTraceElement element : stack) {
            String className = element.getClassName();

            if (isExcludedByPackage(className)) {
                continue;
            }

            Class<?> clazz = loadClass(className);

            if (Objects.isNull(clazz)) {
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
        return STACK_CLASS_CACHE.computeIfAbsent(className, cn -> {
            try {
                return Class.forName(cn);
            } catch (ClassNotFoundException e) {
                return null;
            }
        });
    }

    private static boolean isTestClass(Class<?> clazz) {
        return IS_TEST_CLASS_CACHE.computeIfAbsent(clazz, cls -> {
            // Игнорируем абстрактные классы (они обычно базовые)
            if (Modifier.isAbstract(cls.getModifiers())) {
                return false;
            }

            for (Method method : cls.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Test.class) ||
                        method.isAnnotationPresent(ParameterizedTest.class) ||
                        method.isAnnotationPresent(RepeatedTest.class) ||
                        method.isAnnotationPresent(TestFactory.class)) {
                    return true;
                }
            }

            if (cls.isAnnotationPresent(Nested.class)) {
                return true;
            }

            return false;
        });
    }

    public static void releaseForClass(Class<?> testClass) {
        TestEnvironment env = CLASS_ENV_MAP.remove(testClass);

        if (Objects.nonNull(env)) {
            PARALLEL_ENVIRONMENT_POOL.release(env);
        }
    }
}