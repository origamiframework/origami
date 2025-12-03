package ru.origami.common.cartesian_source;

import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ArgumentsSource(CartesianArgumentsProvider.class)
public @interface CartesianSource {

    CartesianValue[] value();

    // Примитивный exclude: список правил. Каждое правило — это И-условие с запятыми:
    // "number=2, letter=B" (OR между строками массива).
    // Можно ссылаться по имени оси (CartesianValue.name) или по индексу: "0=2"
    String[] exclude() default {};

    // Сложные фильтры — класс(ы), реализующие CartesianFilter
    Class<? extends CartesianFilter>[] filters() default {};
}

