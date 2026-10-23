package com.urbanpulse.api.web;

import com.urbanpulse.api.domain.UrbanEvent;
import com.urbanpulse.api.service.EventIngestionService;
import com.urbanpulse.api.web.dto.UrbanEventRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/events")
public class EventController {

    private final EventIngestionService ingestionService;

    public EventController(EventIngestionService ingestionService) {
        this.ingestionService = ingestionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> ingest(@Valid @RequestBody UrbanEventRequest request) {
        UrbanEvent saved = ingestionService.ingest(request);
        return Map.of(
                "eventId", saved.getEventId(),
                "spatialEntityId", saved.getSpatialEntityId(),
                "sourceKind", saved.getSourceKind().name(),
                "status", "accepted");
    }
}
