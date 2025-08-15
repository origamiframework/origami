package ru.origami.testit_allure.allure.java_commons.util;

public final class ExceptionUtils {

    private ExceptionUtils() {
        throw new IllegalStateException("Do not instance");
    }

    @SuppressWarnings("unchecked")
    public static <T extends Throwable> void sneakyThrow(final Throwable throwable) throws T {
        throw (T) throwable;
    }
}
