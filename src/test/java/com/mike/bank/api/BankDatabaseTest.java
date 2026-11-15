package com.mike.bank.api;

import com.mike.bank.CreateAccountRequest;
import com.mike.bank.DB.DatabaseHelper;
import com.mike.bank.data.TestDataFactory;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.*;


import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

public class BankDatabaseTest {

    @Test
    void shouldConnectToPostgres() throws SQLException {

        String url = "jdbc:postgresql://localhost:5432/banking_db";
        String user = "banking_user";
        String password = System.getenv().getOrDefault("DB_PASSWORD", "banking_dev_password");

        try(Connection connection =
                DriverManager.getConnection(url, user, password)){

            assertTrue(connection.isValid(2));
        }
    }

    @Test
    void shouldReadAccountFromDatabase() throws SQLException {

        RequestSpecification anonymousSpec = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(8080)
                .setContentType(ContentType.JSON)
                .build();

        String adminPassword = System.getenv("BANK_ADMIN_PASSWORD");
        assertNotNull(adminPassword, "BANK_ADMIN_PASSWORD is not set");
        assertFalse(adminPassword.isBlank(), "BANK_ADMIN_PASSWORD is blank");

        String adminToken = AuthHelper.loginAndGetToken(
                anonymousSpec, "admin", adminPassword
        );

        RequestSpecification adminSpec = new RequestSpecBuilder()
                .addRequestSpecification(anonymousSpec)
                .addHeader("Authorization", "Bearer " + adminToken)
                .build();

        BankApiClient client = new BankApiClient(adminSpec);

        String accountNumber = TestDataFactory.generateAccountNumber();

        CreateAccountRequest request = TestDataFactory.createAccountRequest(
                accountNumber, 1500, "Database Test"
        );

        client.createAccount(request)
                .then()
                .statusCode(201);

        try {
            DatabaseHelper db = new DatabaseHelper();
            BigDecimal actualBalance = db.getAccountBalance(accountNumber);

            assertEquals(
                    0,
                    new BigDecimal("1500.00").compareTo(actualBalance),
                    "Database balance should be 1500.00"
            );
        } finally {
            client.deleteAccount(accountNumber)
                    .then()
                    .statusCode(204);
        }
    }
}
