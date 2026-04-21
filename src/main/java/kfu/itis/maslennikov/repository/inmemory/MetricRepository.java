package kfu.itis.maslennikov.repository.inmemory;

import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class MetricRepository {

    private final ConcurrentHashMap<String, AtomicLong> metricsSuccess = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> metricsFailure = new ConcurrentHashMap<>();

    public void incrementSuccess(String methodName) {
        metricsSuccess.computeIfAbsent(methodName, key -> new AtomicLong()).incrementAndGet();
    }

    public void incrementFailure(String methodName) {
        metricsFailure.computeIfAbsent(methodName, key -> new AtomicLong()).incrementAndGet();
    }

    public Map<String, Long> getSuccessStatistics() {
        return snapshot(metricsSuccess);
    }

    public Map<String, Long> getFailureStatistics() {
        return snapshot(metricsFailure);
    }

    private Map<String, Long> snapshot(ConcurrentHashMap<String, AtomicLong> source) {
        return source.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().get()
                ));
    }
}