package ru.origami.common.parallel;

import lombok.Getter;

import java.util.concurrent.Semaphore;
import static ru.origami.common.environment.Environment.EXECUTION_PARALLEL_THREADS;

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
            e.printStackTrace();
        }

        throw new IllegalStateException("No free environment despite semaphore");
    }

    public void release(TestEnvironment env) {
        env.release();
        semaphore.release();
    }

    public static int getExecutionParallelThreads() {
        try {
            int threads = Integer.parseInt(EXECUTION_PARALLEL_THREADS);

            return threads > 0 ? threads : 1;
        } catch (NumberFormatException e) {
            return 1;
        }
    }
}
