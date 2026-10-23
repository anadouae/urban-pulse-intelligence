package com.urbanpulse.api.web;

import com.urbanpulse.api.service.EnvironmentService;
import com.urbanpulse.api.web.dto.EnvironmentReading;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/environment")
public class EnvironmentController {

    private final EnvironmentService environmentService;

    public EnvironmentController(EnvironmentService environmentService) {
        this.environmentService = environmentService;
    }

    @GetMapping("/latest")
    public List<EnvironmentReading> latest() {
        return environmentService.current();
    }
}
