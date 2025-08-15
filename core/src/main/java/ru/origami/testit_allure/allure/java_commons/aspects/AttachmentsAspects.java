package ru.origami.testit_allure.allure.java_commons.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import ru.origami.testit_allure.allure.java_commons.Allure;
import ru.origami.testit_allure.allure.java_commons.AllureLifecycle;
import ru.origami.testit_allure.allure.java_commons.Attachment;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static ru.origami.testit_allure.allure.java_commons.util.AspectUtils.getParametersMap;
import static ru.origami.testit_allure.allure.java_commons.util.NamingUtils.processNameTemplate;

/**
 * Aspects (AspectJ) for handling {@link Attachment}.
 */
@Aspect
public class AttachmentsAspects {

    private static final InheritableThreadLocal<AllureLifecycle> LIFECYCLE =
            new InheritableThreadLocal<AllureLifecycle>() {
        @Override
        protected AllureLifecycle initialValue() {
            return Allure.getLifecycle();
        }
    };

    /**
     * Pointcut for things annotated with {@link Attachment}.
     */
    @Pointcut("@annotation(ru.origami.testit_allure.allure.java_commons.Attachment)")
    public void withAttachmentAnnotation() {
        //pointcut body, should be empty
    }

    /**
     * Pointcut for any methods.
     */
    @Pointcut("execution(* *(..))")
    public void anyMethod() {
        //pointcut body, should be empty
    }

    /**
     * Process data returned from method annotated with {@link Attachment}.
     * If returned data is not a byte array, then use toString() method, and get bytes from it.
     *
     * @param joinPoint the join point to process.
     * @param result    the returned value.
     */
    @AfterReturning(pointcut = "anyMethod() && withAttachmentAnnotation()", returning = "result")
    public void attachment(final JoinPoint joinPoint, final Object result) {
        final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        final Attachment attachment = methodSignature.getMethod()
                .getAnnotation(Attachment.class);
        final byte[] bytes = (result instanceof byte[]) ? (byte[]) result : Objects.toString(result)
                .getBytes(StandardCharsets.UTF_8);

        final String name = attachment.value().isEmpty()
                ? methodSignature.getName()
                : processNameTemplate(attachment.value(), getParametersMap(joinPoint));
        getLifecycle().addAttachment(name, attachment.type(), attachment.fileExtension(), bytes);
    }

    /**
     * For tests only.
     *
     * @param allure allure lifecycle to set.
     */
    public static void setLifecycle(final AllureLifecycle allure) {
        LIFECYCLE.set(allure);
    }

    public static AllureLifecycle getLifecycle() {
        return LIFECYCLE.get();
    }
}
