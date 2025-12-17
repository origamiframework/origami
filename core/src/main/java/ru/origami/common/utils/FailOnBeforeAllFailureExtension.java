package ru.origami.common.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import ru.origami.common.models.BeforeAllErrorInfo;

import java.lang.reflect.Method;

public class FailOnBeforeAllFailureExtension implements BeforeEachCallback, InvocationInterceptor {

    private static final String INIT_ERROR_KEY = "beforeAllError";

    private ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getStore(
                ExtensionContext.Namespace.create(
                        FailOnBeforeAllFailureExtension.class,
                        context.getRequiredTestClass()
                )
        );
    }

    @Override
    public void interceptBeforeAllMethod(Invocation<Void> invocation, ReflectiveInvocationContext<Method> invocationContext,
                                         ExtensionContext extensionContext) {
        Method beforeAllMethod = invocationContext.getExecutable();
        String className = beforeAllMethod.getDeclaringClass().getName();
        String methodName = beforeAllMethod.getName();

        try {
            invocation.proceed();
        } catch (Throwable t) {
            System.err.printf("BeforeAll FAILED in %s#%s: %s%n", className, methodName, t);

            t.printStackTrace(System.err);

            BeforeAllErrorInfo info = BeforeAllErrorInfo.Builder()
                    .setThrowable(t)
                    .setTestClassName(className)
                    .setMethodName(methodName)
                    .build();
            getStore(extensionContext).put(INIT_ERROR_KEY, info);
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        BeforeAllErrorInfo info = getStore(context).get(INIT_ERROR_KEY, BeforeAllErrorInfo.class);

        if (info != null) {
            String message = String.format(
                    "Инициализация в @BeforeAll упала в %s#%s: %s",
                    info.getTestClassName(),
                    info.getMethodName(),
                    info.getThrowable().toString()
            );

            Assertions.fail(message, info.getThrowable());
        }
    }
}
