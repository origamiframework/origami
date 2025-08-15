package ru.origami.testit_allure.allure.java_commons.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Spliterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.fail;
import static ru.origami.common.environment.Language.getLangValue;

public final class NamingUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(NamingUtils.class);

    private static final Collector<CharSequence, ?, String> JOINER = Collectors.joining(", ", "[", "]");

    private NamingUtils() {
        throw new IllegalStateException("Do not instance");
    }

    public static String processNameTemplate(final String template, final Map<String, Object> params) {
        final Matcher matcher = Pattern.compile("\\{([^}]*)}").matcher(template);
        final StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            final String pattern = matcher.group(1);
            final String replacement = processPattern(pattern, params, template).orElseGet(matcher::group);
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private static Optional<String> processPattern(final String pattern, final Map<String, Object> params, String template) {
        if (pattern.isEmpty()) {
            LOGGER.error("Could not process empty pattern");
            return Optional.empty();
        }
        final String[] parts = pattern.split("\\.");
        final String parameterName = parts[0];

        if (!params.containsKey(parameterName)) {
            String value = Objects.isNull(template)
                ? getLangValue("could.not.find.parameter").formatted(parameterName)
                : getLangValue("could.not.find.parameter.template").formatted(parameterName, template);
            fail(value);

            return Optional.empty();
        }

        final Object param = params.get(parameterName);
        return Optional.ofNullable(extractProperties(param, parts, 1, template));
    }

    @SuppressWarnings("ReturnCount")
    private static String extractProperties(final Object object, final String[] parts, final int index, String template) {
        if (Objects.isNull(object)) {
            return "null";
        }
        if (index < parts.length) {
            if (object instanceof Object[]) {
                return Stream.of((Object[]) object)
                        .map(child -> extractProperties(child, parts, index, template))
                        .collect(JOINER);
            }
            if (object instanceof Iterable) {
                final Spliterator<?> iterator = ((Iterable) object).spliterator();
                return StreamSupport.stream(iterator, false)
                        .map(child -> extractProperties(child, parts, index, template))
                        .collect(JOINER);
            }
            final Object child = extractChild(object, parts[index], template);
            return extractProperties(child, parts, index + 1, template);
        }
        return ObjectUtils.toString(object);
    }

    private static Object extractChild(final Object object, final String part, String template) {
        final Class<?> type = object == null ? Object.class : object.getClass();
        try {
            return extractField(object, part, type);
        } catch (ReflectiveOperationException e) {
            String value = Objects.isNull(template)
                    ? getLangValue("unable.to.extract.value").formatted(part, type.getName(), part)
                    : getLangValue("unable.to.extract.value.template").formatted(part, template, type.getName(), part);
            fail(value);
        }

        return null;
    }

    @SuppressWarnings("PMD.EmptyCatchBlock")
    private static Object extractField(final Object object, final String part, final Class<?> type)
            throws ReflectiveOperationException {
        try {
            final Field field = type.getField(part);
            return fieldValue(object, field);
        } catch (NoSuchFieldException e) {
            Class<?> t = type;
            while (t != null) {
                try {
                    final Field declaredField = t.getDeclaredField(part);
                    return fieldValue(object, declaredField);
                } catch (NoSuchFieldException ignore) {
                    // Ignore
                }
                t = t.getSuperclass();
            }
            throw e;
        }
    }

    private static Object fieldValue(final Object object, final Field field) throws IllegalAccessException {
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            field.setAccessible(true);
            return field.get(object);
        }
    }
}
