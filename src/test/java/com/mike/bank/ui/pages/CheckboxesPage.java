package com.mike.bank.ui.pages;

import com.mike.bank.ui.base.BasePage;
import com.mike.bank.ui.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CheckboxesPage extends BasePage {

    private By firstCheckbox = By.cssSelector("#checkboxes input:nth-of-type(1)");

    private By secondCheckbox = By.cssSelector("#checkboxes input:nth-of-type(2)");

    public CheckboxesPage(WebDriver driver){

        super(driver);
    }

    public CheckboxesPage open(){

        driver.get(ConfigReader.get("base.url") + "/checkboxes");

        return this;
    }

    public void clickFirstCheckbox(){

        waitForClickable(firstCheckbox).click();


    }

    public boolean isFirstCheckboxSelected(){

        return waitForVisible(firstCheckbox).isSelected();

    }

    public void selectFirstCheckbox(){

        if(!isFirstCheckboxSelected()){
            waitForClickable(firstCheckbox).click();
        }
    }

    public void unselectFirstCheckbox(){

        if(isFirstCheckboxSelected()){
            waitForClickable(firstCheckbox).click();
        }
    }


}
