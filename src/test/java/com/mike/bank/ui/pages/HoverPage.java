package com.mike.bank.ui.pages;

import com.mike.bank.ui.base.BasePage;
import com.mike.bank.ui.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

public class HoverPage extends BasePage {

    private By firstUserImage = By.cssSelector(".figure:nth-of-type(1) img");

    private By firstUserCaption = By.cssSelector(".figure:nth-of-type(1) .figcaption");

    private By firstUserName = By.cssSelector(".figure:nth-of-type(1) .figcaption h5");

    public HoverPage(WebDriver driver){

        super(driver);
    }

    public HoverPage open(){

        driver.get(ConfigReader.get("base.url")+ "/hovers");
        return this;
    }

    public void hoverOverFirstUser(){

        WebElement image = waitForVisible(firstUserImage);

        Actions actions = new Actions(driver);

        actions.moveToElement(image)
                .perform();
    }

    public String getFirstUserName(){

        return getText(firstUserName);
    }

    public String getFirstUserCaption(){

        return getText(firstUserCaption);
    }
}
