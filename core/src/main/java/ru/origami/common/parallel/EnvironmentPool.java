package ru.origami.common.parallel;

import lombok.Getter;

import java.util.concurrent.Semaphore;
import static ru.origami.common.environment.Language.getLangValue;

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

    public TestEnvironment acquire() {
        try {
            semaphore.acquire();

            for (TestEnvironment env : environments) {
                if (env.tryAcquire()) {
                    return env;
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException(getLangValue("test.containers.fail.get.free.test.env"));
        }

        throw new IllegalStateException(getLangValue("test.containers.no.free.test.env"));
    }

    public void release(TestEnvironment env) {
        env.release();
        semaphore.release();
    }
}
