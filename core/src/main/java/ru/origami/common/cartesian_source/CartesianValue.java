package ru.origami.common.cartesian_source;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CartesianValue {

    String name() default "";

    Class<? extends Enum<?>> enumSource() default NoEnum.class;

    int[] ints() default {};
    long[] longs() default {};
    double[] doubles() default {};
    float[] floats() default {};
    short[] shorts() default {};
    byte[] bytes() default {};
    char[] chars() default {};
    boolean[] bools() default {};
    String[] strings() default {};

    String[] csv() default {};
    char csvDelimiter() default ',';
    char csvQuote() default '"';
    boolean csvTrim() default true;

    Class<?> providerClass() default None.class;
    String field() default "";
    String method() default "";

    // "{name}={value}" если name задан, иначе просто "{value}"
    String displayFormat() default "";

    String[] columns() default {};

    public enum NoEnum {
        __
    }

    public static final class None {
        private None() {}
    }
}

