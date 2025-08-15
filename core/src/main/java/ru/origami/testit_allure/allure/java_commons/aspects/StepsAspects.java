package ru.origami.testit_allure.allure.java_commons.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import ru.origami.testit_allure.allure.java_commons.Allure;
import ru.origami.testit_allure.allure.java_commons.AllureLifecycle;
import ru.origami.testit_allure.allure.model.Parameter;
import ru.origami.testit_allure.allure.model.Status;
import ru.origami.testit_allure.allure.model.StepResult;
import ru.origami.testit_allure.annotations.Step;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static ru.origami.testit_allure.allure.java_commons.util.AspectUtils.getStepName;
import static ru.origami.testit_allure.allure.java_commons.util.AspectUtils.getParameters;
import static ru.origami.testit_allure.allure.java_commons.util.ResultsUtils.getStatus;
import static ru.origami.testit_allure.allure.java_commons.util.ResultsUtils.getStatusDetails;
import static ru.origami.testit_allure.test_it.testit.aspects.StepAspect.TEST_IT_ATTACHMENT_TECH_STEP_VALUE;

@Aspect
public class StepsAspects {

    private static final InheritableThreadLocal<AllureLifecycle> LIFECYCLE
            = new InheritableThreadLocal<AllureLifecycle>() {
        @Override
        protected AllureLifecycle initialValue() {
            return Allure.getLifecycle();
        }
    };

    private boolean isTestItAttachTechStep = false;

    private String testItAttachTechPreviousStep = null;

    @Pointcut("@annotation(ru.origami.testit_allure.annotations.Step)")
    public void withStepAnnotation() {
        //pointcut body, should be empty
    }

    @Pointcut("execution(* *(..))")
    public void anyMethod() {
        //pointcut body, should be empty
    }

    @Before("anyMethod() && withStepAnnotation()")
    public void stepStart(final JoinPoint joinPoint) {
        final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        final Step step = methodSignature.getMethod().getAnnotation(Step.class);
        String name = getStepName(step.value(), joinPoint);
        final String uuid = UUID.randomUUID().toString();
        final List<Parameter> parameters = getParameters(methodSignature, joinPoint.getArgs());

        if (!TEST_IT_ATTACHMENT_TECH_STEP_VALUE.equals(name)) {
            final StepResult result = new StepResult()
                    .setName(name)
                    .setParameters(parameters);

            getLifecycle().startStep(uuid, result);
        } else {
            isTestItAttachTechStep = true;
            testItAttachTechPreviousStep = getLifecycle().getCurrentTestCaseOrStep().orElse(null);
        }
    }

    @AfterThrowing(pointcut = "anyMethod() && withStepAnnotation()", throwing = "e")
    public void stepFailed(final Throwable e) {
        Optional<String> name = getLifecycle().getCurrentTestCaseOrStep();

        if (isTestItAttachTechStep && name.isPresent() && name.get().equals(testItAttachTechPreviousStep)) {
            isTestItAttachTechStep = false;
            testItAttachTechPreviousStep = null;
        } else {
            getLifecycle().updateStep(s -> s
                    .setStatus(getStatus(e).orElse(Status.BROKEN))
                    .setStatusDetails(getStatusDetails(e).orElse(null)));
            getLifecycle().stopStep();
        }
    }

    @AfterReturning(pointcut = "anyMethod() && withStepAnnotation()")
    public void stepStop() {
        Optional<String> name = getLifecycle().getCurrentTestCaseOrStep();

        if (isTestItAttachTechStep && name.isPresent() && name.get().equals(testItAttachTechPreviousStep)) {
            isTestItAttachTechStep = false;
            testItAttachTechPreviousStep = null;
        } else {
            getLifecycle().updateStep(s -> s.setStatus(Status.PASSED));
            getLifecycle().stopStep();
        }
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
