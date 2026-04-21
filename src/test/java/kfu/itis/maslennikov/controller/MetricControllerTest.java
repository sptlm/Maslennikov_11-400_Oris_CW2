package kfu.itis.maslennikov.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import kfu.itis.maslennikov.service.impl.MetricService;

import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MetricController.class)
class MetricControllerTest {

    @MockitoBean
    private MetricService metricService;


    @Autowired
    private MockMvc mockMvc;

    @Test
    void getMetricStatisticsReturnsJson() throws Exception {
        given(metricService.getStatistics()).willReturn(Map.of(
                "HelloService.sayHello",
                new MetricService.MetricStats(3L, 1L)
        ));

        mockMvc.perform(get("/metrics").with(user("Spirit")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['HelloService.sayHello'].successCount").value(3))
                .andExpect(jsonPath("$.['HelloService.sayHello'].failureCount").value(1));
    }
}