package com.insurance.customer_service.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
public class CustomerPolicies extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "customer_id", nullable = false)
    Long customerId;

    @Column(name = "policy_number", nullable = false)
    UUID policyNumber;

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public UUID getPolicyNumber() {
        return policyNumber;
    }

    public void setPolicyNumber(UUID policyNumber) {
        this.policyNumber = policyNumber;
    }
}
