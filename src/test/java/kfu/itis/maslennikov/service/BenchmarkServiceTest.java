package kfu.itis.maslennikov.service;

import kfu.itis.maslennikov.service.impl.BenchmarkService;
import org.junit.jupiter.api.Test;
import kfu.itis.maslennikov.repository.inmemory.BenchmarkRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BenchmarkServiceTest {

    @Test
    void getStatisticsReturnsSummary() {
        BenchmarkRepository benchmarkRepository = new BenchmarkRepository();
        benchmarkRepository.addExecutionTime("HelloService.sayHello", 10L);
        benchmarkRepository.addExecutionTime("HelloService.sayHello", 20L);
        benchmarkRepository.addExecutionTime("HelloService.sayHello", 30L);
        BenchmarkService benchmarkService = new BenchmarkService(benchmarkRepository);

        BenchmarkService.BenchmarkStats stats = benchmarkService.getStatistics().get("HelloService.sayHello");

        assertEquals(3L, stats.invocationCount());
        assertEquals(10L, stats.minTimeNanos());
        assertEquals(30L, stats.maxTimeNanos());
        assertEquals(20.0, stats.averageTimeNanos());
    }

    @Test
    void getPercentileReturnsRequestedPercentile() {
        BenchmarkRepository benchmarkRepository = new BenchmarkRepository();
        benchmarkRepository.addExecutionTime("HelloService.sayHello", 10L);
        benchmarkRepository.addExecutionTime("HelloService.sayHello", 20L);
        benchmarkRepository.addExecutionTime("HelloService.sayHello", 30L);
        benchmarkRepository.addExecutionTime("HelloService.sayHello", 40L);
        BenchmarkService benchmarkService = new BenchmarkService(benchmarkRepository);

        BenchmarkService.PercentileStats percentile = benchmarkService.getPercentile("HelloService.sayHello", 75);

        assertEquals("HelloService.sayHello", percentile.methodName());
        assertEquals(75.0, percentile.percentile());
        assertEquals(30L, percentile.valueNanos());
    }

    @Test
    void getPercentileRejectsInvalidValue() {
        BenchmarkService benchmarkService = new BenchmarkService(new BenchmarkRepository());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> benchmarkService.getPercentile("HelloService.sayHello", 0)
        );

        assertEquals("Персентиль должен быть в диапазоне (0, 100]", exception.getMessage());
    }
}