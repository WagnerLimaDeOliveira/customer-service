package com.insurance.customer_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.customer_service.TestHelper.TestContainerSetupHelper;
import com.insurance.customer_service.dto.CustomerPolicy;
import com.insurance.customer_service.entity.Customer;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@QuarkusTest
public class CustomerServiceTest {
    @Inject
    EntityManager entityManager;

    @Inject
    CustomerService customerService;

    @Inject
    ObjectMapper objectMapper;

    private static final Instant DATE_OF_BIRTH = Instant.now();
    private static final Instant REGISTRATION_DATE = Instant.now();

    @BeforeAll
    public static void setUp() {
        TestContainerSetupHelper.startContainers();
        System.setProperty("quarkus.datasource.jdbc.url", TestContainerSetupHelper.getPostgresJdbcUrl());
        System.setProperty("quarkus.datasource.username", TestContainerSetupHelper.getPostgresUsername());
        System.setProperty("quarkus.datasource.password", TestContainerSetupHelper.getPostgresPassword());
    }

    @BeforeEach
    @Transactional
    public void cleanDatabase() {
        entityManager.createNativeQuery("TRUNCATE TABLE customer RESTART IDENTITY CASCADE").executeUpdate();
    }

    @AfterAll
    public static void tearDown() {
        TestContainerSetupHelper.stopContainers();
    }

    @Test
    public void createCustomer_shouldCreateAndReturnCustomer() {
        Customer customer = new Customer();
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john@doe.com");
        customer.setAddress("Johnstreet 1");
        customer.setPhoneNumber("4545451-555");
        customer.setDateOfBirth(DATE_OF_BIRTH);
        customer.setRegistrationDate(REGISTRATION_DATE);
        Customer createdCustomer = customerService.createCustomer(customer);

        assertNotNull(createdCustomer.getId(), "Customer ID should not be null after creation");
        assertEquals("John", createdCustomer.getFirstName(), "First name should match");
        assertEquals("Doe", createdCustomer.getLastName(), "Last name should match");
        assertEquals("john@doe.com", createdCustomer.getEmail(), "Email should match");
        assertEquals("Johnstreet 1", createdCustomer.getAddress(), "Address should match");
        assertEquals("4545451-555", createdCustomer.getPhoneNumber(), "Phone number should match");
        assertEquals(DATE_OF_BIRTH, createdCustomer.getDateOfBirth(), "Date of birth should match");
        assertEquals(REGISTRATION_DATE, createdCustomer.getRegistrationDate(), "Registration date should match");
    }

    @Test
    public void getCustomerByPolicyNumber_shouldReturnCustomer() throws JsonProcessingException {
        Customer customer = new Customer();
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john@doe.com");
        customer.setAddress("Johnstreet 1");
        customer.setPhoneNumber("4545451-555");
        customer.setDateOfBirth(DATE_OF_BIRTH);
        customer.setRegistrationDate(REGISTRATION_DATE);
        Customer createdCustomer = customerService.createCustomer(customer);

        UUID policyNumber = UUID.randomUUID();
        CustomerPolicy customerPolicy = new CustomerPolicy(createdCustomer.getId(), policyNumber);
        String requestedCustomerPolicy = objectMapper.writeValueAsString(customerPolicy);
        customerService.updateCustomerPolicy(requestedCustomerPolicy);

        Customer requestedCustomer = customerService.getCustomerByPolicyNumber(policyNumber);
        assertEquals(requestedCustomer.getId(), createdCustomer.getId());
    }
}
