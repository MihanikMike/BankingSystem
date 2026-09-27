package com.mike.bank.ui.pages;
import com.mike.bank.ui.base.BasePage;
import com.mike.bank.ui.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class WindowsPage extends BasePage {

    private By clickHereLink = By.linkText("Click Here");

    private By heading = By.tagName("h3");

    public WindowsPage(WebDriver driver){
        super(driver);
    }

    public WindowsPage open(){

        driver.get(ConfigReader.get("base.url")+ "/windows");
        return this;
    }

    public void clickHere(){

        waitForClickable(clickHereLink).click();
    }

    public String getHeading(){
        return getText(heading);
    }
}
