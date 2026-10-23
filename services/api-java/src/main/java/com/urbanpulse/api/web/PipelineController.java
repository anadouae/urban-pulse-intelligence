package com.urbanpulse.api.web;

import com.urbanpulse.api.kafka.TrafficEventConsumer;
import com.urbanpulse.api.repo.TrafficAggregateRepository;
import com.urbanpulse.api.repo.UrbanEventRepository;
import com.urbanpulse.api.web.dto.PipelineStatusView;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pipeline")
public class PipelineController {

    private final UrbanEventRepository eventRepository;
    private final TrafficAggregateRepository aggregateRepository;
    private final ObjectProvider<TrafficEventConsumer> consumerProvider;

    @Value("${urbanpulse.kafka.enabled:true}")
    private boolean kafkaEnabled;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${urbanpulse.kafka.traffic-topic}")
    private String trafficTopic;

    public PipelineController(
            UrbanEventRepository eventRepository,
            TrafficAggregateRepository aggregateRepository,
            ObjectProvider<TrafficEventConsumer> consumerProvider) {
        this.eventRepository = eventRepository;
        this.aggregateRepository = aggregateRepository;
        this.consumerProvider = consumerProvider;
    }

    @GetMapping("/status")
    public PipelineStatusView status() {
        TrafficEventConsumer consumer = consumerProvider.getIfAvailable();
        return new PipelineStatusView(
                kafkaEnabled,
                bootstrapServers,
                trafficTopic,
                consumer == null ? 0 : consumer.getConsumed(),
                consumer == null ? 0 : consumer.getFailed(),
                eventRepository.count(),
                aggregateRepository.count(),
                kafkaEnabled ? "KAFKA_STREAMING" : "HTTP_ONLY");
    }
}
