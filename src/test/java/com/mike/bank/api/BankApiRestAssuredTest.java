package com.mike.bank.api;

import com.mike.bank.AccountResponse;
import com.mike.bank.AmountRequest;
import com.mike.bank.CreateAccountRequest;
import com.mike.bank.TransferRequest;
import com.mike.bank.data.TestDataFactory;
import io.restassured.config.JsonConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.path.json.config.JsonPathConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class BankApiRestAssuredTest {

    private static RequestSpecification requestSpec;
    private static BankApiClient bankApiClient;
    private final List<String> testAccountNumbers = new ArrayList<>();

    @BeforeAll
    static void setup(){

        requestSpec = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(8080)
                .setContentType(ContentType.JSON)
                .setConfig(RestAssuredConfig.config()
                        .jsonConfig(JsonConfig.jsonConfig()
                                .numberReturnType(
                                        JsonPathConfig.NumberReturnType.BIG_DECIMAL
                                )))
                .build();

        bankApiClient = new BankApiClient(requestSpec);

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @AfterEach
    void cleanup(){

        try {
            for(String accountNumber : testAccountNumbers){
                bankApiClient.deleteAccount(accountNumber)
                        .then()
                        .statusCode(204);

                bankApiClient.getAccount(accountNumber)
                        .then()
                        .statusCode(404);
            }
        }finally{
            testAccountNumbers.clear();
        }
    }

    private String createTestAccount(double balance, String owner){

        String accountNumber = TestDataFactory.generateAccountNumber();

        CreateAccountRequest request = TestDataFactory.createAccountRequest(accountNumber, balance, owner);

        Response createResponse = bankApiClient.createAccount(request);

        String createdAccountNumber = createResponse
                        .then()
                        .statusCode(201)
                        .extract()
                        .path("accountNumber");

        testAccountNumbers.add(createdAccountNumber);

        return createdAccountNumber;

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
                .body("owner", equalTo("Mike"));

        BigDecimal actualBalance = response.jsonPath().getObject("balance", BigDecimal.class);

        BigDecimal expectedBalance = new BigDecimal("1200.00");

        assertEquals(0, expectedBalance.compareTo(actualBalance));
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

        CreateAccountRequest request = TestDataFactory.createAccountRequest("ACC1005", 1500, "Alex");

        // Create account
        Response createResponse = bankApiClient.createAccount(request);

        createResponse.then()
                .statusCode(201)
                .body("accountNumber", equalTo("ACC1005"))
                .body("owner", equalTo("Alex"));

        BigDecimal actualBalance = createResponse.jsonPath().getObject("balance", BigDecimal.class);

        BigDecimal expectedBalance = new BigDecimal("1500");

        assertEquals(0, expectedBalance.compareTo(actualBalance));

    }

    @Test
    void createdAccountShouldBeRetrievable(){

        CreateAccountRequest request = TestDataFactory.createAccountRequest("ACC3002", 2000, "David");

        // Create account
        Response createResponse = bankApiClient.createAccount(request);

        assertEquals(201, createResponse.getStatusCode());

        // Get created account
        Response getResponse = bankApiClient.getAccount("ACC3002");

        getResponse
                .then()
                .statusCode(200)
                .body("accountNumber", equalTo("ACC3002"))
                .body("owner", equalTo("David"));

        BigDecimal actualBalance = getResponse.jsonPath().getObject("balance", BigDecimal.class);

        BigDecimal expectedBalance = new BigDecimal("2000");

        assertEquals(0, expectedBalance.compareTo(actualBalance));

    }

    @Test
    void createdAccountShouldBeRetrievableUsingExtract(){

        String accountNumber = TestDataFactory.generateAccountNumber();

        CreateAccountRequest request = TestDataFactory.createAccountRequest(accountNumber, 1800, "Chris");

        Response createResponse = bankApiClient.createAccount(request);

        createResponse.then().statusCode(201);

        testAccountNumbers.add(accountNumber);

        String createdAccountNumber = createResponse
                .then()
                .statusCode(201)
                .extract()
                .path("accountNumber");

        Response getResponse = bankApiClient.getAccount(accountNumber);

        getResponse
                .then()
                .statusCode(200)
                .body("accountNumber", equalTo(accountNumber))
                .body("owner", equalTo("Chris"));

        BigDecimal actualBalance = getResponse.jsonPath().getObject("balance", BigDecimal.class);

        BigDecimal expecterBalance = new BigDecimal("1800");

        assertEquals(0, expecterBalance.compareTo(actualBalance));

    }

    @Test
    void shouldCreateAccountAndReadResponse(){

        CreateAccountRequest request = TestDataFactory.createAccountRequest("ACC5001", 2200, "Emma");

        Response response = bankApiClient.createAccount(request);

        assertEquals(201, response.getStatusCode());

        AccountResponse account = response.as(AccountResponse.class);

        assertEquals("ACC5001", account.getAccountNumber());
        assertEquals("Emma", account.getOwner());
        assertEquals(2200.0f, account.getBalance());

    }

    @Test
    void shouldDeserializeCreatedAccountResponse(){

        CreateAccountRequest request = TestDataFactory.createAccountRequest("ACC6001", 3000, "Tom");

        Response response = bankApiClient.createAccount(request);

        assertEquals(201, response.getStatusCode());

        AccountResponse account = response.as(AccountResponse.class);

        assertEquals("ACC6001", account.getAccountNumber());
        assertEquals(3000.0, account.getBalance());
        assertEquals("Tom", account.getOwner());
    }

    @Test
    void deleteAccountShouldReturn204AndAccountShouldDisappear(){

        String accountNumber = TestDataFactory.generateAccountNumber();

        CreateAccountRequest request = TestDataFactory.createAccountRequest(
                accountNumber, 1000, "Delete Test");

        Response createResponse =
                bankApiClient.createAccount(request);

        assertEquals(201, createResponse.getStatusCode());

        Response deleteResponse =
                bankApiClient.deleteAccount(accountNumber);

        assertEquals(204, deleteResponse.getStatusCode());


        Response getResponse =
                bankApiClient.getAccount(request.getAccountNumber());

        getResponse
                .then()
                .statusCode(404)
                .body("error", equalTo("ACCOUNT_NOT_FOUND"));

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

        String accountNumber = TestDataFactory.generateAccountNumber();

        CreateAccountRequest createRequest =
        TestDataFactory.createAccountRequest(accountNumber, 1000, "Deposit Test");

        Response createResponse = bankApiClient.createAccount(createRequest);

        assertEquals(201, createResponse.getStatusCode());

        testAccountNumbers.add(accountNumber);

            AmountRequest depositRequest =
        TestDataFactory.createAmountRequest(250);

            Response depositResponse = bankApiClient.deposit(accountNumber, depositRequest);

            assertEquals(204, depositResponse.getStatusCode());

            Response getResponse =
                    bankApiClient.getAccount(accountNumber);

            getResponse
                    .then()
                    .statusCode(200);

            BigDecimal actualBalance = getResponse.jsonPath().getObject("balance", BigDecimal.class);

            BigDecimal expectedBalance = new BigDecimal("1250");

            assertEquals(0, expectedBalance.compareTo(actualBalance));

    }

    @Test
    void depositShouldReturn400WhenAmountIsNegative(){

        String accountNumber = "ACC1002";

        Response beforeResponse =
                bankApiClient.getAccount(accountNumber);

        float balanceBefore = beforeResponse.jsonPath().getFloat("balance");

        AmountRequest depositRequest =
                TestDataFactory.createAmountRequest(-100);

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

        String accountNumber = TestDataFactory.generateAccountNumber();

        CreateAccountRequest createRequest =
        TestDataFactory.createAccountRequest(accountNumber, 1000, "Deposit Test");

        Response createResponse = bankApiClient.createAccount(createRequest);

        assertEquals(201, createResponse.getStatusCode());

        Response beforeResponse =
                bankApiClient.getAccount(accountNumber);

        float balanceBefore = beforeResponse.jsonPath().getFloat("balance");

        AmountRequest withdrawRequest =
                TestDataFactory.createAmountRequest(100);

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
                TestDataFactory.createAmountRequest(-100);

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
        AmountRequest withdrawRequest = TestDataFactory.createAmountRequest(withdrawAmount);

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

        AmountRequest depositRequest = TestDataFactory.createAmountRequest(100);

        Response depositResponse = bankApiClient.deposit(accountNumber, depositRequest);

        depositResponse
                .then()
                .statusCode(404)
                .body("error", equalTo("ACCOUNT_NOT_FOUND"))
                .body("message", equalTo("Account "+accountNumber+" was not found"));
    }

    @Test
    void fullAccountLifecycleShouldWork(){

        String accountNumber = TestDataFactory.generateAccountNumber();

        CreateAccountRequest createRequest = TestDataFactory.createAccountRequest(accountNumber, 1000, "Test Account");

        Response createResponse = bankApiClient.createAccount(createRequest);

        assertEquals(201, createResponse.getStatusCode());

        AmountRequest amountDeposit = TestDataFactory.createAmountRequest(250);

        Response depositResponse = bankApiClient.deposit(accountNumber, amountDeposit);

        depositResponse
                .then()
                .statusCode(204);

        AmountRequest amountWithdraw = TestDataFactory.createAmountRequest(100);

        Response withdrawResponse = bankApiClient.withdraw(accountNumber, amountWithdraw);

        withdrawResponse
                .then()
                .statusCode(204);

        Response getResponse = bankApiClient.getAccount(accountNumber);


        getResponse
                .then()
                .statusCode(200);

        BigDecimal actualBalance = getResponse.jsonPath().getObject("balance", BigDecimal.class);

        BigDecimal expectedBalance = new BigDecimal("1150");

        assertEquals(0, expectedBalance.compareTo(actualBalance));

        Response deleteResponse = bankApiClient.deleteAccount(accountNumber);

        deleteResponse
                .then()
                .statusCode(204);

        Response deleteAccResponse = bankApiClient.getAccount(accountNumber);

        deleteAccResponse
                .then()
                .statusCode(404)
                .body("error", equalTo("ACCOUNT_NOT_FOUND"));

    }

    @Test
    void accountBalanceShouldRemainCorrectAfterValidAndInvalidTransactions(){

        String accountNumber = TestDataFactory.generateAccountNumber();

        CreateAccountRequest createRequest = TestDataFactory.createAccountRequest(accountNumber, 2000, "Final Test");

        Response createResponse = bankApiClient.createAccount(createRequest);

        assertEquals(201, createResponse.getStatusCode());

        AmountRequest amountDeposit = TestDataFactory.createAmountRequest(500);

        Response depositResponse = bankApiClient.deposit(accountNumber, amountDeposit);

        depositResponse
                .then()
                .statusCode(204);

        AmountRequest amountNegativeRequest = TestDataFactory.createAmountRequest(-200);

        Response withdrawNegativeResponse = bankApiClient.withdraw(accountNumber, amountNegativeRequest);

        withdrawNegativeResponse
                .then()
                .statusCode(400)
                .body("error", equalTo("INVALID_ARGUMENT"));

        AmountRequest amountRequest = TestDataFactory.createAmountRequest(300);

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

    }

    @Test
    void depositValidAmountShouldUpdateBalance(){

        String accountNumber = "ACC1001";

        Response beforeResponse =
                given()
                        .spec(requestSpec)
                        .pathParam("accountNumber", accountNumber)
                        .log().all()
                        .when()
                        .get("/accounts/{accountNumber}");

        float balanceBefore = beforeResponse.jsonPath().getFloat("balance");

        AmountRequest depositRequest = new AmountRequest();
        depositRequest.setAmount(200);

        Response depositResponse =
                given()
                        .spec(requestSpec)
                        .pathParam("accountNumber", accountNumber)
                        .body(depositRequest)
                  .when()
                         .post("/accounts/{accountNumber}/deposit");

        depositResponse
                .then()
                .statusCode(204);

        Response afterResponse = bankApiClient.getAccount(accountNumber);

        float balanceAfter = afterResponse.jsonPath().getFloat("balance");

        assertEquals(balanceBefore + 200, balanceAfter, 0.01f);

    }

    @Test
    void depositNegativeAmountShouldReturn400(){

        String accountNumber = "ACC1002";

        Response beforeResponse =
                given()
                        .spec(requestSpec)
                        .pathParam("accountNumber", accountNumber)
                        .log().all()
                        .when()
                        .get("/accounts/{accountNumber}");
        beforeResponse
                .then()
                .statusCode(200);

        float balanceBefore = beforeResponse.jsonPath().getFloat("balance");

        AmountRequest depositRequest = new AmountRequest();
        depositRequest.setAmount(-200);

        Response depositResponse =
                given()
                        .spec(requestSpec)
                        .pathParam("accountNumber", accountNumber)
                        .body(depositRequest)
                        .when()
                        .post("accounts/{accountNumber}/deposit");

        depositResponse
                .then()
                .statusCode(400);

        Response afterResponse = bankApiClient.getAccount(accountNumber);
        afterResponse
                .then()
                .statusCode(200);

        float balanceAfter = afterResponse.jsonPath().getFloat("balance");

        assertEquals(balanceBefore, balanceAfter, 0.01f);

    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -100, -9999})
    void depositInvalidAmountShouldReturn400(int amount) {

        String accountNumber = TestDataFactory.generateAccountNumber();

        CreateAccountRequest request = TestDataFactory.createAccountRequest(accountNumber, 2000, "Invalid Amount Test");

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

        AmountRequest depositRequest = new AmountRequest();

        Response beforeResponse =
                given()
                        .spec(requestSpec)
                        .pathParam("accountNumber", createdAccountNumber)
                        .log().all()
                        .when()
                        .get("accounts/{accountNumber}");

        beforeResponse
                .then()
                .statusCode(200);

        float balanceBefore = beforeResponse.jsonPath().getFloat("balance");

        depositRequest.setAmount(amount);

        Response depositResponse =
                given()
                        .spec(requestSpec)
                        .pathParams("accountNumber", createdAccountNumber)
                        .body(depositRequest)
                        .when()
                        .post("accounts/{accountNumber}/deposit");

        depositResponse
                .then()
                .statusCode(400);


        Response afterResponse =
                given()
                        .spec(requestSpec)
                        .pathParam("accountNumber", createdAccountNumber)
                        .log().all()
                        .when()
                        .get("accounts/{accountNumber}");

        afterResponse
                .then()
                .statusCode(200);

        float balanceAfter = afterResponse.jsonPath().getFloat("balance");

        assertEquals(balanceBefore, balanceAfter, 0.01f);
    }

    @ParameterizedTest
    @CsvSource({
            "-1, 400",
            "0, 400",
            "1, 204",
            "2, 204",
            "4999, 204",
            "5000, 204",
            "5001, 204"
    })
    void depositBoundaryValuesTest(int amount, int expectedStatus){

        String createdAccount = createTestAccount(2000, "Boundary Values Test");

        Response beforeResponse = bankApiClient.getAccount(createdAccount);

        beforeResponse
                .then()
                .statusCode(200);

        BigDecimal balanceBefore = beforeResponse.jsonPath().getObject("balance", BigDecimal.class);

        AmountRequest depositRequest = TestDataFactory.createAmountRequest(amount);

        Response depositResponse = bankApiClient.deposit(createdAccount, depositRequest);

        depositResponse
                .then()
                .statusCode(expectedStatus);

        Response afterResponse = bankApiClient.getAccount(createdAccount);

        afterResponse
                .then()
                .statusCode(200);

        BigDecimal balanceAfter = afterResponse.jsonPath().getObject("balance", BigDecimal.class);

        if(expectedStatus == 204) {

            BigDecimal depositAmount = BigDecimal.valueOf(amount);

            BigDecimal expectedBalance = balanceBefore.add(depositAmount);

                    assertEquals(0, expectedBalance.compareTo(balanceAfter),
                            "Balance mismatch. Expected: " + expectedBalance
                                    + ", Actual: " + balanceAfter);

        }else{
            assertEquals(0, balanceBefore.compareTo(balanceAfter));

        }
    }

    @Test
    void transferValidAmountShouldUpdateBothBalances(){

        String senderTestAccount = createTestAccount(2000, "Sender Test Account");

        String receiverTestAccount  = createTestAccount(1000, "Receiver Test Account");

        Response beforeResponseSender = bankApiClient.getAccount(senderTestAccount);
        beforeResponseSender
                .then()
                .statusCode(200);

        BigDecimal balanceBeforeSender = beforeResponseSender.jsonPath().getObject("balance", BigDecimal.class);

        Response beforeResponseReceiver = bankApiClient.getAccount(receiverTestAccount);

        beforeResponseReceiver
                .then()
                .statusCode(200);


        BigDecimal balanceBeforeReceiver = beforeResponseReceiver.jsonPath().getObject("balance", BigDecimal.class);


        TransferRequest transferRequest = new TransferRequest();

        transferRequest.setFromAccount(senderTestAccount);
        transferRequest.setToAccount(receiverTestAccount);
        transferRequest.setAmount(500);

        Response transferResponse =
                bankApiClient.transfer(transferRequest);

        transferResponse.then().statusCode(204);

        Response afterResponseSender = bankApiClient.getAccount(senderTestAccount);
        afterResponseSender.then().statusCode(200);

        Response afterResponseReceiver = bankApiClient.getAccount(receiverTestAccount);
        afterResponseReceiver.then().statusCode(200);

        BigDecimal afterBalanceSender = afterResponseSender.jsonPath().getObject("balance", BigDecimal.class);
        BigDecimal afterBalanceReceiver = afterResponseReceiver.jsonPath().getObject("balance", BigDecimal.class);

        BigDecimal transferAmount = BigDecimal.valueOf(500);

        BigDecimal expectedSenderBalance = balanceBeforeSender.subtract(transferAmount);
        BigDecimal expectedReceiverBalance = balanceBeforeReceiver.add(transferAmount);


        assertEquals(0, expectedSenderBalance.compareTo(afterBalanceSender));
        assertEquals(0, expectedReceiverBalance.compareTo(afterBalanceReceiver));

    }

    @ParameterizedTest
    @CsvSource({"-100, 400",
            "-1, 400",
            "0, 400"})
    void transferInvalidAmountShouldReturn400(double amount, int expectedStatus){

        String sender = createTestAccount(2000, "Sender Test Account");

        String receiver = createTestAccount(1000, "Receiver Test Account");

        Response beforeResponseSender = bankApiClient.getAccount(sender);

        beforeResponseSender.then().statusCode(200);

        Response beforeResponseReceiver = bankApiClient.getAccount(receiver);
        beforeResponseReceiver.then().statusCode(200);

        BigDecimal balanceBeforeSender = beforeResponseSender.jsonPath().getObject("balance", BigDecimal.class);

        BigDecimal balanceBeforeReceiver = beforeResponseReceiver.jsonPath().getObject("balance", BigDecimal.class);

        TransferRequest transferRequest = new TransferRequest();

        transferRequest.setFromAccount(sender);
        transferRequest.setToAccount(receiver);
        transferRequest.setAmount(amount);

        Response transferResponse = bankApiClient.transfer(transferRequest);

        transferResponse.then().statusCode(expectedStatus);

        Response afterResponseSender = bankApiClient.getAccount(sender);
        afterResponseSender.then().statusCode(200);

        Response afterResponseReceiver = bankApiClient.getAccount(receiver);
        afterResponseReceiver.then().statusCode(200);

        BigDecimal balanceAfterSender = afterResponseSender.jsonPath().getObject("balance", BigDecimal.class);

        BigDecimal balanceAfterReceiver = afterResponseReceiver.jsonPath().getObject("balance", BigDecimal.class);

        BigDecimal transferAmount = BigDecimal.valueOf(amount);

        assertEquals(0, balanceBeforeSender.compareTo(balanceAfterSender));
        assertEquals(0, balanceBeforeReceiver.compareTo(balanceAfterReceiver));
    }

    @Test
    void transferInsufficientFundsShouldNotChangeBalances(){

        String sender = createTestAccount(2000, "Sender Test Account");

        String receiver = createTestAccount(1000, "Receiver Test Account");

        Response beforeResponseSender = bankApiClient.getAccount(sender);

        beforeResponseSender.then().statusCode(200);

        Response beforeResponseReceiver = bankApiClient.getAccount(receiver);
        beforeResponseReceiver.then().statusCode(200);

        BigDecimal balanceBeforeSender = beforeResponseSender.jsonPath().getObject("balance", BigDecimal.class);

        BigDecimal balanceBeforeReceiver = beforeResponseReceiver.jsonPath().getObject("balance", BigDecimal.class);

        TransferRequest transferRequest = new TransferRequest();

        transferRequest.setFromAccount(sender);
        transferRequest.setToAccount(receiver);
        transferRequest.setAmount(2500);

        Response transferResponse = bankApiClient.transfer(transferRequest);

        transferResponse.then().statusCode(400);

        Response afterResponseSender = bankApiClient.getAccount(sender);
        afterResponseSender.then().statusCode(200);

        Response afterResponseReceiver = bankApiClient.getAccount(receiver);
        afterResponseReceiver.then().statusCode(200);

        BigDecimal balanceAfterSender = afterResponseSender.jsonPath().getObject("balance", BigDecimal.class);

        BigDecimal balanceAfterReceiver = afterResponseReceiver.jsonPath().getObject("balance", BigDecimal.class);

        assertEquals(0, balanceBeforeSender.compareTo(balanceAfterSender),"Sender balance should not change");
        assertEquals(0, balanceBeforeReceiver.compareTo(balanceAfterReceiver),"Receiver balance should not change");

    }

    @Test
    void transferToNonexistingAccountShouldReturn404(){

        String sender = createTestAccount(2000, "Sender");

        String receiver = "ACC-NONEXISTENT";

        Response beforeResponseSender = bankApiClient.getAccount(sender);

        beforeResponseSender.then().statusCode(200);

        BigDecimal balanceBeforeSender = beforeResponseSender.jsonPath().getObject("balance", BigDecimal.class);

        TransferRequest transferRequest = new TransferRequest();

        transferRequest.setFromAccount(sender);
        transferRequest.setToAccount(receiver);
        transferRequest.setAmount(500);

        Response transferResponse = bankApiClient.transfer(transferRequest);

        transferResponse.then().statusCode(404);

        Response afterSenderResponse = bankApiClient.getAccount(sender);

        afterSenderResponse.then().statusCode(200);

        BigDecimal balanceAfterSender = afterSenderResponse.jsonPath().getObject("balance", BigDecimal.class);

        assertEquals(0, balanceBeforeSender.compareTo(balanceAfterSender), "Sender balance must remain unchanged when receiver does not exist");
    }

}
