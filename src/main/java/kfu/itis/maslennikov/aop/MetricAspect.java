package kfu.itis.maslennikov.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import kfu.itis.maslennikov.repository.inmemory.MetricRepository;

@Aspect
@Component
@RequiredArgsConstructor
public class MetricAspect {

    private final MetricRepository metricRepository;

    @Around("@annotation(Metricable)")
    public Object collectMetrics(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = buildMethodName(joinPoint);
        try {
            Object result = joinPoint.proceed();
            metricRepository.incrementSuccess(methodName);
            return result;
        } catch (Throwable throwable) {
            metricRepository.incrementFailure(methodName);
            throw throwable;
        }
    }

    private String buildMethodName(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getDeclaringType().getSimpleName() + "." + signature.getName();
    }
}