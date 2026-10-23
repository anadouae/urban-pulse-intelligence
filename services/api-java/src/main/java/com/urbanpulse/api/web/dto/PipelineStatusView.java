package com.urbanpulse.api.web.dto;

public record PipelineStatusView(
        boolean kafkaEnabled,
        String bootstrapServers,
        String trafficTopic,
        long kafkaConsumed,
        long kafkaFailed,
        long eventCount,
        long aggregateCount,
        String mode
) {
}
