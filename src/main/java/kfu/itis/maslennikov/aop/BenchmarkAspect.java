package kfu.itis.maslennikov.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import kfu.itis.maslennikov.repository.inmemory.BenchmarkRepository;

@Aspect
@Component
@RequiredArgsConstructor
public class BenchmarkAspect {

    private final BenchmarkRepository benchmarkRepository;

    @Around("@annotation(Benchmarkable)")
    public Object benchmark(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = buildMethodName(joinPoint);
        long startTime = System.nanoTime();
        try {
            return joinPoint.proceed();
        } finally {
            benchmarkRepository.addExecutionTime(methodName, System.nanoTime() - startTime);
        }
    }

    private String buildMethodName(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getDeclaringType().getSimpleName() + "." + signature.getName();
    }
}