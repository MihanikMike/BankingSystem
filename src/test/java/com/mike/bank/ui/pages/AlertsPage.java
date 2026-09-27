package com.mike.bank.ui.pages;

import com.mike.bank.ui.base.BasePage;
import com.mike.bank.ui.config.ConfigReader;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class AlertsPage extends BasePage {

    private By jsAlertButton = By.xpath("//button[text()='Click for JS Alert']");

    private By result = By.id("result");

    public AlertsPage(WebDriver driver){

        super(driver);
    }

    public AlertsPage open(){

        driver.get(ConfigReader.get("base.url") + "/javascript_alerts");
        return this;
    }

    public void clickJsAlert(){

        waitForClickable(jsAlertButton).click();
    }

    public void acceptAlert(){

        Alert alert = driver.switchTo().alert();
        alert.accept();
    }

    public String getResult(){

        return getText(result);
    }

}
