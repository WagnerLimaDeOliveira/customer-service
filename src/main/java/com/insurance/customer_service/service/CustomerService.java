package com.insurance.customer_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.customer_service.dto.CustomerPolicy;
import com.insurance.customer_service.entity.Customer;
import com.insurance.customer_service.entity.CustomerPolicies;
import com.insurance.customer_service.repository.CustomerPoliciesRepository;
import com.insurance.customer_service.repository.CustomerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import java.util.UUID;

@ApplicationScoped
public class CustomerService {
    @Inject
    CustomerRepository customerRepository;

    @Inject
    CustomerPoliciesRepository customerPoliciesRepository;

    @Inject
    ObjectMapper objectMapper;

    @Inject
    @Channel("deleted-customer-events")
    Emitter<String> deletedCustomerEmitter;

    public Customer getCustomerById(Long customerId) {
        return customerRepository.findById(customerId);
    }

    @Transactional
    public Customer createCustomer(Customer customer) {
        if (customer != null && customer.getId() == null) {
            customerRepository.persist(customer);
            return customer;
        } else {
            return null;
        }
    }

    public Customer getCustomerByPolicyNumber(UUID policyNumber) {
        CustomerPolicies customerPolicy = customerPoliciesRepository.find("policyNumber", policyNumber).firstResult();
        if (customerPolicy == null) {
            return null;
        } else {
            return customerRepository.findById(customerPolicy.getCustomerId());
        }
    }

    @Transactional
    public Customer updateCustomer(Customer customer) {
        if (customer != null && customer.getId() != null) {
            customerRepository.persist(customer);
            return customer;
        } else {
            return null;
        }
    }

    @Transactional
    @Incoming("generated-policy")
    public void updateCustomerPolicy(String customerPolicy) throws JsonProcessingException {
        CustomerPolicy customerPolicyDTO = objectMapper.readValue(customerPolicy, CustomerPolicy.class);
        CustomerPolicies customerPolicies = new CustomerPolicies();
        customerPolicies.setCustomerId(customerPolicyDTO.customerId());
        customerPolicies.setPolicyNumber(customerPolicyDTO.policyNumber());
        customerPoliciesRepository.persist(customerPolicies);
    }

    /*TODO: Create a uniform naming pattern for all channels*/
    @Incoming("deleted-policy-events")
    @Transactional
    public void deleteCustomerPolicy(String customerPolicy) throws JsonProcessingException {
        CustomerPolicy customerPolicyDTO = objectMapper.readValue(customerPolicy, CustomerPolicy.class);
        customerPoliciesRepository.delete("customerId =?1 and policyNumber =?2", customerPolicyDTO.customerId(), customerPolicyDTO.policyNumber());
    }

    /*TODO: Create Action DTO? So All delete events can be send on the same topic?*/
    @Transactional
    public boolean deleteCustomerById(Long customerId) {
        boolean isCustomerDeleted = customerRepository.deleteById(customerId);
        deletedCustomerEmitter.send(customerId.toString());
        return isCustomerDeleted;
    }

}
