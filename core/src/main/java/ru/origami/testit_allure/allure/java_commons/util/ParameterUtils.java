package ru.origami.testit_allure.allure.java_commons.util;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import ru.origami.testit_allure.allure.java_commons.Param;
import ru.origami.testit_allure.allure.model.Parameter;

public final class ParameterUtils {

    private ParameterUtils() {
        throw new IllegalStateException("do not instance");
    }

    public static List<Parameter> createParameters(final Method method,
                                                   final Object... args) {
        final java.lang.reflect.Parameter[] parameters = method.getParameters();
        return IntStream.range(0, parameters.length)
                .mapToObj(i -> {
                    final java.lang.reflect.Parameter parameter = parameters[i];
                    final Object value = args[i];
                    final Param annotation = parameter.getAnnotation(Param.class);
                    if (Objects.isNull(annotation)) {
                        return ResultsUtils.createParameter(parameter.getName(), value);
                    }
                    final String name = Stream.of(annotation.value(), annotation.name(), parameter.getName())
                            .map(String::trim)
                            .filter(s -> s.length() > 0)
                            .findFirst()
                            .orElseGet(() -> "arg" + i);

                    return ResultsUtils.createParameter(
                            name,
                            value,
                            annotation.excluded(),
                            annotation.mode()
                    );
                })
                .collect(Collectors.toList());
    }

}
