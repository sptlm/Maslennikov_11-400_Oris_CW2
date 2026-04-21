package kfu.itis.maslennikov.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class LoggingAspect {

    public static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* kfu.itis.maslennikov..*.*(..)) &&" +
            " !within(kfu.itis.maslennikov.dto..*) &&" +
            " !within(kfu.itis.maslennikov.config..*) &&" +
            "!within(kfu.itis.maslennikov.model.*) $$")
    public void logExecution() {
    }

    @Pointcut("@annotation(Loggable)")
    public void logAnnotated() {
    }

    @Around("logAnnotated()")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        LOGGER.debug("Entering log execution");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();
        LOGGER.info("Start execution {}.{}", className, methodName);
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }

        LOGGER.info("Finish executing {}.{}", className, methodName);
        return result;
    }
}