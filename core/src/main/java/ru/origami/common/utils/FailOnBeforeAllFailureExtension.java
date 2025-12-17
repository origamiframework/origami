package ru.origami.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;
import ru.origami.common.models.BeforeAllErrorInfo;

import java.lang.reflect.Method;
import java.util.Objects;

import static ru.origami.common.environment.Language.getLangValue;

@Slf4j
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
        try {
            invocation.proceed();
        } catch (Throwable t) {
            Method beforeAllMethod = invocationContext.getExecutable();
            String className = beforeAllMethod.getDeclaringClass().getName();
            String methodName = beforeAllMethod.getName();

            DisplayName displayNameAnn = beforeAllMethod.getAnnotation(DisplayName.class);
            String methodDisplayName = (displayNameAnn != null)
                    ? displayNameAnn.value()
                    : methodName;

            log.error(getLangValue("before.all.error"), className, methodName, t);
            t.printStackTrace(System.err);

            BeforeAllErrorInfo info = BeforeAllErrorInfo.Builder()
                    .setThrowable(t)
                    .setTestClassName(className)
                    .setMethodName(methodDisplayName)
                    .build();
            getStore(extensionContext).put(INIT_ERROR_KEY, info);
        }
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        BeforeAllErrorInfo info = getStore(context).get(INIT_ERROR_KEY, BeforeAllErrorInfo.class);

        if (Objects.nonNull(info)) {
            String message = getLangValue("before.all.error.after.each").formatted(info.getMethodName(), info.getThrowable().toString());

            Assertions.fail(message, info.getThrowable());
        }
    }
}
