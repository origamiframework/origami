package ru.origami.common.parallel;

import lombok.Getter;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.fail;
import static ru.origami.common.environment.Language.getLangValue;

public class EnvironmentPool {

    @Getter
    private final TestEnvironment[] environments;

    private final BlockingQueue<TestEnvironment> available;

    public EnvironmentPool(int size) {
        environments = new TestEnvironment[size];
        available = new ArrayBlockingQueue<>(size, true);

        for (int i = 0; i < size; i++) {
            environments[i] = new TestEnvironment(i + 1);
            available.add(environments[i]);
        }
    }

    public TestEnvironment acquire() {
        try {
            TestEnvironment env = available.poll(20, TimeUnit.MINUTES);

            if (env == null) {
                fail(getLangValue("test.containers.fail.get.free.test.env.timeout"));
            }

            return env;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail(getLangValue("test.containers.fail.get.free.test.env"));

            return null;
        }
    }

    public synchronized void release(TestEnvironment env) {
        if (env == null) {
            return;
        }

        if (!available.contains(env)) {
            available.offer(env);
        }
    }
}
