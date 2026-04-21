package kfu.itis.maslennikov.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import kfu.itis.maslennikov.service.impl.MetricService;

import java.util.Map;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class MetricController {

    private final MetricService metricService;

    @GetMapping("/metrics")
    public Map<String, MetricService.MetricStats> getMetricStatistics() {
        return metricService.getStatistics();
    }
}