package ru.origami.common.parallel;

import lombok.Getter;

public class TestEnvironment {

    @Getter
    private final int id;

    public TestEnvironment(int id) {
        this.id = id;
    }
}
