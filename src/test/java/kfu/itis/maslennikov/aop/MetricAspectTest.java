package kfu.itis.maslennikov.aop;

import kfu.itis.maslennikov.service.impl.HelloService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;
import kfu.itis.maslennikov.repository.inmemory.MetricRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class MetricAspectTest {

    @Test
    void collectMetricsIncrementsSuccessCounter() throws Throwable {
        MetricRepository metricRepository = new MetricRepository();
        MetricAspect metricAspect = new MetricAspect(metricRepository);
        ProceedingJoinPoint joinPoint = mockJoinPoint();
        when(joinPoint.proceed()).thenReturn("ok");

        Object result = metricAspect.collectMetrics(joinPoint);

        assertEquals("ok", result);
        assertEquals(1L, metricRepository.getSuccessStatistics().get("HelloService.sayHello"));
        assertEquals(0L, metricRepository.getFailureStatistics().getOrDefault("HelloService.sayHello", 0L));
    }

    @Test
    void collectMetricsIncrementsFailureCounter() throws Throwable {
        MetricRepository metricRepository = new MetricRepository();
        MetricAspect metricAspect = new MetricAspect(metricRepository);
        ProceedingJoinPoint joinPoint = mockJoinPoint();
        when(joinPoint.proceed()).thenThrow(new IllegalArgumentException("boom"));

        assertThrows(IllegalArgumentException.class, () -> metricAspect.collectMetrics(joinPoint));
        assertEquals(1L, metricRepository.getFailureStatistics().get("HelloService.sayHello"));
        assertEquals(0L, metricRepository.getSuccessStatistics().getOrDefault("HelloService.sayHello", 0L));
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