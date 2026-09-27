package com.mike.bank.ui.pages;

import com.mike.bank.ui.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SecureAreaPage extends BasePage {

    private By flashMessage = By.id("flash");

    private By logoutButton = By.cssSelector("a[href='/logout']");

    public SecureAreaPage(WebDriver driver){
        super(driver);
    }

    public String getFlashMessage(){

        return getText(flashMessage);
    }

    public LoginPage logout(){

        waitForClickable(logoutButton).click();

        return new LoginPage(driver);
    }


}
