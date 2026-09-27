package com.mike.bank.ui.pages;

import com.mike.bank.ui.base.BasePage;
import com.mike.bank.ui.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class LoginPage extends BasePage {

    private By usernameInput = By.id("username");

    private By passwordInput = By.id("password");

    private By loginButton = By.cssSelector("button[type='submit']");

    private By flashMessage = By.id("flash");


    public LoginPage open(){

        driver.get(ConfigReader.get("base.url") + "/login");

        return this;
    }

    public LoginPage(WebDriver driver){
        super(driver);

    }

    public void enterUsername(String username){

        waitForVisible(usernameInput).sendKeys(username);
    }

    public void enterPassword(String password){

        waitForVisible(passwordInput).sendKeys(password);
    }

    public void clickLogin(){

        waitForClickable(loginButton).click();
    }

    public void login(String username, String password){

        enterUsername(username);
        enterPassword(password);
        clickLogin();
    }

    public String getFlashMessage(){

        return getText(flashMessage);
    }

    public SecureAreaPage loginAsValidUser(String username, String password){

        enterUsername(username);
        enterPassword(password);
        clickLogin();

        return new SecureAreaPage(driver);
    }

    public boolean isLoginButtonDisplayed(){

        return waitForVisible(loginButton).isDisplayed();
    }

    public boolean isLoginButtonEnabled(){

        return waitForClickable(loginButton).isDisplayed();
    }
}
