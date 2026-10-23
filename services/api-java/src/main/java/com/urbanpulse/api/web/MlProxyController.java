package com.urbanpulse.api.web;

import com.urbanpulse.api.service.AnalyticsService;
import com.urbanpulse.api.web.dto.HistoryPoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Proxies forecast/anomaly requests to the Python ML service using stored history.
 */
@RestController
@RequestMapping("/api/v1/ml")
public class MlProxyController {

    private final AnalyticsService analyticsService;
    private final RestClient restClient;

    public MlProxyController(
            AnalyticsService analyticsService,
            @Value("${urbanpulse.ml-base-url:http://127.0.0.1:3851}") String mlBaseUrl) {
        this.analyticsService = analyticsService;
        this.restClient = RestClient.builder().baseUrl(mlBaseUrl).build();
    }

    @PostMapping("/forecast/{segmentId}")
    public ResponseEntity<String> forecast(
            @PathVariable String segmentId,
            @RequestParam(defaultValue = "6") int horizon,
            @RequestParam(defaultValue = "ridge") String model) {
        List<HistoryPoint> history = analyticsService.history(segmentId);
        if (history.size() < 3) {
            return ResponseEntity.badRequest().body(
                    "{\"detail\":\"Need more history. Run the ingestion generator for several rounds.\"}");
        }

        Map<String, Object> body = new HashMap<>();
        body.put("segment_id", segmentId);
        body.put("horizon", horizon);
        body.put("model", model);
        body.put("history", history.stream().map(h -> Map.of(
                "t", h.windowStart().getEpochSecond(),
                "value", h.avgSpeedKmh())).toList());

        String response = restClient.post()
                .uri("/v1/forecast")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
    }

    @PostMapping("/anomalies/{segmentId}")
    public ResponseEntity<String> anomalies(@PathVariable String segmentId) {
        List<HistoryPoint> history = analyticsService.history(segmentId);
        if (history.size() < 8) {
            // synthesize denser series from available windows for demo stability
            return ResponseEntity.badRequest().body(
                    "{\"detail\":\"Need >=8 history windows. Increase generator rounds or wait for more aggregates.\"}");
        }

        Map<String, Object> body = new HashMap<>();
        body.put("segment_id", segmentId);
        body.put("history", history.stream().map(h -> Map.of(
                "t", h.windowStart().getEpochSecond(),
                "value", h.avgSpeedKmh())).toList());

        String response = restClient.post()
                .uri("/v1/anomalies")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
    }
}
