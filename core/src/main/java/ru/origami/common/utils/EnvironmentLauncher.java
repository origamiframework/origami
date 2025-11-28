package ru.origami.common.utils;

import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.LauncherSessionListener;
import ru.origami.common.environment.Environment;

import java.util.concurrent.atomic.AtomicBoolean;

public class EnvironmentLauncher implements LauncherSessionListener {

    private static final AtomicBoolean IS_FIRST_EXECUTE = new AtomicBoolean(true);

    @Override
    public void launcherSessionOpened(LauncherSession session) {
        if (IS_FIRST_EXECUTE.getAndSet(false)) {
            new Environment();
        }
    }
}
