package com.urbanpulse.api.domain;

/**
 * Provenance label — never present simulated or predicted values as observed facts.
 */
public enum SourceKind {
    SIMULATED,
    OBSERVED,
    PREDICTED
}
