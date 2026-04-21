package kfu.itis.maslennikov.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import kfu.itis.maslennikov.service.impl.BenchmarkService;

import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BenchmarkController.class)
class BenchmarkControllerTest {


    @MockitoBean
    private BenchmarkService benchmarkService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getBenchmarkStatisticsReturnsJson() throws Exception {
        given(benchmarkService.getStatistics()).willReturn(Map.of(
                "HelloService.sayHello",
                new BenchmarkService.BenchmarkStats(2L, 10L, 25L, 17.5)
        ));

        mockMvc.perform(get("/benchmarks").with(user("Spirit")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['HelloService.sayHello'].invocationCount").value(2))
                .andExpect(jsonPath("$.['HelloService.sayHello'].minTimeNanos").value(10))
                .andExpect(jsonPath("$.['HelloService.sayHello'].maxTimeNanos").value(25))
                .andExpect(jsonPath("$.['HelloService.sayHello'].averageTimeNanos").value(17.5));
    }

    @Test
    void getPercentileReturnsJson() throws Exception {
        given(benchmarkService.getPercentile("HelloService.sayHello", 95))
                .willReturn(new BenchmarkService.PercentileStats("HelloService.sayHello", 95.0, 123L));

        mockMvc.perform(get("/benchmarks/percentile")
                        .with(user("Spirit"))
                        .param("methodName", "HelloService.sayHello")
                        .param("percentile", "95"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.methodName").value("HelloService.sayHello"))
                .andExpect(jsonPath("$.percentile").value(95.0))
                .andExpect(jsonPath("$.valueNanos").value(123));
    }
}