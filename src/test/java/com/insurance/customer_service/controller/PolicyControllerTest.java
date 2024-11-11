package com.insurance.customer_service.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.customer_service.TestHelper.TestContainerSetupHelper;
import com.insurance.customer_service.dto.CustomerPolicy;
import com.insurance.customer_service.entity.Customer;
import com.insurance.customer_service.service.CustomerService;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class PolicyControllerTest {
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
        entityManager.createNativeQuery("TRUNCATE TABLE customerpolicies RESTART IDENTITY CASCADE").executeUpdate();
    }

    @AfterAll
    public static void tearDown() {
        TestContainerSetupHelper.stopContainers();
    }


    @Test
    public void getCustomerByPolicyId_shouldCreateAndReturnCustomer() throws JsonProcessingException {
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

       Customer requestedCustomer =  given().contentType(ContentType.JSON)
                .when()
                .get("/policy/" + policyNumber)
                .then()
                .statusCode(200)
                .body("firstName", equalTo(customer.getFirstName()))
                .body("lastName", equalTo(customer.getLastName()))
                .body("email", equalTo(customer.getEmail()))
                .body("address", equalTo(customer.getAddress()))
                .body("phoneNumber", equalTo(customer.getPhoneNumber()))
                .body("id", equalTo(createdCustomer.getId().intValue())).extract().as(Customer.class);

       assertEquals(requestedCustomer.getDateOfBirth().truncatedTo(ChronoUnit.SECONDS), createdCustomer.getDateOfBirth().truncatedTo(ChronoUnit.SECONDS));
       assertEquals(requestedCustomer.getRegistrationDate().truncatedTo(ChronoUnit.SECONDS), createdCustomer.getRegistrationDate().truncatedTo(ChronoUnit.SECONDS));
    }

}
