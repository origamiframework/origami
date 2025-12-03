package ru.origami.common.cartesian_source;

import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.origami.common.environment.Language.getLangValue;

public class CartesianArgumentsProvider implements ArgumentsProvider {

    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        Method testMethod = context.getRequiredTestMethod();
        Class<?> testClass = testMethod.getDeclaringClass();

        CartesianSource source = testMethod.getAnnotation(CartesianSource.class);
        CartesianValue[] parameters = source.value();

        List<String> colNames = new ArrayList<>();
        List<String> colFormats = new ArrayList<>();
        List<List<Object[]>> axesTuples = new ArrayList<>();

        for (int i = 0; i < parameters.length; i++) {
            CartesianValue axis = parameters[i];
            AxisTuples tuples = resolveAxisTuples(axis, testClass, i);

            if (tuples.rows.isEmpty()) {
                String axisName = axis.name().isEmpty() ? ("arg%d".formatted(i)) : axis.name();

                throw new IllegalArgumentException(getLangValue("cartesian.axis.no.value").formatted(axisName));
            }

            axesTuples.add(tuples.rows);
            colNames.addAll(tuples.colNames);
            colFormats.addAll(tuples.colFormats);
        }

        List<Object[]> product = cartesianConcat(axesTuples);
        List<Predicate<Object[]>> excludePredicates = Arrays.stream(source.exclude())
                .map(expr -> buildExcludePredicate(expr, colNames))
                .collect(Collectors.toList());

        for (Class<? extends CartesianFilter> filterClass : source.filters()) {
            CartesianFilter filter = instantiate(filterClass);
            excludePredicates.add(filter::test);
        }

        return product.stream()
                .filter(args -> excludePredicates.stream().noneMatch(p -> p.test(args)))
                .map(args -> Arguments.of(wrapForDisplay(args, colNames, colFormats)));
    }

    private static List<Object> resolveCartesianValueValues(CartesianValue cv, Class<?> defaultProviderClass,
                                                            String name) {
        List<Object> values = new ArrayList<>();

        // 1) enum
        if (cv.enumSource() != CartesianValue.NoEnum.class) {
            values.addAll(Arrays.asList(cv.enumSource().getEnumConstants()));
        }

        // 2) примитивы и строки
        for (int v : cv.ints()) values.add(v);
        for (long v : cv.longs()) values.add(v);
        for (double v : cv.doubles()) values.add(v);
        for (float v : cv.floats()) values.add(v);
        for (short v : cv.shorts()) values.add(v);
        for (byte v : cv.bytes()) values.add(v);
        for (char v : cv.chars()) values.add(v);
        for (boolean v : cv.bools()) values.add(v);
        values.addAll(Arrays.asList(cv.strings()));

        // 3) CSV: каждая строка -> Object[] токенов (как кортеж)
        if (cv.csv().length > 0) {
            for (String line : cv.csv()) {
                List<String> tokens = parseCsvLine(
                        line == null ? "" : line,
                        cv.csvDelimiter(),
                        cv.csvQuote()
                );

                if (cv.csvTrim()) {
                    for (int i = 0; i < tokens.size(); i++) {
                        tokens.set(i, tokens.get(i) == null ? null : tokens.get(i).trim());
                    }
                }

                values.add(tokens.toArray(new Object[0])); // Object[] -> будет распознано как кортеж
            }
        }

        // 4) static field / method
        Class<?> providerClass = cv.providerClass() != CartesianValue.None.class
                ? cv.providerClass()
                : defaultProviderClass;

        if (!cv.field().isEmpty()) {
            values.addAll(toList(getStaticField(providerClass, cv.field())));
        }

        if (!cv.method().isEmpty()) {
            values.addAll(toList(invokeStaticNoArg(providerClass, cv.method())));
        }

        if (values.isEmpty()) {
            throw new IllegalArgumentException(getLangValue("cartesian.value.no.value").formatted(name));
        }

        return values;
    }

    private static Object[] wrapForDisplay(Object[] args, List<String> names, List<String> formats) {
        Object[] wrapped = new Object[args.length];

        for (int i = 0; i < args.length; i++) {
            String techName = names.get(i);
            String format = formats.get(i);
            String valueStr = String.valueOf(args[i]);
            String label;

            if (format != null && !format.isEmpty()) {
                label = format.replace("{name}", techName).replace("{value}", valueStr);
            } else {
                if (techName == null || techName.isEmpty() || techName.startsWith("arg")) {
                    label = valueStr;
                } else {
                    label = techName + "=" + valueStr;
                }
            }

            if (label.isBlank() && args[i].toString().isBlank()) {
                wrapped[i] = "";
            } else {
                wrapped[i] = Named.of(label, args[i]);
            }
        }

        return wrapped;
    }

    private static List<Object[]> cartesianConcat(List<List<Object[]>> axes) {
        List<Object[]> out = new ArrayList<>();

        if (axes.isEmpty()) {
            return out;
        }

        backtrackTuples(axes, 0, new Object[0], out);

        return out;
    }

    private static void backtrackTuples(List<List<Object[]>> axes, int depth, Object[] acc, List<Object[]> out) {
        if (depth == axes.size()) {
            out.add(acc);

            return;
        }

        for (Object[] tuple : axes.get(depth)) {
            Object[] next = concat(acc, tuple);
            backtrackTuples(axes, depth + 1, next, out);
        }
    }

    private static Object[] concat(Object[] a, Object[] b) {
        Object[] res = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, res, a.length, b.length);

        return res;
    }

    private static Predicate<Object[]> buildExcludePredicate(String expr, List<String> cartesianValueNames) {
        // Пример: "number=2, letter=B, flag=false"
        // Комбинация исключается, если ВСЕ пары в выражении совпали
        if (expr == null || expr.trim().isEmpty()) {
            return args -> false;
        }

        String[] tokens = Arrays.stream(expr.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toArray(String[]::new);

        List<BiPredicate<Object[], List<String>>> checks = new ArrayList<>();

        for (String token : tokens) {
            int eq = token.indexOf('=');

            if (eq <= 0) {
                throw new IllegalArgumentException(getLangValue("cartesian.bad.exclude.value").formatted(token));
            }

            String key = token.substring(0, eq).trim();
            String value = token.substring(eq + 1).trim();

            checks.add((args, names) -> {
                int idx = resolveCartesianValueIndex(key, names);

                if (idx < 0 || idx >= args.length) {
                    throw new IllegalArgumentException(getLangValue("cartesian.unknown.exclude.cartesian.value").formatted(key));
                }

                Object actual = args[idx];

                return Objects.toString(actual).equals(value);
            });
        }

        return args -> checks.stream().allMatch(ch -> ch.test(args, cartesianValueNames));
    }

    private static int resolveCartesianValueIndex(String key, List<String> names) {
        // key может быть именем оси или индексом, например "0"
        int byIndex = tryParseInt(key, -1);

        if (byIndex >= 0) {
            return byIndex;
        }

        return names.indexOf(key);
    }

    private static int tryParseInt(String s, int def) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return def;
        }
    }

    private static Object getStaticField(Class<?> cls, String fieldName) {
        try {
            Field f = findField(cls, fieldName);

            if (!Modifier.isStatic(f.getModifiers())) {
                throw new IllegalArgumentException(getLangValue("cartesian.not.static.field").formatted(fieldName));
            }

            f.setAccessible(true);

            return f.get(null);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(getLangValue("cartesian.cannot.read.field").formatted(fieldName, cls.getName()), e);
        }
    }

    private static Field findField(Class<?> cls, String name) throws NoSuchFieldException {
        Class<?> c = cls;

        while (c != null) {
            try {
                return c.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                c = c.getSuperclass();
            }
        }

        throw new NoSuchFieldException(name);
    }

    private static Object invokeStaticNoArg(Class<?> cls, String methodName) {
        try {
            Method m = findStaticNoArgMethod(cls, methodName);
            m.setAccessible(true);

            return m.invoke(null);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e.getMessage(), e);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(getLangValue("cartesian.cannot.invoke.method").formatted(methodName, cls.getName()), e);
        }
    }

    private static Method findStaticNoArgMethod(Class<?> cls, String name) throws NoSuchMethodException {
        Method anyNamed = null;

        for (Class<?> c = cls; c != null; c = c.getSuperclass()) {
            for (Method m : c.getDeclaredMethods()) {
                if (!m.getName().equals(name)) {
                    continue;
                }

                if (anyNamed == null) {
                    anyNamed = m;
                }

                if (m.getParameterCount() == 0 && Modifier.isStatic(m.getModifiers())) {
                    return m;
                }
            }
        }

        if (anyNamed != null) {
            throw new NoSuchMethodException(getLangValue("cartesian.found.unsuitable.methods").formatted(name, cls.getName()));
        }

        throw new NoSuchMethodException(getLangValue("cartesian.no.found.methods").formatted(name, cls.getName()));
    }

    private static List<Object> toList(Object src) {
        if (src == null) {
            return Collections.emptyList();
        }

        if (src instanceof Stream) {
            return ((Stream<?>) src).collect(Collectors.toList());
        }

        if (src instanceof Iterable) {
            List<Object> out = new ArrayList<>();

            for (Object o : (Iterable<?>) src) {
                out.add(o);
            }

            return out;
        }

        Class<?> cls = src.getClass();

        if (cls.isArray()) {
            int len = Array.getLength(src);
            List<Object> out = new ArrayList<>(len);

            for (int i = 0; i < len; i++) {
                out.add(Array.get(src, i));
            }

            return out;
        }

        throw new IllegalArgumentException(getLangValue("cartesian.unsupported.array").formatted(cls.getName()));
    }

    private static <T> T instantiate(Class<T> type) {
        try {
            Constructor<T> ctor = type.getDeclaredConstructor();
            ctor.setAccessible(true);

            return ctor.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(getLangValue("cartesian.type.must.have.no.args.ctor").formatted(type.getName()), e);
        }
    }

    private interface BiPredicate<A, B> {
        boolean test(A a, B b);
    }

    private static final class AxisTuples {
        final List<Object[]> rows;
        final List<String> colNames;
        final List<String> colFormats;

        AxisTuples(List<Object[]> rows, List<String> names, List<String> formats) {
            this.rows = rows;
            this.colNames = names;
            this.colFormats = formats;
        }
    }

    private static AxisTuples resolveAxisTuples(CartesianValue axis, Class<?> defaultProviderClass, int axisIndex) {
        String axisName = axis.name().isEmpty() ? ("arg%d".formatted(axisIndex)) : axis.name();
        List<Object> flatValues = resolveCartesianValueValues(axis, defaultProviderClass, axisName);

        List<Object[]> rows = new ArrayList<>(flatValues.size());
        int arity = -1;

        for (Object v : flatValues) {
            Object[] tuple;

            if (v instanceof Arguments a) {
                tuple = a.get();
            } else if (v != null && v.getClass().isArray() && !v.getClass().getComponentType().isPrimitive()) {
                tuple = (Object[]) v;
            } else {
                tuple = new Object[]{v};
            }

            if (arity < 0) {
                arity = tuple.length;
            } else if (tuple.length != arity) {
                throw new IllegalArgumentException(getLangValue("cartesian.axis.diff.sizes").formatted(
                        axisName, arity, tuple.length));
            }

            rows.add(tuple);
        }

        if (rows.isEmpty()) {
            return new AxisTuples(List.of(), List.of(), List.of());
        }

        List<String> names = new ArrayList<>(arity);
        String[] cols = axis.columns();

        if (cols.length > 0) {
            if (cols.length != arity) {
                throw new IllegalArgumentException(getLangValue("cartesian.axis.column.length").formatted(
                        axisName, cols.length, arity));
            }

            names.addAll(Arrays.asList(cols));
        } else {
            if (arity == 1) {
                names.add(axisName);
            } else {
                for (int i = 0; i < arity; i++) {
                    names.add("%s[%d]".formatted(axisName, i));
                }
            }
        }

        List<String> formats = new ArrayList<>(arity);
        String fmt = axis.displayFormat();

        for (int i = 0; i < arity; i++) {
            formats.add(fmt);
        }

        return new AxisTuples(rows, names, formats);
    }

    private static List<String> parseCsvLine(String line, char delimiter, char quote) {
        List<String> out = new ArrayList<>();

        if (line == null) {
            out.add("");

            return out;
        }

        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);

            if (inQuotes) {
                if (ch == quote) {
                    // двойная кавычка внутри quoted поля -> добавляем одну кавычку
                    if (i + 1 < line.length() && line.charAt(i + 1) == quote) {
                        cur.append(quote);
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    cur.append(ch);
                }
            } else {
                if (ch == delimiter) {
                    out.add(cur.toString());
                    cur.setLength(0);
                } else if (ch == quote) {
                    inQuotes = true;
                } else {
                    cur.append(ch);
                }
            }
        }

        out.add(cur.toString());

        return out;
    }
}
