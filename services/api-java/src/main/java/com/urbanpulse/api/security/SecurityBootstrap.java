package com.urbanpulse.api.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class SecurityBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SecurityBootstrap.class);

    private final String apiKey;
    private final boolean required;

    public SecurityBootstrap(
            @Value("${urbanpulse.security.api-key:}") String apiKey,
            @Value("${urbanpulse.security.api-key-required:false}") boolean required) {
        this.apiKey = apiKey == null ? "" : apiKey.trim();
        this.required = required;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (apiKey.isEmpty() && !required) {
            log.warn(
                    "SECURITY: urbanpulse.security.api-key is empty — mutating /api routes are open (local demo). Set URBAN_PULSE_API_KEY for portfolio/prod demos.");
        } else {
            log.info("SECURITY: X-API-Key enforcement enabled for mutating /api routes");
        }
    }
}
