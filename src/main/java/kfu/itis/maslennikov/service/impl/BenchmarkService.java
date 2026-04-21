package kfu.itis.maslennikov.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import kfu.itis.maslennikov.repository.inmemory.BenchmarkRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BenchmarkService {

    private final BenchmarkRepository benchmarkRepository;

    public Map<String, BenchmarkStats> getStatistics() {
        Map<String, BenchmarkStats> result = new TreeMap<>();
        benchmarkRepository.getExecutionTimes().forEach((methodName, times) -> result.put(methodName, buildStats(times)));
        return result;
    }

    public PercentileStats getPercentile(String methodName, double percentile) {
        validatePercentile(percentile);
        List<Long> times = benchmarkRepository.getExecutionTimes(methodName);
        if (times.isEmpty()) {
            throw new IllegalArgumentException("Статистика для метода не найдена: " + methodName);
        }
        return new PercentileStats(methodName, percentile, calculatePercentile(times, percentile));
    }

    private BenchmarkStats buildStats(List<Long> times) {
        if (times.isEmpty()) {
            return new BenchmarkStats(0, 0L, 0L, 0.0);
        }
        DoubleSummaryStatistics statistics = times.stream()
                .mapToDouble(Long::doubleValue)
                .summaryStatistics();
        return new BenchmarkStats(
                statistics.getCount(),
                (long) statistics.getMin(),
                (long) statistics.getMax(),
                statistics.getAverage()
        );
    }

    private long calculatePercentile(List<Long> times, double percentile) {
        List<Long> sorted = new ArrayList<>(times);
        sorted.sort(Long::compareTo);
        int index = (int) Math.ceil((percentile / 100.0) * sorted.size()) - 1;
        index = Math.clamp(index, 0, sorted.size() - 1);
        return sorted.get(index);
    }

    private void validatePercentile(double percentile) {
        if (percentile <= 0 || percentile > 100) {
            throw new IllegalArgumentException("Персентиль должен быть в диапазоне (0, 100]");
        }
    }

    public record BenchmarkStats(long invocationCount, long minTimeNanos, long maxTimeNanos, double averageTimeNanos) {
    }

    public record PercentileStats(String methodName, double percentile, long valueNanos) {
    }
}