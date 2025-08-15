package ru.origami.common.utils;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import ru.origami.common.environment.Environment;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.GLOBAL;

public class BeforeAllTestsExtension implements BeforeAllCallback {

    private static final AtomicBoolean IS_FIRST_EXECUTE = new AtomicBoolean(true);

    @Override
    public void beforeAll(ExtensionContext context) {
        if (IS_FIRST_EXECUTE.getAndSet(false)) {
            new Environment();
            context.getRoot().getStore(GLOBAL).put("Environment is registered", this);
        }
    }
}
