package com.mike.bank.ui.pages;

import com.mike.bank.ui.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SecureAreaPage extends BasePage {

    private By flashMessage = By.id("flash");

    private By logoutButton = By.cssSelector("a[href='/logout']");

    public SecureAreaPage(WebDriver driver){
        super(driver);
    }

    public String getFlashMessage(){

        return getText(flashMessage);
    }

    public LoginPage logout() {

        waitForClickable(logoutButton).click();

        System.out.println("After logout click URL: " + driver.getCurrentUrl());

        WebDriverWait wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(10)
        );

        wait.until(
                ExpectedConditions.urlContains("/login")
        );

        return new LoginPage(driver);
    }


}
