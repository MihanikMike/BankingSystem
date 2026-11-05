package com.mike.bank.api;

import com.mike.bank.CreateAccountRequest;
import com.mike.bank.DB.DatabaseHelper;
import com.mike.bank.data.TestDataFactory;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.*;


import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

        String accountNumber = TestDataFactory.generateAccountNumber();

        CreateAccountRequest request = TestDataFactory.createAccountRequest(accountNumber, 1500, "Database Test");

        try {
            given()
                    .baseUri("http://localhost")
                    .port(8080)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/accounts")
                    .then()
                    .statusCode(201);

            // 3. Read balance directly from PostgreSQL
            DatabaseHelper db = new DatabaseHelper();

            BigDecimal actualBalance =
                    db.getAccountBalance(accountNumber);

            // 4. Verify database balance
            BigDecimal expectedBalance = new BigDecimal("1500.00");

            assertEquals(
                    0,
                    expectedBalance.compareTo(actualBalance),
                    "Database balance should be 1500.00"
            );

        } finally {

            // 5. Cleanup test data
            given()
                    .baseUri("http://localhost")
                    .port(8080)
                    .when()
                    .delete("/accounts/{accountNumber}", accountNumber)
                    .then()
                    .statusCode(204);
        }
    }
}
