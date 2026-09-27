package com.mike.bank.api;

import com.mike.bank.AmountRequest;
import com.mike.bank.CreateAccountRequest;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class BankApiClient {

    private final RequestSpecification requestSpec;

    public BankApiClient (RequestSpecification requestSpec){
        this.requestSpec = requestSpec;
    }

    public Response createAccount(CreateAccountRequest request){

        return given()
                .spec(requestSpec)
                .body(request)
        .when()
                .post("/accounts");
    }

    public Response getAccount(String accountNumber){

        return given()
                .spec(requestSpec)
                .pathParam("accountNumber", accountNumber)
                .log().all()
        .when()
                .get("/accounts/{accountNumber}");
    }

    public Response getAccounts(){

        return given()
                .spec(requestSpec)
        .when()
                .get("/accounts");
    }

    public Response deleteAccount(String accountNumber){

        return given()
                .spec(requestSpec)
                .pathParam("accountNumber", accountNumber)
        .when()
                .delete("/accounts/{accountNumber}");
    }

    public Response deposit(String accountNumber, AmountRequest request){
        return given()
                .spec(requestSpec)
                .pathParam("accountNumber", accountNumber)
                .body(request)
        .when()
                .post("/accounts/{accountNumber}/deposit");
    }

    public Response withdraw(String accountNumber, AmountRequest request){
        return given()
                .spec(requestSpec)
                .pathParam("accountNumber", accountNumber)
                .body(request)
        .when()
                .post("/accounts/{accountNumber}/withdraw");
    }

}
