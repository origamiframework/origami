package ru.origami.common.parallel;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class TestEnvironment {

    @Getter
    private final int id;

    private boolean isBusy = false;

    @Getter
    private Map<String, Integer> genericContainerInfos = new HashMap<>();

    public TestEnvironment(int id) {
        this.id = id;
    }

    public synchronized boolean tryAcquire() {
        if (!isBusy) {
            isBusy = true;

            return true;
        }

        return false;
    }

    public synchronized void release() {
        isBusy = false;
    }
}
