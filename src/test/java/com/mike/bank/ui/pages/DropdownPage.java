package com.mike.bank.ui.pages;

import com.mike.bank.ui.base.BasePage;
import com.mike.bank.ui.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

public class DropdownPage extends BasePage {

    private By dropdown = By.id("dropdown");

    public DropdownPage(WebDriver driver){
        super(driver);
    }

    public DropdownPage open(){

        driver.get(
                ConfigReader.get("base.url") + "/dropdown"
        );

        return this;
    }

    public void selectByVisibleText(String option){

        WebElement dropdownElement = driver.findElement(dropdown);

        Select select = new Select(dropdownElement);

        select.selectByVisibleText(option);
    }

    public String getSelectOption(){

        WebElement dropdownElement = waitForVisible(dropdown);

        Select select = new Select(dropdownElement);

        return select.getFirstSelectedOption().getText();
    }
}
