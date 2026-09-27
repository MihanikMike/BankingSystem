package com.mike.bank.ui.pages;

import com.mike.bank.ui.base.BasePage;
import com.mike.bank.ui.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class FramesPage extends BasePage {

    private By iframe = By.cssSelector("iframe");

    private By editor = By.id("tinymce");

    private By pageHeading = By.tagName("h3");

    public FramesPage(WebDriver driver){

        super(driver);
    }

    public FramesPage open(){

        driver.get(ConfigReader.get("base.url") + "/iframe");

        return this;
    }

    public void switchToEditorFrame(){

        WebElement frame = waitForVisible(iframe);

        driver.switchTo().frame(frame);
    }

    public void switchToMainPage(){

        driver.switchTo().defaultContent();
    }

    public String getEditorText(){

        return getText(editor);
    }

    public String getPageHeading(){

        return getText(pageHeading);
    }
}
