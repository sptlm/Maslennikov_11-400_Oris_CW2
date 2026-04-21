package kfu.itis.maslennikov.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import kfu.itis.maslennikov.repository.inmemory.MetricRepository;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
public class MetricService {

    private final MetricRepository metricRepository;

    public Map<String, MetricStats> getStatistics() {
        Map<String, Long> successStatistics = metricRepository.getSuccessStatistics();
        Map<String, Long> failureStatistics = metricRepository.getFailureStatistics();

        Set<String> methodNames = new TreeSet<>();
        methodNames.addAll(successStatistics.keySet());
        methodNames.addAll(failureStatistics.keySet());

        Map<String, MetricStats> result = new TreeMap<>();
        for (String methodName : methodNames) {
            long successCount = successStatistics.getOrDefault(methodName, 0L);
            long failureCount = failureStatistics.getOrDefault(methodName, 0L);
            result.put(methodName, new MetricStats(successCount, failureCount));
        }
        return result;
    }

    public record MetricStats(long successCount, long failureCount) {
    }
}