package com.mike.bank.ui.base;

import com.mike.bank.ui.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver){
        this.driver = driver;

        int timeout = Integer.parseInt(ConfigReader.get("timeout"));

        this.wait = new WebDriverWait(
                driver, Duration.ofSeconds(timeout)
        );
    }

    protected WebElement waitForVisible(By locator){

        return wait.until(
                ExpectedConditions.visibilityOfElementLocated(locator)
        );
    }

    protected WebElement waitForClickable(By locator){

        return wait.until(
                ExpectedConditions.elementToBeClickable(locator)
        );
    }

    protected String getText(By locator){

        return waitForVisible(locator).getText();
    }

    protected boolean waitForInvisible(By locator){

        return wait.until(
                ExpectedConditions.invisibilityOfElementLocated(locator)
        );
    }

    protected void scrollToElement(By locator){

        WebElement element = waitForVisible(locator);

        JavascriptExecutor js = (JavascriptExecutor) driver;

        js.executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

}
