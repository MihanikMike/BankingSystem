package com.mike.bank.ui.tests;

import com.mike.bank.ui.base.BaseTest;
import com.mike.bank.ui.pages.LoginPage;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.ParameterizedTest;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginParameterizedTest extends BaseTest {

    @ParameterizedTest
    @CsvSource({"tomsmith, WrongPassword,Your password is invalid!",
            "wronguser, SuperSecretPassword!, Your username is invalid!"})
    void loginShouldFailWithInvalidCredentials(String username,
                                               String password,
                                               String expectedMessage){

        LoginPage loginPage = new LoginPage(driver).open();

        loginPage.login(username, password);

        assertTrue(loginPage.getFlashMessage().contains(expectedMessage));
    }

    @ParameterizedTest
    @MethodSource("invalidLoginData")
    void loginShouldFailWithInvalidCredentialsMl(
            String username,
            String password,
            String expectedMessage){

        LoginPage loginPage =
                new LoginPage(driver).open();

        loginPage.login(username, password);

        assertTrue(
                loginPage.getFlashMessage()
                        .contains(expectedMessage)
        );
    }

    static Stream<Arguments> invalidLoginData(){

        return Stream.of(
                Arguments.of(
                        "tomsmith",
                        "WrongPassword",
                        "Your password is invalid!"
                ),
                Arguments.of(
                        "wronguser",
                        "SuperSecretPassword!",
                        "Your username is invalid!"
                )
        );
    }
}
