package com.insurance.customer_service.controller;

import com.insurance.customer_service.TestHelper.TestContainerSetupHelper;
import com.insurance.customer_service.entity.Customer;
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

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class CustomerControllerTest {
    @Inject
    EntityManager entityManager;

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
    public void createCustomer_shouldCreateAndReturnCustomer() {
        Customer customer = new Customer();
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setEmail("john@doe.com");
        customer.setAddress("Johnstreet 1");
        customer.setPhoneNumber("4545451-555");
        customer.setDateOfBirth(DATE_OF_BIRTH);
        customer.setRegistrationDate(REGISTRATION_DATE);

        given().contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(customer)
                .when()
                .post("/customer")
                .then()
                .statusCode(200)
                .body("firstName", equalTo("John"))
                .body("lastName", equalTo("Doe"))
                .body("email", equalTo("john@doe.com"))
                .body("address", equalTo("Johnstreet 1"))
                .body("phoneNumber", equalTo("4545451-555"))
                .body("dateOfBirth", equalTo(DATE_OF_BIRTH.toString()))
                .body("registrationDate", equalTo(REGISTRATION_DATE.toString()))
                .body("id", notNullValue());
    }

}
