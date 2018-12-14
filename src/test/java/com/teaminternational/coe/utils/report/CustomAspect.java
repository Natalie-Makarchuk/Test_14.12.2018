package com.teaminternational.coe.utils.report;

import com.teaminternational.coe.utils.CommonHelper;
import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.Status;
import io.qameta.allure.model.StepResult;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.qameta.allure.util.ResultsUtils.getStatus;
import static io.qameta.allure.util.ResultsUtils.getStatusDetails;

/**
 * Aspect class. Custom implementation of aspect that creates Allure lifecycle and write steps with details into report for each execution of any method from pages and tests packages
 *
 * @author Vladyslav.Yegorov@teaminternational.com
 * @version 1.0 01/10/18
 *
 * @see AllureLifecycle
 * @see Aspect
 * @see Pointcut
 * @see Around
 */
@SuppressWarnings("unused")
@Aspect
public class CustomAspect {

    /**
     * Allure context variable
     */
    private static AllureLifecycle lifecycle;

    /**
     * Create a new AllureLifecycle instance if it's not created before and store in private static variable
     *
     * @return current saved or created AllureLifecycle
     */
    public static AllureLifecycle getLifecycle() {
        if (Objects.isNull(lifecycle)) {
            lifecycle = Allure.getLifecycle();
        }
        return lifecycle;
    }

    /**
     * Create a new AllureLifecycle instance if it's not created before and store in private static variable
     *
     * @param array Array tham must be converted to string
     *
     * @return current saved or created AllureLifecycle
     */
    private static String arrayToString(final Object... array) {
        return Stream.of(array)
                .map(object -> {
                    if (object.getClass().isArray()) {
                        return arrayToString((Object[]) object);
                    }
                    return Objects.toString(object);
                })
                .collect(Collectors.joining(", "));
    }

    /**
     * Defines pointcut for aspect.
     * Specify to implementation of aspect for execution of any method in package com.teaminternational.coe.pages
     *
     */
    @Pointcut("execution(* com.teaminternational.coe.pages.*.*(..))")
    public void anyPageMethod() {
        //pointcut body, should be empty
    }

    /**
     * Defines pointcut for aspect.
     * Specify to implementation of aspect for execution of any method in package com.teaminternational.coe.tests
     *
     */
    @Pointcut("execution(* com.teaminternational.coe.tests.*.*(..))")
    public void anyTestMethod() {
        //pointcut body, should be empty
    }

    /**
     * Defines pointcut for aspect.
     * Specify to implementation of aspect for execution of any method in package com.teaminternational.coe.tests.example
     *
     */
    @Pointcut("execution(* com.teaminternational.coe.tests.example.*.*(..))")
    public void anyTestExampleMethod() {
        //pointcut body, should be empty
    }

    /**
     * Describes implementation of aspect method instead of any method in pages and tests packages.
     * Starts Allure step with name of the executed method, make execution of method, write success or broken status to allure step, and stop step in the end.
     *
     * @param joinPoint method's pointer which replaced by aspect's method step
     *
     * @return result of execution method that was replaced by aspect's method step
     *
     * @throws Throwable in case of error
     */
    @Around("anyPageMethod() || anyTestMethod() || anyTestExampleMethod()")
    public Object step(ProceedingJoinPoint joinPoint) throws Throwable {
        CommonHelper commonHelper = CommonHelper.getInstance();
        final MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        final String name = joinPoint.getArgs().length > 0
                ? String.format("%s (%s)", methodSignature.getName(), arrayToString(joinPoint.getArgs())) // -> (1)
                : methodSignature.getName();
        final String uuid = UUID.randomUUID().toString();
        final StepResult result = new StepResult()
                .withName(name);
        getLifecycle().startStep(uuid, result);
        try {
            final Object proceed = joinPoint.proceed();
            getLifecycle().updateStep(uuid, s -> s.withStatus(Status.PASSED));
            return proceed;
        } catch (Throwable e) {
            getLifecycle().updateStep(uuid, s -> s
                    .withStatus(getStatus(e).orElse(Status.BROKEN))
                    .withStatusDetails(getStatusDetails(e).orElse(null)));
            getLifecycle().addAttachment("Screenshot", "image/png", "png", commonHelper.getScreenshotBytes());
            getLifecycle().addAttachment("Page source", "text/html", "html", commonHelper.getPageSourceBytes());
            throw e;
        } finally {
            getLifecycle().stopStep(uuid);
        }

    }

}