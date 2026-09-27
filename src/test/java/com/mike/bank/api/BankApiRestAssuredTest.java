package com.mike.bank.api;

import com.mike.bank.AccountResponse;
import com.mike.bank.AmountRequest;
import com.mike.bank.CreateAccountRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

import java.util.UUID;


public class BankApiRestAssuredTest {

    private static RequestSpecification requestSpec;
    private static BankApiClient bankApiClient;
    private String testAccountNumber;

    @BeforeAll
    static void setup(){

        requestSpec = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(8080)
                .setContentType(ContentType.JSON)
                .build();

        bankApiClient = new BankApiClient(requestSpec);

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @AfterEach
    void cleanup(){

        if(testAccountNumber != null){
            bankApiClient.deleteAccount(testAccountNumber);
        }
    }

    private String generateAccountNumber(){

        return "ACC-" + UUID.randomUUID()
                .toString()
                .substring(0, 8);
    }

    private CreateAccountRequest createAccountRequest(
            String accountNumber,
            double balance,
            String owner) {

        CreateAccountRequest request = new CreateAccountRequest();

        request.setAccountNumber(accountNumber);
        request.setBalance(balance);
        request.setOwner(owner);

        return request;
    }

    private AmountRequest createAmountRequest(double amount){

        AmountRequest request = new AmountRequest();
        request.setAmount(amount);

        return request;
    }

    @Test
    void getAccountsShouldReturn200(){

        Response response = bankApiClient.getAccounts();

        response
            .then()
                .statusCode(200)
                .body("accountNumber",
                        hasItems("ACC1001", "ACC1002"))
                .body("owner",
                        hasItems("Mike", "John"));
    }

    @Test
    void getAccountShouldReturnCorrectAccount(){

        String accountNumber = "ACC1001";

        Response response = bankApiClient.getAccount(accountNumber);

        response
                .then()
                .statusCode(200)
                .body("accountNumber", equalTo(accountNumber))
                .body("owner", equalTo("Mike"))
                .body("balance", equalTo(1000.0f));
    }

    @Test
    void getAccountShouldReturn404WhenAccountDoesNotExist(){

        String accNumber = "ACC9999";
        Response response = bankApiClient.getAccount(accNumber);

        response
                .then()
                .statusCode(404)
                .body("error", equalTo("ACCOUNT_NOT_FOUND"))
                .body("message", equalTo("Account " + accNumber + " was not found"));

    }

    @Test
    void createAccountShouldReturn201AndCreatedAccount(){

        CreateAccountRequest request = createAccountRequest("ACC3001", 1500, "Alex");

        // Create account
        given()
                .spec(requestSpec)
                .body(request)

        .when()
                .post("/accounts")

        .then()
                .statusCode(201)
                .body("accountNumber", equalTo("ACC3001"))
                .body("owner", equalTo("Alex"))
                .body("balance", equalTo(1500.0f));


    }

    @Test
    void createdAccountShouldBeRetrievable(){

        CreateAccountRequest request = createAccountRequest("ACC3002", 2000, "David");

        // Create account
        Response createResponse = bankApiClient.createAccount(request);

        assertEquals(201, createResponse.getStatusCode());

        // Get created account
        Response getResponse = bankApiClient.getAccount("ACC3002");

        getResponse
                .then()
                .statusCode(200)
                .body("accountNumber", equalTo("ACC3002"))
                .body("owner", equalTo("David"))
                .body("balance", equalTo(2000.0f));

    }

    @Test
    void createdAccountShouldBeRetrievableUsingExtract(){

        CreateAccountRequest request =
                new CreateAccountRequest();
        request.setAccountNumber("ACC4001");
        request.setBalance(1800);
        request.setOwner("Chris");

        String createdAccountNumber =
                given()
                        .spec(requestSpec)
                        .body(request)
                .when()
                        .post("/accounts")
                .then()
                        .statusCode(201)
                        .extract()
                        .path("accountNumber");

        given()
                .spec(requestSpec)
                .pathParam("accountNumber", createdAccountNumber)
        .when()
                .get("/accounts/{accountNumber}")
        .then()
                .statusCode(200)
                .body("accountNumber", equalTo(createdAccountNumber))
                .body("owner", equalTo("Chris"))
                .body("balance", equalTo(1800.0f));
    }

    @Test
    void shouldCreateAccountAndReadResponse(){

        CreateAccountRequest request = createAccountRequest("ACC5001", 2200, "Emma");

        Response response = bankApiClient.createAccount(request);

        assertEquals(201, response.getStatusCode());

        AccountResponse account = response.as(AccountResponse.class);

        assertEquals("ACC5001", account.getAccountNumber());
        assertEquals("Emma", account.getOwner());
        assertEquals(2200.0f, account.getBalance());

    }

    @Test
    void shouldDeserializeCreatedAccountResponse(){

        CreateAccountRequest request = createAccountRequest("ACC6001", 3000, "Tom");

        Response response = bankApiClient.createAccount(request);

        assertEquals(201, response.getStatusCode());

        AccountResponse account = response.as(AccountResponse.class);

        assertEquals("ACC6001", account.getAccountNumber());
        assertEquals(3000.0, account.getBalance());
        assertEquals("Tom", account.getOwner());
    }

    @Test
    void deleteAccountShouldReturn204AndAccountShouldDisappear(){

        String accountNumber = generateAccountNumber();

        testAccountNumber = accountNumber;

        CreateAccountRequest request = createAccountRequest(
                testAccountNumber, 1000, "Delete Test");

        Response createResponse =
                bankApiClient.createAccount(request);

        assertEquals(201, createResponse.getStatusCode());

        Response deleteResponse =
                bankApiClient.deleteAccount(testAccountNumber);

        assertEquals(204, deleteResponse.getStatusCode());


        Response getResponse =
                bankApiClient.getAccount(request.getAccountNumber());

        getResponse
                .then()
                .statusCode(404)
                .body("error", equalTo("ACCOUNT_NOT_FOUND"));

        testAccountNumber = null;
    }

    @Test
    void deleteAccountShouldReturn404WhenAccountDoesNotExist(){

        String accountNumber = "ACC9999";

        Response response =
                bankApiClient.deleteAccount(accountNumber);

        response
                .then()
                .statusCode(404)
                .body("error", equalTo("ACCOUNT_NOT_FOUND"))
                .body("message", equalTo("Account "+ accountNumber + " was not found"));
    }

    @Test
    void depositShouldReturn204AndIncreaseBalance(){

        testAccountNumber = generateAccountNumber();

        CreateAccountRequest createRequest =
                createAccountRequest(testAccountNumber, 1000, "Deposit Test");

        Response createResponse = bankApiClient.createAccount(createRequest);

        assertEquals(201, createResponse.getStatusCode());

            AmountRequest depositRequest =
                    createAmountRequest(250);

            Response depositResponse = bankApiClient.deposit(testAccountNumber, depositRequest);

            assertEquals(204, depositResponse.getStatusCode());

            Response getResponse =
                    bankApiClient.getAccount(testAccountNumber);

            getResponse
                    .then()
                    .statusCode(200)
                    .body("balance", equalTo(1250.0f));
    }

    @Test
    void depositShouldReturn400WhenAmountIsNegative(){

        String accountNumber = "ACC1002";

        Response beforeResponse =
                bankApiClient.getAccount(accountNumber);

        float balanceBefore = beforeResponse.jsonPath().getFloat("balance");

        AmountRequest depositRequest =
                createAmountRequest(-100);

        Response depositResponse = bankApiClient.deposit(accountNumber,depositRequest);

        depositResponse
                .then()
                .statusCode(400)
                .body("error", equalTo("INVALID_ARGUMENT"));

        Response afterResponse = bankApiClient.getAccount(accountNumber);


        float balanceAfter = afterResponse.path("balance");

        assertEquals(balanceBefore, balanceAfter);
    }

    @Test
    void withdrawShouldReturn204AndDecreaseBalance(){

        String accountNumber = generateAccountNumber();

        testAccountNumber = accountNumber;

        CreateAccountRequest createRequest =
                createAccountRequest(accountNumber, 1000, "Deposit Test");

        Response createResponse = bankApiClient.createAccount(createRequest);

        assertEquals(201, createResponse.getStatusCode());

        Response beforeResponse =
                bankApiClient.getAccount(accountNumber);

        float balanceBefore = beforeResponse.jsonPath().getFloat("balance");

        AmountRequest withdrawRequest =
                createAmountRequest(100);

        Response withdrawResponse = bankApiClient.withdraw(accountNumber, withdrawRequest);

        assertEquals(204, withdrawResponse.getStatusCode());

        Response afterResponse =
                bankApiClient.getAccount(accountNumber);

        afterResponse
                .then()
                .statusCode(200);

        float balanceAfter = afterResponse.jsonPath().getFloat("balance");

        assertEquals(
                balanceBefore - 100, balanceAfter);
    }

    @Test
    void withdrawShouldReturn400WhenAmountIsNegative(){

        String accountNumber = "ACC1001";

        Response beforeResponse = bankApiClient.getAccount(accountNumber);

        float balanceBefore = beforeResponse.jsonPath().getFloat("balance");

        AmountRequest withdrawRequest =
                createAmountRequest(-100);

        Response withdrawResponse = bankApiClient.withdraw(accountNumber, withdrawRequest);

        withdrawResponse
                .then()
                .statusCode(400)
                .body("error", equalTo("INVALID_ARGUMENT"));

        Response afterResponse = bankApiClient.getAccount(accountNumber);

        float balanceAfter = afterResponse.jsonPath().getFloat("balance");

        assertEquals(balanceBefore, balanceAfter);
    }

    @Test
    void withdrawShouldReturn400WhenBalanceIsInsufficient(){

        String accountNumber = "ACC1002";

        Response beforeResponse = bankApiClient.getAccount(accountNumber);

        float balanceBefore = beforeResponse.jsonPath().getFloat("balance");

        float withdrawAmount = balanceBefore + 100;
        AmountRequest withdrawRequest = createAmountRequest(withdrawAmount);

        Response withdrawResponse = bankApiClient.withdraw(accountNumber, withdrawRequest);

        withdrawResponse
                .then()
                .statusCode(400)
                .body("error", equalTo("INSUFFICIENT_FUNDS"));

        Response afterResponse = bankApiClient.getAccount(accountNumber);

        float balanceAfter = afterResponse.jsonPath().getFloat("balance");

        assertEquals(balanceBefore, balanceAfter);

    }

    @Test
    void depositShouldReturn404WhenAccountDoesNotExist(){

        String accountNumber = "ACC9999";

        AmountRequest depositRequest = createAmountRequest(100);

        Response depositResponse = bankApiClient.deposit(accountNumber, depositRequest);

        depositResponse
                .then()
                .statusCode(404)
                .body("error", equalTo("ACCOUNT_NOT_FOUND"))
                .body("message", equalTo("Account "+accountNumber+" was not found"));
    }

    @Test
    void fullAccountLifecycleShouldWork(){

        String accountNumber = generateAccountNumber();

        testAccountNumber = accountNumber;

        CreateAccountRequest createRequest = createAccountRequest(accountNumber, 1000, "Test Account");

        Response createResponse = bankApiClient.createAccount(createRequest);

        assertEquals(201, createResponse.getStatusCode());

        AmountRequest amountDeposit = createAmountRequest(250);

        Response depositResponse = bankApiClient.deposit(accountNumber, amountDeposit);

        depositResponse
                .then()
                .statusCode(204);

        AmountRequest amountWithdraw = createAmountRequest(100);

        Response withdrawResponse = bankApiClient.withdraw(accountNumber, amountWithdraw);

        withdrawResponse
                .then()
                .statusCode(204);

        Response getResponse = bankApiClient.getAccount(accountNumber);


        getResponse
                .then()
                .statusCode(200)
                .body("balance", equalTo(1150.0f));

        Response deleteResponse = bankApiClient.deleteAccount(accountNumber);

        deleteResponse
                .then()
                .statusCode(204);

        Response deleteAccResponse = bankApiClient.getAccount(accountNumber);

        deleteAccResponse
                .then()
                .statusCode(404)
                .body("error", equalTo("ACCOUNT_NOT_FOUND"));

        testAccountNumber = null;

    }

    @Test
    void accountBalanceShouldRemainCorrectAfterValidAndInvalidTransactions(){

        String accountNumber = generateAccountNumber();

        testAccountNumber = accountNumber;

        CreateAccountRequest createRequest = createAccountRequest(accountNumber, 2000, "Final Test");

        Response createResponse = bankApiClient.createAccount(createRequest);

        assertEquals(201, createResponse.getStatusCode());

        AmountRequest amountDeposit = createAmountRequest(500);

        Response depositResponse = bankApiClient.deposit(accountNumber, amountDeposit);

        depositResponse
                .then()
                .statusCode(204);

        AmountRequest amountNegativeRequest = createAmountRequest(-200);

        Response withdrawNegativeResponse = bankApiClient.withdraw(accountNumber, amountNegativeRequest);

        withdrawNegativeResponse
                .then()
                .statusCode(400)
                .body("error", equalTo("INVALID_ARGUMENT"));

        AmountRequest amountRequest = createAmountRequest(300);

        Response withdrawResponse = bankApiClient.withdraw(accountNumber, amountRequest);

        withdrawResponse
                .then()
                .statusCode(204);

        Response getResponse = bankApiClient.getAccount(accountNumber);

        getResponse
                .then()
                .statusCode(200);

        AccountResponse account = getResponse.as(AccountResponse.class);

        assertEquals(accountNumber, account.getAccountNumber());
        assertEquals("Final Test", account.getOwner());
        assertEquals(2200.0, account.getBalance());

        Response deleteResponse = bankApiClient.deleteAccount(accountNumber);

        deleteResponse
                .then()
                .statusCode(204);

        Response getDeleteAccResponse = bankApiClient.getAccount(accountNumber);

        getDeleteAccResponse
                .then()
                .statusCode(404)
                .body("error", equalTo("ACCOUNT_NOT_FOUND"));

        testAccountNumber = null;
    }


}
