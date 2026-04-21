package kfu.itis.maslennikov.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import kfu.itis.maslennikov.service.impl.BenchmarkService;

import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class BenchmarkController {

    private final BenchmarkService benchmarkService;

    @GetMapping("/benchmarks")
    public Map<String, BenchmarkService.BenchmarkStats> getBenchmarkStatistics() {
        return benchmarkService.getStatistics();
    }

    @GetMapping("/benchmarks/percentile")
    public BenchmarkService.PercentileStats getPercentile(
            @RequestParam String methodName,
            @RequestParam double percentile
    ) {
        return benchmarkService.getPercentile(methodName, percentile);
    }
}