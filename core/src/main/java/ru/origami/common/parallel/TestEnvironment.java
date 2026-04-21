package ru.origami.common.parallel;

import lombok.Getter;

public class TestEnvironment {

    @Getter
    private final int id;

    private boolean isBusy = false;

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
