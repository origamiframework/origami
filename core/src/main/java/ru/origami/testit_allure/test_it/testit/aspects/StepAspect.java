package ru.origami.testit_allure.test_it.testit.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import ru.origami.testit_allure.annotations.Step;
import ru.origami.testit_allure.test_it.testit.models.ItemStatus;
import ru.origami.testit_allure.test_it.testit.models.StepResult;
import ru.origami.testit_allure.test_it.testit.services.Adapter;
import ru.origami.testit_allure.test_it.testit.services.AdapterManager;
import ru.origami.testit_allure.test_it.testit.services.Utils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static ru.origami.testit_allure.allure.java_commons.util.AspectUtils.getStepName;

@Aspect
public class StepAspect {

    public static final String TEST_IT_ATTACHMENT_TECH_STEP_VALUE = "testit.attachment.tech.step";

    private static final InheritableThreadLocal<AdapterManager> adapterManager
            = new InheritableThreadLocal<AdapterManager>() {
        @Override
        protected AdapterManager initialValue() {
            try {
                return Adapter.getAdapterManager();
            } catch (NoSuchAlgorithmException | KeyManagementException e) {
                throw new RuntimeException(e);
            }
        }
    };

    @Pointcut("@annotation(step)")
    public void withStepAnnotation(final Step step) {
    }

    @Pointcut("execution(* *.*(..))")
    public void anyMethod() {
    }

    @Before("anyMethod() && withStepAnnotation(step)")
    public void startStep(final JoinPoint joinPoint, Step step) {
        if (getManager() != null) {
            final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            final String uuid = UUID.randomUUID().toString();
            Method method = signature.getMethod();

            Parameter[] parameters = method.getParameters();
            Map<String, String> stepParameters = new HashMap<>();

            for (int i = 0; i < parameters.length; i++) {
                final Parameter parameter = parameters[i];

                String name = parameter.getName();
                String value = Objects.isNull(joinPoint.getArgs()[i]) ? "[null]" : joinPoint.getArgs()[i].toString();

                stepParameters.put(name, value);
            }

            String name = getStepName(step.value(), joinPoint);
            String description = Utils.extractDescription(method, stepParameters);

            if (name.equals(TEST_IT_ATTACHMENT_TECH_STEP_VALUE) && !description.equals("")) {
                getManager().updateStep(s -> {
                    if (Objects.isNull(s.getDescription()) || s.getDescription().equals("")) {
                        s.setDescription(Utils.extractDescription(method, stepParameters));
                    }
                });
            } else {
                final StepResult result = new StepResult()
                        .setName(Utils.extractTitle(method, stepParameters, name))
                        .setDescription(description)
                        .setParameters(stepParameters);

                getManager().startStep(uuid, result);
            }
        }
    }

    @AfterReturning(value = "anyMethod() && withStepAnnotation(step)")
    public void finishStep(Step step) {
        if (getManager() != null && !TEST_IT_ATTACHMENT_TECH_STEP_VALUE.equals(step.value())) {
            getManager().updateStep(s -> s.setItemStatus(ItemStatus.PASSED));
            getManager().stopStep();
        }
    }

    @AfterThrowing(value = "anyMethod() && withStepAnnotation(step)", throwing = "throwable")
    public void failedStep(final Throwable throwable, Step step) {
        if (getManager() != null && !TEST_IT_ATTACHMENT_TECH_STEP_VALUE.equals(step.value())) {
            getManager().updateStep(s ->
                    s.setItemStatus(ItemStatus.FAILED)
                            .setThrowable(throwable)
            );
            getManager().stopStep();
        }
    }

    private AdapterManager getManager() {
        return adapterManager.get();
    }
}
