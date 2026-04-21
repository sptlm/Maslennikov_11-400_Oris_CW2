package kfu.itis.maslennikov.repository.inmemory;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@Repository
public class BenchmarkRepository {

    private final Map<String, Queue<Long>> executionTimes = new ConcurrentHashMap<>();

    public void addExecutionTime(String methodName, long timeInNanos) {
        executionTimes
                .computeIfAbsent(methodName, key -> new ConcurrentLinkedQueue<>())
                .add(timeInNanos);
    }

    public Map<String, List<Long>> getExecutionTimes() {
        return executionTimes.entrySet().stream()
                .collect(Collectors.toUnmodifiableMap(
                        Map.Entry::getKey,
                        entry -> List.copyOf(new ArrayList<>(entry.getValue()))
                ));
    }

    public List<Long> getExecutionTimes(String methodName) {
        Queue<Long> times = executionTimes.get(methodName);
        return times == null ? List.of() : List.copyOf(new ArrayList<>(times));
    }
}