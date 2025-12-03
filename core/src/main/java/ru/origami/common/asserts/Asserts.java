package ru.origami.common.asserts;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.assertj.core.api.ListAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.api.function.ThrowingSupplier;
import org.opentest4j.MultipleFailuresError;
import ru.origami.testit_allure.annotations.Step;

import java.time.*;
import java.util.Collection;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Asserts {

    //todo add assert that matches

    @Step("getLangValue:asserts.step.assert.true")
    public static void assertTrue(String field, boolean condition) {
        Assertions.assertTrue(condition, field);
    }

    @Step("getLangValue:asserts.step.assert.true")
    public static void assertTrue(String field, boolean condition, Supplier<String> messageSupplier) {
        Assertions.assertTrue(condition, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.true")
    public static void assertTrue(String field, BooleanSupplier booleanSupplier) {
        Assertions.assertTrue(booleanSupplier, field);
    }

    @Step("getLangValue:asserts.step.assert.true")
    public static void assertTrue(String field, BooleanSupplier booleanSupplier, String message) {
        Assertions.assertTrue(booleanSupplier, message);
    }

    @Step("getLangValue:asserts.step.assert.true")
    public static void assertTrue(String field, boolean condition, String message) {
        Assertions.assertTrue(condition, message);
    }

    @Step("getLangValue:asserts.step.assert.true")
    public static void assertTrue(String field, BooleanSupplier booleanSupplier, Supplier<String> messageSupplier) {
        Assertions.assertTrue(booleanSupplier, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.false")
    public static void assertFalse(String field, boolean condition) {
        Assertions.assertFalse(condition, field);
    }

    @Step("getLangValue:asserts.step.assert.false")
    public static void assertFalse(String field, boolean condition, String message) {
        Assertions.assertFalse(condition, message);
    }

    @Step("getLangValue:asserts.step.assert.false")
    public static void assertFalse(String field, boolean condition, Supplier<String> messageSupplier) {
        Assertions.assertFalse(condition, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.false")
    public static void assertFalse(String field, BooleanSupplier booleanSupplier) {
        Assertions.assertFalse(booleanSupplier, field);
    }

    @Step("getLangValue:asserts.step.assert.false")
    public static void assertFalse(String field, BooleanSupplier booleanSupplier, String message) {
        Assertions.assertFalse(booleanSupplier, message);
    }

    @Step("getLangValue:asserts.step.assert.false")
    public static void assertFalse(String field, BooleanSupplier booleanSupplier, Supplier<String> messageSupplier) {
        Assertions.assertFalse(booleanSupplier, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.null")
    public static void assertNull(String field, Object actual) {
        Assertions.assertNull(actual, field);
    }

    @Step("getLangValue:asserts.step.assert.null")
    public static void assertNull(String field, Object actual, String message) {
        Assertions.assertNull(actual, message);
    }

    @Step("getLangValue:asserts.step.assert.null")
    public static void assertNull(String field, Object actual, Supplier<String> messageSupplier) {
        Assertions.assertNull(actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.not.null")
    public static void assertNotNull(String field, Object actual) {
        Assertions.assertNotNull(actual, field);
    }

    @Step("getLangValue:asserts.step.assert.not.null")
    public static void assertNotNull(String field, Object actual, String message) {
        Assertions.assertNotNull(actual, message);
    }

    @Step("getLangValue:asserts.step.assert.not.null")
    public static void assertNotNull(String field, Object actual, Supplier<String> messageSupplier) {
        Assertions.assertNotNull(actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, short expected, short actual) {
        Assertions.assertEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, short expected, Short actual) {
        Assertions.assertEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Short expected, short actual) {
        Assertions.assertEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Short expected, Short actual) {
        Assertions.assertEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, short expected, short actual, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, short expected, Short actual, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Short expected, short actual, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Short expected, Short actual, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, short expected, short actual, Supplier<String> messageSupplier) {
        Assertions.assertEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, short expected, Short actual, Supplier<String> messageSupplier) {
        Assertions.assertEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Short expected, short actual, Supplier<String> messageSupplier) {
        Assertions.assertEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Short expected, Short actual, Supplier<String> messageSupplier) {
        Assertions.assertEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, byte expected, byte actual) {
        Assertions.assertEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, byte expected, Byte actual) {
        Assertions.assertEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Byte expected, byte actual) {
        Assertions.assertEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Byte expected, Byte actual) {
        Assertions.assertEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, byte expected, byte actual, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, byte expected, Byte actual, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Byte expected, byte actual, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Byte expected, Byte actual, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, byte expected, byte actual, Supplier<String> messageSupplier) {
        Assertions.assertEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, byte expected, Byte actual, Supplier<String> messageSupplier) {
        Assertions.assertEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Byte expected, byte actual, Supplier<String> messageSupplier) {
        Assertions.assertEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Byte expected, Byte actual, Supplier<String> messageSupplier) {
        Assertions.assertEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, int expected, int actual) {
        Assertions.assertEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, int expected, Integer actual) {
        Assertions.assertEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Integer expected, int actual) {
        Assertions.assertEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Integer expected, Integer actual) {
        Assertions.assertEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, int expected, int actual, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, int expected, Integer actual, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Integer expected, int actual, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Integer expected, Integer actual, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, int expected, int actual, Supplier<String> messageSupplier) {
        Assertions.assertEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, int expected, Integer actual, Supplier<String> messageSupplier) {
        Assertions.assertEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Integer expected, int actual, Supplier<String> messageSupplier) {
        Assertions.assertEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Integer expected, Integer actual, Supplier<String> messageSupplier) {
        Assertions.assertEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, long expected, long actual) {
        Assertions.assertEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, long expected, Long actual) {
        Assertions.assertEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Long expected, long actual) {
        Assertions.assertEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Long expected, Long actual) {
        Assertions.assertEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, long expected, long actual, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, long expected, Long actual, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Long expected, long actual, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Long expected, Long actual, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, long expected, long actual, Supplier<String> messageSupplier) {
        Assertions.assertEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, long expected, Long actual, Supplier<String> messageSupplier) {
        Assertions.assertEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Long expected, long actual, Supplier<String> messageSupplier) {
        Assertions.assertEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Long expected, Long actual, Supplier<String> messageSupplier) {
        Assertions.assertEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, float expected, float actual) {
        Assertions.assertEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, float expected, Float actual) {
        Assertions.assertEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Float expected, float actual) {
        Assertions.assertEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Float expected, Float actual) {
        Assertions.assertEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, float expected, float actual, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, float expected, Float actual, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Float expected, float actual, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Float expected, Float actual, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, float expected, float actual, Supplier<String> messageSupplier) {
        Assertions.assertEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, float expected, Float actual, Supplier<String> messageSupplier) {
        Assertions.assertEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Float expected, float actual, Supplier<String> messageSupplier) {
        Assertions.assertEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Float expected, Float actual, Supplier<String> messageSupplier) {
        Assertions.assertEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals.with.delta")
    public static void assertEquals(String field, float expected, float actual, float delta) {
        Assertions.assertEquals(expected, actual, delta, field);
    }

    @Step("getLangValue:asserts.step.assert.equals.with.delta")
    public static void assertEquals(String field, float expected, float actual, float delta, String message) {
        Assertions.assertEquals(expected, actual, delta, message);
    }

    @Step("getLangValue:asserts.step.assert.equals.with.delta")
    public static void assertEquals(String field, float expected, float actual, float delta, Supplier<String> messageSupplier) {
        Assertions.assertEquals(expected, actual, delta, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, double expected, double actual) {
        Assertions.assertEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, double expected, Double actual) {
        Assertions.assertEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Double expected, double actual) {
        Assertions.assertEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Double expected, Double actual) {
        Assertions.assertEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, double expected, double actual, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, double expected, Double actual, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Double expected, double actual, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Double expected, Double actual, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, double expected, double actual, Supplier<String> messageSupplier) {
        Assertions.assertEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, double expected, Double actual, Supplier<String> messageSupplier) {
        Assertions.assertEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Double expected, double actual, Supplier<String> messageSupplier) {
        Assertions.assertEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Double expected, Double actual, Supplier<String> messageSupplier) {
        Assertions.assertEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals.with.delta")
    public static void assertEquals(String field, double expected, double actual, double delta) {
        Assertions.assertEquals(expected, actual, delta, field);
    }

    @Step("getLangValue:asserts.step.assert.equals.with.delta")
    public static void assertEquals(String field, double expected, double actual, double delta, String message) {
        Assertions.assertEquals(expected, actual, delta, message);
    }

    @Step("getLangValue:asserts.step.assert.equals.with.delta")
    public static void assertEquals(String field, double expected, double actual, double delta, Supplier<String> messageSupplier) {
        Assertions.assertEquals(expected, actual, delta, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, char expected, char actual) {
        Assertions.assertEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, char expected, Character actual) {
        Assertions.assertEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Character expected, char actual) {
        Assertions.assertEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Character expected, Character actual) {
        Assertions.assertEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, char expected, char actual, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, char expected, Character actual, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Character expected, char actual, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Character expected, Character actual, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, char expected, char actual, Supplier<String> messageSupplier) {
        Assertions.assertEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, char expected, Character actual, Supplier<String> messageSupplier) {
        Assertions.assertEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Character expected, char actual, Supplier<String> messageSupplier) {
        Assertions.assertEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Character expected, Character actual, Supplier<String> messageSupplier) {
        Assertions.assertEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Object expected, Object actual) {
        Assertions.assertEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Object expected, Object actual, String message) {
        Assertions.assertEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertEquals(String field, Object expected, Object actual, Supplier<String> messageSupplier) {
        Assertions.assertEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertArrayEquals(String field, boolean[] expected, boolean[] actual) {
        Assertions.assertArrayEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertArrayEquals(String field, boolean[] expected, boolean[] actual, String message) {
        Assertions.assertArrayEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertArrayEquals(String field, boolean[] expected, boolean[] actual, Supplier<String> messageSupplier) {
        Assertions.assertArrayEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertArrayEquals(String field, char[] expected, char[] actual) {
        Assertions.assertArrayEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertArrayEquals(String field, char[] expected, char[] actual, String message) {
        Assertions.assertArrayEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertArrayEquals(String field, char[] expected, char[] actual, Supplier<String> messageSupplier) {
        Assertions.assertArrayEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertArrayEquals(String field, byte[] expected, byte[] actual) {
        Assertions.assertArrayEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertArrayEquals(String field, byte[] expected, byte[] actual, String message) {
        Assertions.assertArrayEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertArrayEquals(String field, byte[] expected, byte[] actual, Supplier<String> messageSupplier) {
        Assertions.assertArrayEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertArrayEquals(String field, short[] expected, short[] actual) {
        Assertions.assertArrayEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertArrayEquals(String field, short[] expected, short[] actual, String message) {
        Assertions.assertArrayEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertArrayEquals(String field, short[] expected, short[] actual, Supplier<String> messageSupplier) {
        Assertions.assertArrayEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertArrayEquals(String field, int[] expected, int[] actual) {
        Assertions.assertArrayEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertArrayEquals(String field, int[] expected, int[] actual, String message) {
        Assertions.assertArrayEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertArrayEquals(String field, int[] expected, int[] actual, Supplier<String> messageSupplier) {
        Assertions.assertArrayEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertArrayEquals(String field, long[] expected, long[] actual) {
        Assertions.assertArrayEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertArrayEquals(String field, long[] expected, long[] actual, String message) {
        Assertions.assertArrayEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertArrayEquals(String field, long[] expected, long[] actual, Supplier<String> messageSupplier) {
        Assertions.assertArrayEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertArrayEquals(String field, float[] expected, float[] actual) {
        Assertions.assertArrayEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertArrayEquals(String field, float[] expected, float[] actual, String message) {
        Assertions.assertArrayEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertArrayEquals(String field, float[] expected, float[] actual, Supplier<String> messageSupplier) {
        Assertions.assertArrayEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals.with.delta")
    public static void assertArrayEquals(String field, float[] expected, float[] actual, float delta) {
        Assertions.assertArrayEquals(expected, actual, delta, field);
    }

    @Step("getLangValue:asserts.step.assert.equals.with.delta")
    public static void assertArrayEquals(String field, float[] expected, float[] actual, float delta, String message) {
        Assertions.assertArrayEquals(expected, actual, delta, message);
    }

    @Step("getLangValue:asserts.step.assert.equals.with.delta")
    public static void assertArrayEquals(String field, float[] expected, float[] actual, float delta, Supplier<String> messageSupplier) {
        Assertions.assertArrayEquals(expected, actual, delta, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertArrayEquals(String field, double[] expected, double[] actual) {
        Assertions.assertArrayEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertArrayEquals(String field, double[] expected, double[] actual, String message) {
        Assertions.assertArrayEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertArrayEquals(String field, double[] expected, double[] actual, Supplier<String> messageSupplier) {
        Assertions.assertArrayEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals.with.delta")
    public static void assertArrayEquals(String field, double[] expected, double[] actual, double delta) {
        Assertions.assertArrayEquals(expected, actual, delta, field);
    }

    @Step("getLangValue:asserts.step.assert.equals.with.delta")
    public static void assertArrayEquals(String field, double[] expected, double[] actual, double delta, String message) {
        Assertions.assertArrayEquals(expected, actual, delta, message);
    }

    @Step("getLangValue:asserts.step.assert.equals.with.delta")
    public static void assertArrayEquals(String field, double[] expected, double[] actual, double delta, Supplier<String> messageSupplier) {
        Assertions.assertArrayEquals(expected, actual, delta, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertArrayEquals(String field, Object[] expected, Object[] actual) {
        Assertions.assertArrayEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertArrayEquals(String field, Object[] expected, Object[] actual, String message) {
        Assertions.assertArrayEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertArrayEquals(String field, Object[] expected, Object[] actual, Supplier<String> messageSupplier) {
        Assertions.assertArrayEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertIterableEquals(String field, Iterable<?> expected, Iterable<?> actual) {
        Assertions.assertIterableEquals(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertIterableEquals(String field, Iterable<?> expected, Iterable<?> actual, String message) {
        Assertions.assertIterableEquals(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertIterableEquals(String field, Iterable<?> expected, Iterable<?> actual, Supplier<String> messageSupplier) {
        Assertions.assertIterableEquals(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.lines.match")
    public static void assertLinesMatch(String field, List<String> expectedLines, List<String> actualLines) {
        Assertions.assertLinesMatch(expectedLines, actualLines, field);
    }

    @Step("getLangValue:asserts.step.assert.lines.match")
    public static void assertLinesMatch(String field, List<String> expectedLines, List<String> actualLines, String message) {
        Assertions.assertLinesMatch(expectedLines, actualLines, message);
    }

    @Step("getLangValue:asserts.step.assert.lines.match")
    public static void assertLinesMatch(String field, List<String> expectedLines, List<String> actualLines, Supplier<String> messageSupplier) {
        Assertions.assertLinesMatch(expectedLines, actualLines, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.lines.match")
    public static void assertLinesMatch(String field, Stream<String> expectedLines, Stream<String> actualLines) {
        Assertions.assertLinesMatch(expectedLines, actualLines, field);
    }

    @Step("getLangValue:asserts.step.assert.lines.match")
    public static void assertLinesMatch(String field, Stream<String> expectedLines, Stream<String> actualLines, String message) {
        Assertions.assertLinesMatch(expectedLines, actualLines, message);
    }

    @Step("getLangValue:asserts.step.assert.lines.match")
    public static void assertLinesMatch(String field, Stream<String> expectedLines, Stream<String> actualLines, Supplier<String> messageSupplier) {
        Assertions.assertLinesMatch(expectedLines, actualLines, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, byte unexpected, byte actual) {
        Assertions.assertNotEquals(unexpected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, byte unexpected, Byte actual) {
        Assertions.assertNotEquals(unexpected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Byte unexpected, byte actual) {
        Assertions.assertNotEquals(unexpected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Byte unexpected, Byte actual) {
        Assertions.assertNotEquals(unexpected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, byte unexpected, byte actual, String message) {
        Assertions.assertNotEquals(unexpected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, byte unexpected, Byte actual, String message) {
        Assertions.assertNotEquals(unexpected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Byte unexpected, byte actual, String message) {
        Assertions.assertNotEquals(unexpected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Byte unexpected, Byte actual, String message) {
        Assertions.assertNotEquals(unexpected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, byte unexpected, byte actual, Supplier<String> messageSupplier) {
        Assertions.assertNotEquals(unexpected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, byte unexpected, Byte actual, Supplier<String> messageSupplier) {
        Assertions.assertNotEquals(unexpected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Byte unexpected, byte actual, Supplier<String> messageSupplier) {
        Assertions.assertNotEquals(unexpected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Byte unexpected, Byte actual, Supplier<String> messageSupplier) {
        Assertions.assertNotEquals(unexpected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, short unexpected, short actual) {
        Assertions.assertNotEquals(unexpected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, short unexpected, Short actual) {
        Assertions.assertNotEquals(unexpected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Short unexpected, short actual) {
        Assertions.assertNotEquals(unexpected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Short unexpected, Short actual) {
        Assertions.assertNotEquals(unexpected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, short unexpected, short actual, String message) {
        Assertions.assertNotEquals(unexpected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, short unexpected, Short actual, String message) {
        Assertions.assertNotEquals(unexpected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Short unexpected, short actual, String message) {
        Assertions.assertNotEquals(unexpected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Short unexpected, Short actual, String message) {
        Assertions.assertNotEquals(unexpected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, short unexpected, short actual, Supplier<String> messageSupplier) {
        Assertions.assertNotEquals(unexpected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, short unexpected, Short actual, Supplier<String> messageSupplier) {
        Assertions.assertNotEquals(unexpected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Short unexpected, short actual, Supplier<String> messageSupplier) {
        Assertions.assertNotEquals(unexpected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Short unexpected, Short actual, Supplier<String> messageSupplier) {
        Assertions.assertNotEquals(unexpected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, int unexpected, int actual) {
        Assertions.assertNotEquals(unexpected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, int unexpected, Integer actual) {
        Assertions.assertNotEquals(unexpected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Integer unexpected, int actual) {
        Assertions.assertNotEquals(unexpected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Integer unexpected, Integer actual) {
        Assertions.assertNotEquals(unexpected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, int unexpected, int actual, String message) {
        Assertions.assertNotEquals(unexpected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, int unexpected, Integer actual, String message) {
        Assertions.assertNotEquals(unexpected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Integer unexpected, int actual, String message) {
        Assertions.assertNotEquals(unexpected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Integer unexpected, Integer actual, String message) {
        Assertions.assertNotEquals(unexpected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, int unexpected, int actual, Supplier<String> messageSupplier) {
        Assertions.assertNotEquals(unexpected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, int unexpected, Integer actual, Supplier<String> messageSupplier) {
        Assertions.assertNotEquals(unexpected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Integer unexpected, int actual, Supplier<String> messageSupplier) {
        Assertions.assertNotEquals(unexpected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Integer unexpected, Integer actual, Supplier<String> messageSupplier) {
        Assertions.assertNotEquals(unexpected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, long unexpected, long actual) {
        Assertions.assertNotEquals(unexpected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, long unexpected, Long actual) {
        Assertions.assertNotEquals(unexpected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Long unexpected, long actual) {
        Assertions.assertNotEquals(unexpected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Long unexpected, Long actual) {
        Assertions.assertNotEquals(unexpected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, long unexpected, long actual, String message) {
        Assertions.assertNotEquals(unexpected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, long unexpected, Long actual, String message) {
        Assertions.assertNotEquals(unexpected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Long unexpected, long actual, String message) {
        Assertions.assertNotEquals(unexpected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Long unexpected, Long actual, String message) {
        Assertions.assertNotEquals(unexpected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, long unexpected, long actual, Supplier<String> messageSupplier) {
        Assertions.assertNotEquals(unexpected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, long unexpected, Long actual, Supplier<String> messageSupplier) {
        Assertions.assertNotEquals(unexpected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Long unexpected, long actual, Supplier<String> messageSupplier) {
        Assertions.assertNotEquals(unexpected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Long unexpected, Long actual, Supplier<String> messageSupplier) {
        Assertions.assertNotEquals(unexpected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, float unexpected, float actual) {
        Assertions.assertNotEquals(unexpected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, float unexpected, Float actual) {
        Assertions.assertNotEquals(unexpected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Float unexpected, float actual) {
        Assertions.assertNotEquals(unexpected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Float unexpected, Float actual) {
        Assertions.assertNotEquals(unexpected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, float unexpected, float actual, String message) {
        Assertions.assertNotEquals(unexpected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, float unexpected, Float actual, String message) {
        Assertions.assertNotEquals(unexpected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Float unexpected, float actual, String message) {
        Assertions.assertNotEquals(unexpected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Float unexpected, Float actual, String message) {
        Assertions.assertNotEquals(unexpected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, float unexpected, float actual, Supplier<String> messageSupplier) {
        Assertions.assertNotEquals(unexpected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, float unexpected, Float actual, Supplier<String> messageSupplier) {
        Assertions.assertNotEquals(unexpected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Float unexpected, float actual, Supplier<String> messageSupplier) {
        Assertions.assertNotEquals(unexpected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Float unexpected, Float actual, Supplier<String> messageSupplier) {
        Assertions.assertNotEquals(unexpected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.not.equals.with.delta")
    public static void assertNotEquals(String field, float unexpected, float actual, float delta) {
        Assertions.assertNotEquals(unexpected, actual, delta, field);
    }

    @Step("getLangValue:asserts.step.assert.not.equals.with.delta")
    public static void assertNotEquals(String field, float unexpected, float actual, float delta, String message) {
        Assertions.assertNotEquals(unexpected, actual, delta, message);
    }

    @Step("getLangValue:asserts.step.assert.not.equals.with.delta")
    public static void assertNotEquals(String field, float unexpected, float actual, float delta, Supplier<String> messageSupplier) {
        Assertions.assertNotEquals(unexpected, actual, delta, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, double unexpected, double actual) {
        Assertions.assertNotEquals(unexpected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, double unexpected, Double actual) {
        Assertions.assertNotEquals(unexpected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Double unexpected, double actual) {
        Assertions.assertNotEquals(unexpected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Double unexpected, Double actual) {
        Assertions.assertNotEquals(unexpected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, double unexpected, double actual, String message) {
        Assertions.assertNotEquals(unexpected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, double unexpected, Double actual, String message) {
        Assertions.assertNotEquals(unexpected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Double unexpected, double actual, String message) {
        Assertions.assertNotEquals(unexpected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Double unexpected, Double actual, String message) {
        Assertions.assertNotEquals(unexpected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, double unexpected, double actual, Supplier<String> messageSupplier) {
        Assertions.assertNotEquals(unexpected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, double unexpected, Double actual, Supplier<String> messageSupplier) {
        Assertions.assertNotEquals(unexpected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Double unexpected, double actual, Supplier<String> messageSupplier) {
        Assertions.assertNotEquals(unexpected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Double unexpected, Double actual, Supplier<String> messageSupplier) {
        Assertions.assertNotEquals(unexpected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.not.equals.with.delta")
    public static void assertNotEquals(String field, double unexpected, double actual, double delta) {
        Assertions.assertNotEquals(unexpected, actual, delta, field);
    }

    @Step("getLangValue:asserts.step.assert.not.equals.with.delta")
    public static void assertNotEquals(String field, double unexpected, double actual, double delta, String message) {
        Assertions.assertNotEquals(unexpected, actual, delta, message);
    }

    @Step("getLangValue:asserts.step.assert.not.equals.with.delta")
    public static void assertNotEquals(String field, double unexpected, double actual, double delta, Supplier<String> messageSupplier) {
        Assertions.assertNotEquals(unexpected, actual, delta, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, char unexpected, char actual) {
        Assertions.assertNotEquals(unexpected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, char unexpected, Character actual) {
        Assertions.assertNotEquals(unexpected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Character unexpected, char actual) {
        Assertions.assertNotEquals(unexpected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Character unexpected, Character actual) {
        Assertions.assertNotEquals(unexpected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, char unexpected, char actual, String message) {
        Assertions.assertNotEquals(unexpected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, char unexpected, Character actual, String message) {
        Assertions.assertNotEquals(unexpected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Character unexpected, char actual, String message) {
        Assertions.assertNotEquals(unexpected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Character unexpected, Character actual, String message) {
        Assertions.assertNotEquals(unexpected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, char unexpected, char actual, Supplier<String> messageSupplier) {
        Assertions.assertNotEquals(unexpected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, char unexpected, Character actual, Supplier<String> messageSupplier) {
        Assertions.assertNotEquals(unexpected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Character unexpected, char actual, Supplier<String> messageSupplier) {
        Assertions.assertNotEquals(unexpected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Character unexpected, Character actual, Supplier<String> messageSupplier) {
        Assertions.assertNotEquals(unexpected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Object unexpected, Object actual) {
        Assertions.assertNotEquals(unexpected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Object unexpected, Object actual, String message) {
        Assertions.assertNotEquals(unexpected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotEquals(String field, Object unexpected, Object actual, Supplier<String> messageSupplier) {
        Assertions.assertNotEquals(unexpected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertSame(String field, Object expected, Object actual) {
        Assertions.assertSame(expected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertSame(String field, Object expected, Object actual, String message) {
        Assertions.assertSame(expected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertSame(String field, Object expected, Object actual, Supplier<String> messageSupplier) {
        Assertions.assertSame(expected, actual, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotSame(String field, Object unexpected, Object actual) {
        Assertions.assertNotSame(unexpected, actual, field);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotSame(String field, Object unexpected, Object actual, String message) {
        Assertions.assertNotSame(unexpected, actual, message);
    }

    @Step("getLangValue:asserts.step.assert.not.equals")
    public static void assertNotSame(String field, Object unexpected, Object actual, Supplier<String> messageSupplier) {
        Assertions.assertNotSame(unexpected, actual, messageSupplier);
    }

    public static void assertAll(Executable... executables) throws MultipleFailuresError {
        Assertions.assertAll(executables);
    }

    public static void assertAll(String heading, Executable... executables) throws MultipleFailuresError {
        Assertions.assertAll(heading, executables);
    }

    public static void assertAll(Collection<Executable> executables) throws MultipleFailuresError {
        Assertions.assertAll(executables);
    }

    public static void assertAll(String heading, Collection<Executable> executables) throws MultipleFailuresError {
        Assertions.assertAll(heading, executables);
    }

    public static void assertAll(Stream<Executable> executables) throws MultipleFailuresError {
        Assertions.assertAll(executables);
    }

    public static void assertAll(String heading, Stream<Executable> executables) throws MultipleFailuresError {
        Assertions.assertAll(heading, executables);
    }

    @Step("getLangValue:asserts.step.assert.throws.exactly")
    public static <T extends Throwable> T assertThrowsExactly(Class<T> expectedType, Executable executable) {
        return Assertions.assertThrowsExactly(expectedType, executable);
    }

    @Step("getLangValue:asserts.step.assert.throws.exactly")
    public static <T extends Throwable> T assertThrowsExactly(Class<T> expectedType, Executable executable, String message) {
        return Assertions.assertThrowsExactly(expectedType, executable, message);
    }

    @Step("getLangValue:asserts.step.assert.throws.exactly")
    public static <T extends Throwable> T assertThrowsExactly(Class<T> expectedType, Executable executable, Supplier<String> messageSupplier) {
        return Assertions.assertThrowsExactly(expectedType, executable, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.throws.exactly")
    public static <T extends Throwable> T assertThrows(Class<T> expectedType, Executable executable) {
        return Assertions.assertThrows(expectedType, executable);
    }

    @Step("getLangValue:asserts.step.assert.throws.exactly")
    public static <T extends Throwable> T assertThrows(Class<T> expectedType, Executable executable, String message) {
        return Assertions.assertThrows(expectedType, executable, message);
    }

    @Step("getLangValue:asserts.step.assert.throws.exactly")
    public static <T extends Throwable> T assertThrows(Class<T> expectedType, Executable executable, Supplier<String> messageSupplier) {
        return Assertions.assertThrows(expectedType, executable, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.throws.exactly")
    public static void assertDoesNotThrow(Executable executable) {
        Assertions.assertDoesNotThrow(executable);
    }

    @Step("getLangValue:asserts.step.assert.throws.exactly")
    public static void assertDoesNotThrow(Executable executable, String message) {
        Assertions.assertDoesNotThrow(executable, message);
    }

    @Step("getLangValue:asserts.step.assert.throws.exactly")
    public static void assertDoesNotThrow(Executable executable, Supplier<String> messageSupplier) {
        Assertions.assertDoesNotThrow(executable, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.does.not.throw")
    public static <T> T assertDoesNotThrow(ThrowingSupplier<T> supplier) {
        return Assertions.assertDoesNotThrow(supplier);
    }

    @Step("getLangValue:asserts.step.assert.does.not.throw")
    public static <T> T assertDoesNotThrow(ThrowingSupplier<T> supplier, String message) {
        return Assertions.assertDoesNotThrow(supplier, message);
    }

    @Step("getLangValue:asserts.step.assert.does.not.throw")
    public static <T> T assertDoesNotThrow(ThrowingSupplier<T> supplier, Supplier<String> messageSupplier) {
        return Assertions.assertDoesNotThrow(supplier, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.timeout")
    public static void assertTimeout(Duration timeout, Executable executable) {
        Assertions.assertTimeout(timeout, executable);
    }

    @Step("getLangValue:asserts.step.assert.timeout")
    public static void assertTimeout(Duration timeout, Executable executable, String message) {
        Assertions.assertTimeout(timeout, executable, message);
    }

    @Step("getLangValue:asserts.step.assert.timeout")
    public static void assertTimeout(Duration timeout, Executable executable, Supplier<String> messageSupplier) {
        Assertions.assertTimeout(timeout, executable, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.timeout.supplier")
    public static <T> T assertTimeout(Duration timeout, ThrowingSupplier<T> supplier) {
        return Assertions.assertTimeout(timeout, supplier);
    }

    @Step("getLangValue:asserts.step.assert.timeout.supplier")
    public static <T> T assertTimeout(Duration timeout, ThrowingSupplier<T> supplier, String message) {
        return Assertions.assertTimeout(timeout, supplier, message);
    }

    @Step("getLangValue:asserts.step.assert.timeout.supplier")
    public static <T> T assertTimeout(Duration timeout, ThrowingSupplier<T> supplier, Supplier<String> messageSupplier) {
        return Assertions.assertTimeout(timeout, supplier, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.timeout")
    public static void assertTimeoutPreemptively(Duration timeout, Executable executable) {
        Assertions.assertTimeoutPreemptively(timeout, executable);
    }

    @Step("getLangValue:asserts.step.assert.timeout")
    public static void assertTimeoutPreemptively(Duration timeout, Executable executable, String message) {
        Assertions.assertTimeoutPreemptively(timeout, executable, message);
    }

    @Step("getLangValue:asserts.step.assert.timeout")
    public static void assertTimeoutPreemptively(Duration timeout, Executable executable, Supplier<String> messageSupplier) {
        Assertions.assertTimeoutPreemptively(timeout, executable, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.timeout.supplier")
    public static <T> T assertTimeoutPreemptively(Duration timeout, ThrowingSupplier<T> supplier) {
        return Assertions.assertTimeoutPreemptively(timeout, supplier);
    }

    @Step("getLangValue:asserts.step.assert.timeout.supplier")
    public static <T> T assertTimeoutPreemptively(Duration timeout, ThrowingSupplier<T> supplier, String message) {
        return Assertions.assertTimeoutPreemptively(timeout, supplier, message);
    }

    @Step("getLangValue:asserts.step.assert.timeout.supplier")
    public static <T> T assertTimeoutPreemptively(Duration timeout, ThrowingSupplier<T> supplier, Supplier<String> messageSupplier) {
        return Assertions.assertTimeoutPreemptively(timeout, supplier, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.instance.of")
    public static <T> T assertInstanceOf(Class<T> expectedType, Object actualValue) {
        return Assertions.assertInstanceOf(expectedType, actualValue);
    }

    @Step("getLangValue:asserts.step.assert.instance.of")
    public static <T> T assertInstanceOf(Class<T> expectedType, Object actualValue, String message) {
        return Assertions.assertInstanceOf(expectedType, actualValue, message);
    }

    @Step("getLangValue:asserts.step.assert.instance.of")
    public static <T> T assertInstanceOf(Class<T> expectedType, Object actualValue, Supplier<String> messageSupplier) {
        return Assertions.assertInstanceOf(expectedType, actualValue, messageSupplier);
    }

    @Step("getLangValue:asserts.step.assert.list.equals")
    public static <T> void assertListEquals(String field, List<T> expectedList, List<T> actualList) {
        try {
            assertThat(actualList).isEqualTo(expectedList);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.list.equals")
    public static <T> void assertListEquals(String field, List<T> expectedList, List<T> actualList, String message) {
            assertThat(actualList).withFailMessage(message).isEqualTo(expectedList);
    }

    @Step("getLangValue:asserts.step.assert.list.equals")
    public static <T> void assertListEqualsInAnyMatch(String field, List<T> expectedList, List<T> actualList) {
        try {
            assertThat(actualList).hasSameElementsAs(expectedList);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.list.equals")
    public static <T> void assertListEqualsInAnyMatch(String field, List<T> expectedList, List<T> actualList, String message) {
            assertThat(actualList).withFailMessage(message).hasSameElementsAs(expectedList);
    }

    @Step("getLangValue:asserts.step.assert.list.contains")
    public static <T> void assertListContains(String field, T expectedValue, List<T> actualList) {
        try {
            assertThat(actualList).contains(expectedValue);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.list.contains")
    public static <T> void assertListContains(String field, T expectedValue, List<T> actualList, String message) {
            assertThat(actualList).withFailMessage(message).contains(expectedValue);
    }

    @Step("getLangValue:asserts.step.assert.list.size")
    public static void assertListSize(String field, int size, List<?> actualList) {
        try {
            assertThat(actualList).hasSize(size);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.list.size")
    public static void assertListSize(String field, int size, List<?> actualList, String message) {
            assertThat(actualList).withFailMessage(message).hasSize(size);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertTimeEquals(String field, LocalTime expected, LocalTime actual) {
        try {
            assertThat(actual).isEqualTo(expected);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertTimeEquals(String field, LocalTime expected, LocalTime actual, String message) {
            assertThat(actual).withFailMessage(message).isEqualTo(expected);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertDateEquals(String field, LocalDate expected, LocalDate actual) {
        try {
            assertThat(actual).isEqualTo(expected);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertDateEquals(String field, LocalDate expected, LocalDate actual, String message) {
            assertThat(actual).withFailMessage(message).isEqualTo(expected);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertDateTimeEquals(String field, LocalDateTime expected, LocalDateTime actual) {
        try {
            assertThat(actual).isEqualTo(expected);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertDateTimeEquals(String field, LocalDateTime expected, LocalDateTime actual, String message) {
            assertThat(actual).withFailMessage(message).isEqualTo(expected);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertDateTimeEquals(String field, ZonedDateTime expected, ZonedDateTime actual) {
        try {
            assertThat(actual).isEqualTo(expected);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertDateTimeEquals(String field, ZonedDateTime expected, ZonedDateTime actual, String message) {
            assertThat(actual).withFailMessage(message).isEqualTo(expected);
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertDateTimeEquals(String field, OffsetDateTime expected, OffsetDateTime actual) {
        try {
            assertThat(actual).isEqualTo(expected);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.equals")
    public static void assertDateTimeEquals(String field, OffsetDateTime expected, OffsetDateTime actual, String message) {
            assertThat(actual).withFailMessage(message).isEqualTo(expected);
    }

    @Step("getLangValue:asserts.step.assert.time.is.after")
    public static void assertTimeIsAfter(String field, LocalTime expected, LocalTime actual) {
        try {
            assertThat(actual).isAfter(expected);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.time.is.after")
    public static void assertTimeIsAfter(String field, LocalTime expected, LocalTime actual, String message) {
            assertThat(actual).withFailMessage(message).isAfter(expected);
    }

    @Step("getLangValue:asserts.step.assert.time.is.after")
    public static void assertDateIsAfter(String field, LocalDate expected, LocalDate actual) {
        try {
            assertThat(actual).isAfter(expected);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.time.is.after")
    public static void assertDateIsAfter(String field, LocalDate expected, LocalDate actual, String message) {
            assertThat(actual).withFailMessage(message).isAfter(expected);
    }

    @Step("getLangValue:asserts.step.assert.time.is.after")
    public static void assertDateTimeIsAfter(String field, LocalDateTime expected, LocalDateTime actual) {
        try {
            assertThat(actual).isAfter(expected);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.time.is.after")
    public static void assertDateTimeIsAfter(String field, LocalDateTime expected, LocalDateTime actual, String message) {
            assertThat(actual).withFailMessage(message).isAfter(expected);
    }

    @Step("getLangValue:asserts.step.assert.time.is.after")
    public static void assertDateTimeIsAfter(String field, ZonedDateTime expected, ZonedDateTime actual) {
        try {
            assertThat(actual).isAfter(expected);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.time.is.after")
    public static void assertDateTimeIsAfter(String field, ZonedDateTime expected, ZonedDateTime actual, String message) {
            assertThat(actual).withFailMessage(message).isAfter(expected);
    }

    @Step("getLangValue:asserts.step.assert.time.is.after")
    public static void assertDateTimeIsAfter(String field, OffsetDateTime expected, OffsetDateTime actual) {
        try {
            assertThat(actual).isAfter(expected);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.time.is.after")
    public static void assertDateTimeIsAfter(String field, OffsetDateTime expected, OffsetDateTime actual, String message) {
            assertThat(actual).withFailMessage(message).isAfter(expected);
    }

    @Step("getLangValue:asserts.step.assert.time.is.after.or.equal.to")
    public static void assertTimeIsAfterOrEqualTo(String field, LocalTime expected, LocalTime actual) {
        try {
            assertThat(actual).isAfterOrEqualTo(expected);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.time.is.after.or.equal.to")
    public static void assertTimeIsAfterOrEqualTo(String field, LocalTime expected, LocalTime actual, String message) {
            assertThat(actual).withFailMessage(message).isAfterOrEqualTo(expected);
    }

    @Step("getLangValue:asserts.step.assert.time.is.after.or.equal.to")
    public static void assertDateIsAfterOrEqualTo(String field, LocalDate expected, LocalDate actual) {
        try {
            assertThat(actual).isAfterOrEqualTo(expected);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.time.is.after.or.equal.to")
    public static void assertDateIsAfterOrEqualTo(String field, LocalDate expected, LocalDate actual, String message) {
            assertThat(actual).withFailMessage(message).isAfterOrEqualTo(expected);
    }

    @Step("getLangValue:asserts.step.assert.time.is.after.or.equal.to")
    public static void assertDateTimeIsAfterOrEqualTo(String field, LocalDateTime expected, LocalDateTime actual) {
        try {
            assertThat(actual).isAfterOrEqualTo(expected);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.time.is.after.or.equal.to")
    public static void assertDateTimeIsAfterOrEqualTo(String field, LocalDateTime expected, LocalDateTime actual, String message) {
            assertThat(actual).withFailMessage(message).isAfterOrEqualTo(expected);
    }

    @Step("getLangValue:asserts.step.assert.time.is.after.or.equal.to")
    public static void assertDateTimeIsAfterOrEqualTo(String field, ZonedDateTime expected, ZonedDateTime actual) {
        try {
            assertThat(actual).isAfterOrEqualTo(expected);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.time.is.after.or.equal.to")
    public static void assertDateTimeIsAfterOrEqualTo(String field, ZonedDateTime expected, ZonedDateTime actual, String message) {
            assertThat(actual).withFailMessage(message).isAfterOrEqualTo(expected);
    }

    @Step("getLangValue:asserts.step.assert.time.is.after.or.equal.to")
    public static void assertDateTimeIsAfterOrEqualTo(String field, OffsetDateTime expected, OffsetDateTime actual) {
        try {
            assertThat(actual).isAfterOrEqualTo(expected);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.time.is.after.or.equal.to")
    public static void assertDateTimeIsAfterOrEqualTo(String field, OffsetDateTime expected, OffsetDateTime actual, String message) {
            assertThat(actual).withFailMessage(message).isAfterOrEqualTo(expected);
    }

    @Step("getLangValue:asserts.step.assert.time.is.before")
    public static void assertTimeIsBefore(String field, LocalTime expected, LocalTime actual) {
        try {
            assertThat(actual).isBefore(expected);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.time.is.before")
    public static void assertTimeIsBefore(String field, LocalTime expected, LocalTime actual, String message) {
            assertThat(actual).withFailMessage(message).isBefore(expected);
    }

    @Step("getLangValue:asserts.step.assert.time.is.before")
    public static void assertDateIsBefore(String field, LocalDate expected, LocalDate actual) {
        try {
            assertThat(actual).isBefore(expected);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.time.is.before")
    public static void assertDateIsBefore(String field, LocalDate expected, LocalDate actual, String message) {
            assertThat(actual).withFailMessage(message).isBefore(expected);
    }

    @Step("getLangValue:asserts.step.assert.time.is.before")
    public static void assertDateTimeIsBefore(String field, LocalDateTime expected, LocalDateTime actual) {
        try {
            assertThat(actual).isBefore(expected);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.time.is.before")
    public static void assertDateTimeIsBefore(String field, LocalDateTime expected, LocalDateTime actual, String message) {
            assertThat(actual).withFailMessage(message).isBefore(expected);
    }

    @Step("getLangValue:asserts.step.assert.time.is.before")
    public static void assertDateTimeIsBefore(String field, ZonedDateTime expected, ZonedDateTime actual) {
        try {
            assertThat(actual).isBefore(expected);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.time.is.before")
    public static void assertDateTimeIsBefore(String field, ZonedDateTime expected, ZonedDateTime actual, String message) {
            assertThat(actual).withFailMessage(message).isBefore(expected);
    }

    @Step("getLangValue:asserts.step.assert.time.is.before")
    public static void assertDateTimeIsBefore(String field, OffsetDateTime expected, OffsetDateTime actual) {
        try {
            assertThat(actual).isBefore(expected);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.time.is.before")
    public static void assertDateTimeIsBefore(String field, OffsetDateTime expected, OffsetDateTime actual, String message) {
            assertThat(actual).withFailMessage(message).isBefore(expected);
    }

    @Step("getLangValue:asserts.step.assert.time.is.before.or.equal.to")
    public static void assertTimeIsBeforeOrEqualTo(String field, LocalTime expected, LocalTime actual) {
        try {
            assertThat(actual).isBeforeOrEqualTo(expected);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.time.is.before.or.equal.to")
    public static void assertTimeIsBeforeOrEqualTo(String field, LocalTime expected, LocalTime actual, String message) {
            assertThat(actual).withFailMessage(message).isBeforeOrEqualTo(expected);
    }

    @Step("getLangValue:asserts.step.assert.time.is.before.or.equal.to")
    public static void assertDateIsBeforeOrEqualTo(String field, LocalDate expected, LocalDate actual) {
        try {
            assertThat(actual).isBeforeOrEqualTo(expected);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.time.is.before.or.equal.to")
    public static void assertDateIsBeforeOrEqualTo(String field, LocalDate expected, LocalDate actual, String message) {
            assertThat(actual).withFailMessage(message).isBeforeOrEqualTo(expected);
    }

    @Step("getLangValue:asserts.step.assert.time.is.before.or.equal.to")
    public static void assertDateTimeIsBeforeOrEqualTo(String field, LocalDateTime expected, LocalDateTime actual) {
        try {
            assertThat(actual).isBeforeOrEqualTo(expected);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.time.is.before.or.equal.to")
    public static void assertDateTimeIsBeforeOrEqualTo(String field, LocalDateTime expected, LocalDateTime actual, String message) {
            assertThat(actual).withFailMessage(message).isBeforeOrEqualTo(expected);
    }

    @Step("getLangValue:asserts.step.assert.time.is.before.or.equal.to")
    public static void assertDateTimeIsBeforeOrEqualTo(String field, ZonedDateTime expected, ZonedDateTime actual) {
        try {
            assertThat(actual).isBeforeOrEqualTo(expected);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.time.is.before.or.equal.to")
    public static void assertDateTimeIsBeforeOrEqualTo(String field, ZonedDateTime expected, ZonedDateTime actual, String message) {
            assertThat(actual).withFailMessage(message).isBeforeOrEqualTo(expected);
    }

    @Step("getLangValue:asserts.step.assert.time.is.before.or.equal.to")
    public static void assertDateTimeIsBeforeOrEqualTo(String field, OffsetDateTime expected, OffsetDateTime actual) {
        try {
            assertThat(actual).isBeforeOrEqualTo(expected);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.time.is.before.or.equal.to")
    public static void assertDateTimeIsBeforeOrEqualTo(String field, OffsetDateTime expected, OffsetDateTime actual, String message) {
            assertThat(actual).withFailMessage(message).isBeforeOrEqualTo(expected);
    }

    @Step("getLangValue:asserts.step.assert.time.is.between")
    public static void assertTimeIsBetween(String field, LocalTime expectedStartTime, LocalTime expectedEndTime, LocalTime actual) {
        try {
            assertThat(actual).isStrictlyBetween(expectedStartTime, expectedEndTime);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.time.is.between")
    public static void assertTimeIsBetween(String field, LocalTime expectedStartTime, LocalTime expectedEndTime,
                                           LocalTime actual, String message) {
            assertThat(actual).withFailMessage(message).isStrictlyBetween(expectedStartTime, expectedEndTime);
    }

    @Step("getLangValue:asserts.step.assert.date.is.between")
    public static void assertDateIsBetween(String field, LocalDate expectedStartDate, LocalDate expectedEndDate, LocalDate actual) {
        try {
            assertThat(actual).isStrictlyBetween(expectedStartDate, expectedEndDate);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.date.is.between")
    public static void assertDateIsBetween(String field, LocalDate expectedStartDate, LocalDate expectedEndDate,
                                           LocalDate actual, String message) {
            assertThat(actual).withFailMessage(message).isStrictlyBetween(expectedStartDate, expectedEndDate);
    }

    @Step("getLangValue:asserts.step.assert.time.is.between")
    public static void assertDateTimeIsBetween(String field, LocalDateTime expectedStartTime,
                                               LocalDateTime expectedEndTime, LocalDateTime actual) {
        try {
            assertThat(actual).isStrictlyBetween(expectedStartTime, expectedEndTime);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.time.is.between")
    public static void assertDateTimeIsBetween(String field, LocalDateTime expectedStartTime,
                                               LocalDateTime expectedEndTime, LocalDateTime actual, String message) {
            assertThat(actual).withFailMessage(message).isStrictlyBetween(expectedStartTime, expectedEndTime);
    }

    @Step("getLangValue:asserts.step.assert.time.is.between")
    public static void assertDateTimeIsBetween(String field, ZonedDateTime expectedStartTime,
                                               ZonedDateTime expectedEndTime, ZonedDateTime actual) {
        try {
            assertThat(actual).isStrictlyBetween(expectedStartTime, expectedEndTime);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.time.is.between")
    public static void assertDateTimeIsBetween(String field, ZonedDateTime expectedStartTime,
                                               ZonedDateTime expectedEndTime, ZonedDateTime actual, String message) {
            assertThat(actual).withFailMessage(message).isStrictlyBetween(expectedStartTime, expectedEndTime);
    }

    @Step("getLangValue:asserts.step.assert.time.is.between")
    public static void assertDateTimeIsBetween(String field, OffsetDateTime expectedStartTime,
                                               OffsetDateTime expectedEndTime, OffsetDateTime actual) {
        try {
            assertThat(actual).isStrictlyBetween(expectedStartTime, expectedEndTime);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.time.is.between")
    public static void assertDateTimeIsBetween(String field, OffsetDateTime expectedStartTime,
                                               OffsetDateTime expectedEndTime, OffsetDateTime actual, String message) {
            assertThat(actual).withFailMessage(message).isStrictlyBetween(expectedStartTime, expectedEndTime);
    }

    @Step("getLangValue:asserts.step.assert.time.is.between.or.equal.to")
    public static void assertTimeIsBetweenOrEqualTo(String field, LocalTime expectedStartTime,
                                                    LocalTime expectedEndTime, LocalTime actual) {
        try {
            assertThat(actual).isBetween(expectedStartTime, expectedEndTime);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.time.is.between.or.equal.to")
    public static void assertTimeIsBetweenOrEqualTo(String field, LocalTime expectedStartTime,
                                                    LocalTime expectedEndTime, LocalTime actual, String message) {
            assertThat(actual).withFailMessage(message).isBetween(expectedStartTime, expectedEndTime);
    }

    @Step("getLangValue:asserts.step.assert.date.is.between.or.equal.to")
    public static void assertDateIsBetweenOrEqualTo(String field, LocalDate expectedStartDate,
                                                    LocalDate expectedEndDate, LocalDate actual) {
        try {
            assertThat(actual).isBetween(expectedStartDate, expectedEndDate);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.date.is.between.or.equal.to")
    public static void assertDateIsBetweenOrEqualTo(String field, LocalDate expectedStartDate,
                                                    LocalDate expectedEndDate, LocalDate actual, String message) {
            assertThat(actual).withFailMessage(message).isBetween(expectedStartDate, expectedEndDate);
    }

    @Step("getLangValue:asserts.step.assert.time.is.between.or.equal.to")
    public static void assertDateTimeIsBetweenOrEqualTo(String field, LocalDateTime expectedStartTime,
                                                        LocalDateTime expectedEndTime, LocalDateTime actual) {
        try {
            assertThat(actual).isBetween(expectedStartTime, expectedEndTime);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.time.is.between.or.equal.to")
    public static void assertDateTimeIsBetweenOrEqualTo(String field, LocalDateTime expectedStartTime,
                                                        LocalDateTime expectedEndTime, LocalDateTime actual, String message) {
            assertThat(actual).withFailMessage(message).isBetween(expectedStartTime, expectedEndTime);
    }

    @Step("getLangValue:asserts.step.assert.time.is.between.or.equal.to")
    public static void assertDateTimeIsBetweenOrEqualTo(String field, ZonedDateTime expectedStartTime,
                                                        ZonedDateTime expectedEndTime, ZonedDateTime actual) {
        try {
            assertThat(actual).isBetween(expectedStartTime, expectedEndTime);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.time.is.between.or.equal.to")
    public static void assertDateTimeIsBetweenOrEqualTo(String field, ZonedDateTime expectedStartTime,
                                                        ZonedDateTime expectedEndTime, ZonedDateTime actual, String message) {
            assertThat(actual).withFailMessage(message).isBetween(expectedStartTime, expectedEndTime);
    }

    @Step("getLangValue:asserts.step.assert.time.is.between.or.equal.to")
    public static void assertDateTimeIsBetweenOrEqualTo(String field, OffsetDateTime expectedStartTime,
                                                        OffsetDateTime expectedEndTime, OffsetDateTime actual) {
        try {
            assertThat(actual).isBetween(expectedStartTime, expectedEndTime);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.time.is.between.or.equal.to")
    public static void assertDateTimeIsBetweenOrEqualTo(String field, OffsetDateTime expectedStartTime,
                                                        OffsetDateTime expectedEndTime, OffsetDateTime actual, String message) {
            assertThat(actual).withFailMessage(message).isBetween(expectedStartTime, expectedEndTime);
    }

    @Step("getLangValue:asserts.step.assert.start.with")
    public static void assertStartWith(String field, String expectedPrefix, String actual) {
        try {
            assertThat(actual).startsWith(expectedPrefix);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.start.with")
    public static void assertStartWith(String field, String expectedPrefix, String actual, String message) {
            assertThat(actual).withFailMessage(message).startsWith(expectedPrefix);
    }

    @Step("getLangValue:asserts.step.assert.start.with.ignoring.case")
    public static void assertStartWithIgnoringCase(String field, String expectedPrefix, String actual) {
        try {
            assertThat(actual).startsWithIgnoringCase(expectedPrefix);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.start.with.ignoring.case")
    public static void assertStartWithIgnoringCase(String field, String expectedPrefix, String actual, String message) {
            assertThat(actual).withFailMessage(message).startsWithIgnoringCase(expectedPrefix);
    }

    @Step("getLangValue:asserts.step.assert.not.start.with")
    public static void assertDoesNotStartWith(String field, String expectedNotPrefix, String actual) {
        try {
            assertThat(actual).doesNotStartWith(expectedNotPrefix);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.not.start.with")
    public static void assertDoesNotStartWith(String field, String expectedNotPrefix, String actual, String message) {
            assertThat(actual).withFailMessage(message).doesNotStartWith(expectedNotPrefix);
    }

    @Step("getLangValue:asserts.step.assert.not.start.with.ignoring.case")
    public static void assertDoesNotStartWithIgnoringCase(String field, String expectedNotPrefix, String actual) {
        try {
            assertThat(actual).doesNotStartWithIgnoringCase(expectedNotPrefix);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.not.start.with.ignoring.case")
    public static void assertDoesNotStartWithIgnoringCase(String field, String expectedNotPrefix, String actual, String message) {
            assertThat(actual).withFailMessage(message).doesNotStartWithIgnoringCase(expectedNotPrefix);
    }

    @Step("getLangValue:asserts.step.assert.end.with")
    public static void assertEndWith(String field, String expectedSuffix, String actual) {
        try {
            assertThat(actual).endsWith(expectedSuffix);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.end.with")
    public static void assertEndWith(String field, String expectedSuffix, String actual, String message) {
            assertThat(actual).withFailMessage(message).endsWith(expectedSuffix);
    }

    @Step("getLangValue:asserts.step.assert.end.with.ignoring.case")
    public static void assertEndWithIgnoringCase(String field, String expectedSuffix, String actual) {
        try {
            assertThat(actual).startsWithIgnoringCase(expectedSuffix);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.end.with.ignoring.case")
    public static void assertEndWithIgnoringCase(String field, String expectedSuffix, String actual, String message) {
            assertThat(actual).withFailMessage(message).startsWithIgnoringCase(expectedSuffix);
    }

    @Step("getLangValue:asserts.step.assert.not.end.with")
    public static void assertDoesNotEndWith(String field, String expectedNotSuffix, String actual) {
        try {
            assertThat(actual).doesNotEndWith(expectedNotSuffix);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.not.end.with")
    public static void assertDoesNotEndWith(String field, String expectedNotSuffix, String actual, String message) {
            assertThat(actual).withFailMessage(message).doesNotEndWith(expectedNotSuffix);
    }

    @Step("getLangValue:asserts.step.assert.not.end.with.ignoring.case")
    public static void assertDoesNotEndWithIgnoringCase(String field, String expectedNotSuffix, String actual) {
        try {
            assertThat(actual).doesNotEndWithIgnoringCase(expectedNotSuffix);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.not.end.with.ignoring.case")
    public static void assertDoesNotEndWithIgnoringCase(String field, String expectedNotSuffix, String actual, String message) {
            assertThat(actual).withFailMessage(message).doesNotEndWithIgnoringCase(expectedNotSuffix);
    }

    @Step("getLangValue:asserts.step.assert.equals.with.ignoring.case")
    public static void assertEqualsWithIgnoringCase(String field, String expected, String actual) {
        try {
            assertThat(actual).isEqualToIgnoringCase(expected);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.equals.with.ignoring.case")
    public static void assertEqualsWithIgnoringCase(String field, String expected, String actual, String message) {
            assertThat(actual).withFailMessage(message).isEqualToIgnoringCase(expected);
    }

    @Step("getLangValue:asserts.step.assert.not.equals.with.ignoring.case")
    public static void assertNotEqualsWithIgnoringCase(String field, String notExpected, String actual) {
        try {
            assertThat(actual).isNotEqualToIgnoringCase(notExpected);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.not.equals.with.ignoring.case")
    public static void assertNotEqualsWithIgnoringCase(String field, String notExpected, String actual, String message) {
            assertThat(actual).withFailMessage(message).isNotEqualToIgnoringCase(notExpected);
    }

    @Step("getLangValue:asserts.step.assert.equals.with.ignoring.whitespace")
    public static void assertEqualsWithIgnoringWhitespace(String field, String expected, String actual) {
        try {
            assertThat(actual).isEqualToIgnoringWhitespace(expected);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.equals.with.ignoring.whitespace")
    public static void assertEqualsWithIgnoringWhitespace(String field, String expected, String actual, String message) {
            assertThat(actual).withFailMessage(message).isEqualToIgnoringWhitespace(expected);
    }

    @Step("getLangValue:asserts.step.assert.not.equals.with.ignoring.whitespace")
    public static void assertNotEqualsWithIgnoringWhitespace(String field, String expected, String actual) {
        try {
            assertThat(actual).isNotEqualToIgnoringWhitespace(expected);
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.not.equals.with.ignoring.whitespace")
    public static void assertNotEqualsWithIgnoringWhitespace(String field, String expected, String actual, String message) {
            assertThat(actual).withFailMessage(message).isNotEqualToIgnoringWhitespace(expected);
    }

    @Step("getLangValue:asserts.step.assert.contains.only.whitespaces")
    public static void assertContainsOnlyWhitespaces(String field, String actual) {
        try {
            assertThat(actual).containsOnlyWhitespaces();
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.contains.only.whitespaces")
    public static void assertContainsOnlyWhitespaces(String field, String actual, String message) {
            assertThat(actual).withFailMessage(message).containsOnlyWhitespaces();
    }

    @Step("getLangValue:asserts.step.assert.contains.whitespaces")
    public static void assertContainsWhitespaces(String field, String actual) {
        try {
            assertThat(actual).containsWhitespaces();
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.contains.whitespaces")
    public static void assertContainsWhitespaces(String field, String actual, String message) {
            assertThat(actual).withFailMessage(message).containsWhitespaces();
    }

    @Step("getLangValue:asserts.step.assert.not.contains.whitespaces")
    public static void assertNotContainsWhitespaces(String field, String actual) {
        try {
            assertThat(actual).doesNotContainAnyWhitespaces();
        } catch (AssertionError e) {
            throw new AssertionError("%s ==> %s".formatted(field, e.getMessage()));
        }
    }

    @Step("getLangValue:asserts.step.assert.not.contains.whitespaces")
    public static void assertNotContainsWhitespaces(String field, String actual, String message) {
            assertThat(actual).withFailMessage(message).doesNotContainAnyWhitespaces();
    }
}
