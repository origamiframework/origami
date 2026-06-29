package ru.origami.common.parallel;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.fail;
import static ru.origami.common.environment.Environment.EXECUTION_PARALLEL;
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

    public static TestEnvironment getCurrent(Class<?> testClass) {
        if (EXECUTION_PARALLEL) {
            if (Objects.isNull(testClass)) {
                testClass = resolveCanonicalClass();

                if (Objects.isNull(testClass)) {
                    fail(getLangValue("test.containers.fail.get.current.test.env"));
                }
            }

            if (testClass.equals(DisabledTestClass.class)) {
                return new TestEnvironment(-1);
            }

            return acquireForClass(testClass);
        }

        return null;
    }

    private static Class<?> resolveCanonicalClass() {
        List<Class<?>> stackClasses = findTestClassesFromStackTrace();

        if (stackClasses.isEmpty()) {
            return null;
        } else if (stackClasses.size() == 1 && stackClasses.getFirst().equals(DisabledTestClass.class)) {
            return stackClasses.getFirst();
        }

        for (Class<?> candidate : stackClasses) {
            List<Class<?>> matches = CLASS_ENV_MAP.keySet()
                    .stream()
                    .filter(active -> candidate.equals(active) || candidate.isAssignableFrom(active))
                    .toList();

            if (matches.size() == 1) {
                return matches.get(0);
            }

            if (matches.size() > 1) {
                fail(getLangValue("test.containers.ambiguous.test.env")
                        .formatted(candidate.getName(), matches.stream()
                                .map(Class::getName)
                                .collect(Collectors.joining(", "))));
            }
        }

        return stackClasses.get(0);
    }

    private static TestEnvironment acquireForClass(Class<?> testClass) {
        TestEnvironment env = CLASS_ENV_MAP.get(testClass);

        if (Objects.nonNull(env)) {
            return env;
        }

        TestEnvironment existing = CLASS_ENV_MAP.get(testClass);

        if (Objects.isNull(existing)) {
            TestEnvironment acquired = PARALLEL_ENVIRONMENT_POOL.acquire();
            existing = CLASS_ENV_MAP.putIfAbsent(testClass, acquired);

            if (Objects.nonNull(existing)) {
                // Другой поток уже привязал среду к этому классу - возвращаем лишнюю в пул.
                PARALLEL_ENVIRONMENT_POOL.release(acquired);

                return existing;
            } else {
                return acquired;
            }
        }

        return existing;
    }

    private static List<Class<?>> findTestClassesFromStackTrace() {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        List<Class<?>> result = new ArrayList<>();

        for (int i = stack.length - 1; i >= 0; i--) {
            String className = stack[i].getClassName();

            if (isExcludedByPackage(className)) {
                continue;
            }

            Class<?> clazz = loadClass(className);

            if (Objects.isNull(clazz)) {
                continue;
            }

            if (isTestClass(clazz) && !result.contains(clazz)) {
                if (clazz.isAnnotationPresent(Disabled.class)) {
                    result.add(loadClass(DisabledTestClass.class.getName()));
                } else {
                    result.add(clazz);
                }
            }
        }

        return result;
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
        if (EXECUTION_PARALLEL) {
            TestEnvironment env = CLASS_ENV_MAP.get(testClass);

            if (Objects.nonNull(env)) {
                PARALLEL_ENVIRONMENT_POOL.release(env);
            }
        }
    }
}