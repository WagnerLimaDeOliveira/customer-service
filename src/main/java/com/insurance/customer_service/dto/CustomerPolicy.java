package com.insurance.customer_service.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record CustomerPolicy(
        @JsonProperty("customerId") long customerId,
        @JsonProperty("policyNumber") UUID policyNumber
) {
    @JsonCreator
    public CustomerPolicy {
    }
}