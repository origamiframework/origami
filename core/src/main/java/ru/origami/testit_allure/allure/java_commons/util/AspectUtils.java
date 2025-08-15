package ru.origami.testit_allure.allure.java_commons.util;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import ru.origami.testit_allure.allure.java_commons.Param;
import ru.origami.testit_allure.allure.model.Parameter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static ru.origami.common.environment.Language.getLangValue;
import static ru.origami.testit_allure.allure.java_commons.util.NamingUtils.processNameTemplate;
import static ru.origami.testit_allure.allure.java_commons.util.ResultsUtils.createParameter;

public final class AspectUtils {

    public static String LANG_STEP = "getLangValue:";

    private AspectUtils() {
        throw new IllegalStateException("Do not instance");
    }

    public static String getStepName(String nameTemplate, final JoinPoint joinPoint) {
        StringBuilder name = new StringBuilder(nameTemplate);

        if (name.indexOf(LANG_STEP) != -1) {
            String[] splitName = name.substring(LANG_STEP.length()).split(",");
            name.setLength(0);
            name.append(getLangValue(splitName[0]));

            if (splitName.length > 1) {
                IntStream.range(1, splitName.length).forEach(i -> name.append(splitName[i]));
            }
        }

        return Optional.of(name.toString())
                .filter(v -> !v.isEmpty())
                .map(value -> processNameTemplate(value, getParametersMap(joinPoint)))
                .orElseGet(joinPoint.getSignature()::getName);
    }

    /**
     * @deprecated use {@link AspectUtils#getParametersMap(JoinPoint)} instead.
     */
    @Deprecated
    public static Map<String, Object> getParametersMap(final MethodSignature signature, final Object... args) {
        final String[] parameterNames = signature.getParameterNames();
        final Map<String, Object> params = new HashMap<>();
        params.put("method", signature.getName());
        for (int i = 0; i < Math.max(parameterNames.length, args.length); i++) {
            params.put(parameterNames[i], args[i]);
            params.put(Integer.toString(i), args[i]);
        }
        return params;
    }

    public static Map<String, Object> getParametersMap(final JoinPoint joinPoint) {
        final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        final Map<String, Object> params = getParametersMap(methodSignature, joinPoint.getArgs());
        Optional.ofNullable(joinPoint.getThis()).ifPresent(objThis -> params.put("this", objThis));
        return params;
    }

    public static List<Parameter> getParameters(final MethodSignature signature, final Object... args) {
        final java.lang.reflect.Parameter[] params = signature.getMethod().getParameters();
        return IntStream
                .range(0, args.length)
                .mapToObj(index -> {
                    final Parameter parameter = createParameter(signature.getParameterNames()[index], args[index]);
                    final java.lang.reflect.Parameter ref = params[index];
                    Stream.of(ref.getAnnotationsByType(Param.class))
                            .findFirst()
                            .ifPresent(param -> {
                                Stream.of(param.value(), param.name())
                                        .map(String::trim)
                                        .filter(name -> name.length() > 0)
                                        .findFirst()
                                        .ifPresent(parameter::setName);

                                parameter.setMode(param.mode());
                                parameter.setExcluded(param.excluded());
                            });
                    return parameter;
                })
                .collect(Collectors.toList());
    }

    /**
     * @deprecated use {@link ObjectUtils#toString(Object)} instead.
     */
    @Deprecated
    public static String objectToString(final Object object) {
        return ObjectUtils.toString(object);
    }
}
