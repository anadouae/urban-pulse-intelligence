package com.urbanpulse.api.web;

import com.urbanpulse.api.domain.Scenario;
import com.urbanpulse.api.service.ScenarioService;
import com.urbanpulse.api.web.dto.ScenarioRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/scenarios")
public class ScenarioController {

    private final ScenarioService scenarioService;

    public ScenarioController(ScenarioService scenarioService) {
        this.scenarioService = scenarioService;
    }

    @GetMapping
    public List<Scenario> list() {
        return scenarioService.list();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Scenario create(@Valid @RequestBody ScenarioRequest request) {
        return scenarioService.create(request);
    }
}
