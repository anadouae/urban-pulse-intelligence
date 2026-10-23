package com.urbanpulse.api.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urbanpulse.api.service.EventIngestionService;
import com.urbanpulse.api.web.dto.UrbanEventRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
@ConditionalOnProperty(prefix = "urbanpulse.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
public class TrafficEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(TrafficEventConsumer.class);

    private final ObjectMapper objectMapper;
    private final EventIngestionService ingestionService;
    private final AtomicLong consumed = new AtomicLong();
    private final AtomicLong failed = new AtomicLong();

    public TrafficEventConsumer(ObjectMapper objectMapper, EventIngestionService ingestionService) {
        this.objectMapper = objectMapper;
        this.ingestionService = ingestionService;
    }

    @KafkaListener(topics = "${urbanpulse.kafka.traffic-topic}", groupId = "urban-pulse-api")
    public void onMessage(String payload) {
        try {
            UrbanEventRequest request = objectMapper.readValue(payload, UrbanEventRequest.class);
            ingestionService.ingest(request);
            long n = consumed.incrementAndGet();
            if (n % 25 == 0) {
                log.info("Kafka consumed {} traffic events (failed={})", n, failed.get());
            }
        } catch (Exception ex) {
            failed.incrementAndGet();
            log.warn("Failed to ingest Kafka event: {}", ex.getMessage());
        }
    }

    public long getConsumed() {
        return consumed.get();
    }

    public long getFailed() {
        return failed.get();
    }
}
