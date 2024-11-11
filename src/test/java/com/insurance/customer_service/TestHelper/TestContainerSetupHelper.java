package com.insurance.customer_service.TestHelper;

import org.testcontainers.containers.PostgreSQLContainer;

public class TestContainerSetupHelper {

    private static PostgreSQLContainer<?> postgresContainer;


    public static void startContainers() {
        if (postgresContainer == null) {
            postgresContainer = new PostgreSQLContainer<>("postgres:latest")
                    .withDatabaseName("claim_db")
                    .withUsername("user")
                    .withPassword("password");
            postgresContainer.start();
        }
    }

    public static void stopContainers() {
        if (postgresContainer != null) {
            postgresContainer.stop();
        }
    }

    public static String getPostgresJdbcUrl() {
        return postgresContainer.getJdbcUrl();
    }

    public static String getPostgresUsername() {
        return postgresContainer.getUsername();
    }

    public static String getPostgresPassword() {
        return postgresContainer.getPassword();
    }
}
