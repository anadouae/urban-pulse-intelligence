package com.urbanpulse.api.web;

import com.urbanpulse.api.domain.SpatialEntity;
import com.urbanpulse.api.service.AnalyticsService;
import com.urbanpulse.api.web.dto.SystemStatusView;
import com.urbanpulse.api.web.dto.TrafficSegmentView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/status")
    public SystemStatusView status() {
        return analyticsService.status();
    }

    @GetMapping("/city/entities")
    public List<SpatialEntity> entities() {
        return analyticsService.listEntities();
    }

    @GetMapping("/analytics/traffic/latest")
    public List<TrafficSegmentView> latestTraffic() {
        return analyticsService.latestTrafficBySegment();
    }
}
