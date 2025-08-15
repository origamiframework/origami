package common;

import org.junit.jupiter.api.Test;
import ru.origami.common.asserts.Asserts;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AssertTests {

    @Test
    public void successTestBoolean() {
        Asserts.assertTrue("field_1", true);
    }

    @Test
    public void successTestEquals() {
        Asserts.assertEquals("field34", 123L, 123);
    }

    @Test
    public void successTestListEqualsInAnyMatch() {
        Asserts.assertListEqualsInAnyMatch("field34", new ArrayList<>(List.of("12312", "123")), new ArrayList<>(List.of("123", "12312")));
    }

    @Test
    public void failureTestBoolean() {
        Asserts.assertTrue("field_2", false);
    }

    @Test
    public void failureTestEquals() {
        Asserts.assertEquals("field34", "123", "456");
    }

    @Test
    public void failureTestListContains() {
        Asserts.assertListContains("field34", "123454", new ArrayList<>(List.of("12312", "123")));
    }

    @Test
    public void failureTestListSize() {
        Asserts.assertListSize("field34", 10, new ArrayList<>(List.of("12312", "123")));
    }

    @Test
    public void failureTestTime() {
        Asserts.assertTimeIsBetween("field34", LocalTime.now(), LocalTime.now(), LocalTime.now());
    }
}
