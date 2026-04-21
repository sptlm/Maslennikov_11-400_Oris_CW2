package kfu.itis.maslennikov.aop;

import kfu.itis.maslennikov.service.impl.HelloService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;
import kfu.itis.maslennikov.repository.inmemory.BenchmarkRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BenchmarkAspectTest {

    @Test
    void benchmarkStoresExecutionTime() throws Throwable {
        BenchmarkRepository benchmarkRepository = new BenchmarkRepository();
        BenchmarkAspect benchmarkAspect = new BenchmarkAspect(benchmarkRepository);
        ProceedingJoinPoint joinPoint = mockJoinPoint();
        when(joinPoint.proceed()).thenAnswer(invocation -> {
            Thread.sleep(5L);
            return "ok";
        });

        Object result = benchmarkAspect.benchmark(joinPoint);

        assertEquals("ok", result);
        List<Long> executionTimes = benchmarkRepository.getExecutionTimes("HelloService.sayHello");
        assertEquals(1, executionTimes.size());
        assertFalse(executionTimes.get(0) <= 0);
    }

    private ProceedingJoinPoint mockJoinPoint() {
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        MethodSignature signature = mock(MethodSignature.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getDeclaringType()).thenReturn(HelloService.class);
        when(signature.getName()).thenReturn("sayHello");
        return joinPoint;
    }
}