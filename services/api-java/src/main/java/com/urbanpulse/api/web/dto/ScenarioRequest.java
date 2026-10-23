package com.urbanpulse.api.web.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ScenarioRequest(
        @NotBlank String name,
        @NotBlank String assumptions,
        @NotNull @DecimalMin("0.2") @DecimalMax("3.0") Double demandMultiplier,
        @NotNull @DecimalMin("0.2") @DecimalMax("3.0") Double capacityMultiplier,
        @NotNull @DecimalMin("0") @DecimalMax("240") Double incidentMinutes
) {
}
