package com.urbanpulse.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbanpulse.api.domain.SourceKind;
import com.urbanpulse.api.web.dto.UrbanEventRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class EventIngestionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void ingestAndReadTrafficAnalytics() throws Exception {
        UrbanEventRequest speed = new UrbanEventRequest(
                UUID.randomUUID().toString(),
                "traffic.measurement",
                Instant.parse("2026-10-23T12:00:05Z"),
                "sim-generator-1",
                "seg-ave-mid",
                "1.0.0",
                "speed_kmh",
                28.5,
                "km/h",
                "ok",
                SourceKind.SIMULATED);

        mockMvc.perform(post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(speed)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("accepted"));

        mockMvc.perform(get("/api/v1/analytics/traffic/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.spatialEntityId=='seg-ave-mid')].sourceKind").exists());

        mockMvc.perform(get("/api/v1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.milestone").value("M1"))
                .andExpect(jsonPath("$.dataMode").value("SIMULATED_DEMO"));
    }
}
