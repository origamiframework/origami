package ru.origami.testit_allure.allure.junitplatform;

import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.stream.Stream;

/* package-private */ final class AllureJunitPlatformUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(AllureJunitPlatformUtils.class);

    private AllureJunitPlatformUtils() {
        throw new IllegalStateException("do not instance");
    }

    public static Optional<String> getFullName(final TestSource source) {
        if (source instanceof MethodSource) {
            final MethodSource ms = (MethodSource) source;
            return Optional.of(String.format("%s.%s", ms.getClassName(), ms.getMethodName()));
        }
        if (source instanceof ClassSource) {
            final ClassSource cs = (ClassSource) source;
            return Optional.ofNullable(cs.getClassName());
        }
        return Optional.empty();
    }

    public static Optional<Class<?>> getTestClass(final TestSource source) {
        if (source instanceof MethodSource) {
            return getTestClass(((MethodSource) source).getClassName());
        }
        if (source instanceof ClassSource) {
            return Optional.of(((ClassSource) source).getJavaClass());
        }
        return Optional.empty();
    }

    public static Optional<Class<?>> getTestClass(final String className) {
        try {
            return Optional.of(Class.forName(className));
        } catch (ClassNotFoundException e) {
            LOGGER.trace("Could not get test class from test source {}", className, e);
        }
        return Optional.empty();
    }

    public static Optional<Method> getTestMethod(final TestSource source) {
        if (source instanceof MethodSource) {
            return getTestMethod((MethodSource) source);
        }
        return Optional.empty();
    }

    public static Optional<Method> getTestMethod(final MethodSource source) {
        try {
            final Class<?> aClass = Class.forName(source.getClassName());
            return Stream.of(aClass.getDeclaredMethods())
                    .filter(method -> MethodSource.from(method).equals(source))
                    .findAny();
        } catch (ClassNotFoundException e) {
            LOGGER.trace("Could not get test method from method source {}", source, e);
        }
        return Optional.empty();
    }
}
