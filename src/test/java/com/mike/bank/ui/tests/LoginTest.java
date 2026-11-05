package com.mike.bank.ui.tests;

import com.mike.bank.ui.base.BaseTest;
import com.mike.bank.ui.pages.LoginPage;
import com.mike.bank.ui.pages.SecureAreaPage;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class LoginTest extends BaseTest {

    @Test
    void userShouldBeAbleToLogin(){

        LoginPage loginPage = new LoginPage(driver).open();

        SecureAreaPage secureAreaPage = loginPage.loginAsValidUser("tomsmith", "SuperSecretPassword!");

        assertTrue(
                secureAreaPage.getFlashMessage().contains("You logged into a secure area!")
        );
    }

    @Test
    void loginShouldFailWhenPasswordIsInvalid(){

        LoginPage loginPage = new LoginPage(driver).open();

        loginPage.enterUsername("tomsmith");
        loginPage.enterPassword("WrongPassword");
        loginPage.clickLogin();

        takeScreenshot("login-page");

        assertTrue(
                loginPage.getFlashMessage().contains("Your password is invalid!")
        );
    }

    @Test
    void fullFlowLoginLogoutTest(){

        LoginPage loginPage = new LoginPage(driver).open();

        SecureAreaPage secureAreaPage = loginPage.loginAsValidUser("tomsmith", "SuperSecretPassword!");

        LoginPage loginPageAfterLogout = secureAreaPage.logout();

        String actualMessage = loginPageAfterLogout.waitForFlashMessage("You logged out of the secure area!");

        assertTrue(
                actualMessage.contains("You logged out of the secure area!"), "Expected logout message, but was: [" + actualMessage + "]"
        );
    }

}
