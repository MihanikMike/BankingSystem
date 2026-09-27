package com.mike.bank.ui.pages;

import com.mike.bank.ui.base.BasePage;
import com.mike.bank.ui.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DynamicLoadingPage extends BasePage {

    private By startButton = By.cssSelector("#start button");

    private By finishText = By.cssSelector("#finish h4");

    private By loadingIndicator = By.id("loading");

    public DynamicLoadingPage(WebDriver driver){
        super(driver);
    }

    public DynamicLoadingPage open(){

        driver.get(ConfigReader.get("base.url") + "/dynamic_loading/1");
        return this;
    }

    public void clickStart(){

        waitForClickable(startButton).click();
    }

    public String getFinishText(){

        return getText(finishText);
    }

    public void waitForLoadingToFinish(){

        waitForInvisible(loadingIndicator);
    }
}
