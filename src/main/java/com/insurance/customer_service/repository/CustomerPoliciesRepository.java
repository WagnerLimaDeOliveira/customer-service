package com.insurance.customer_service.repository;

import com.insurance.customer_service.entity.CustomerPolicies;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CustomerPoliciesRepository implements PanacheRepository<CustomerPolicies> {
}
