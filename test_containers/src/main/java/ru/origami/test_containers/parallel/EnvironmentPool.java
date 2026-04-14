package ru.origami.test_containers.parallel;

import lombok.Getter;
import ru.origami.common.parallel.TestEnvironment;

import java.util.concurrent.Semaphore;

public class EnvironmentPool {

    @Getter
    private final TestEnvironment[] environments;

    private final Semaphore semaphore;

    public EnvironmentPool(int size) {
        environments = new TestEnvironment[size];

        for (int i = 0; i < size; i++) {
            environments[i] = new TestEnvironment(i + 1);
        }

        semaphore = new Semaphore(size, true);
    }

    public TestEnvironment acquire() throws InterruptedException {
        semaphore.acquire();

        for (TestEnvironment env : environments) {
            if (env.tryAcquire()) {
                return env;
            }
        }

        throw new IllegalStateException("No free environment despite semaphore");
    }

    public void release(TestEnvironment env) {
        env.release();
        semaphore.release();
    }
}
