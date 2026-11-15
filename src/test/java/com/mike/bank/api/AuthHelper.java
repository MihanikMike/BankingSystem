package com.mike.bank.api;

import com.mike.bank.LoginRequest;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;

public class AuthHelper {


    public static String loginAndGetToken(RequestSpecification requestSpec, String username, String password){

        LoginRequest request = new LoginRequest();
        request.setUsername(username);
        request.setPassword(password);

        return given()
                .spec(requestSpec)
                .body(request)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("token", not(isEmptyOrNullString()))
                .extract()
                .path("token");

    }
}
