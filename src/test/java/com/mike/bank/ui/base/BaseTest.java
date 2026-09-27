package com.mike.bank.ui.base;

import com.mike.bank.ui.driver.DriverFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@ExtendWith(ScreenshotOnFailureExtension.class)
public abstract class BaseTest {

    protected WebDriver driver;

    @BeforeEach
    void setup(){

        driver = DriverFactory.createDriver();

    }

    @AfterEach
    void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }

    protected void takeScreenshot(String screenshotName){

        TakesScreenshot screenshotDriver = (TakesScreenshot) driver;

        byte[] screenshot =
                screenshotDriver.getScreenshotAs(OutputType.BYTES);

        Path screenshotDirectory = Path.of("target", "screenshots");

        try{
            Files.createDirectories(screenshotDirectory);

            Path screenshotPath = screenshotDirectory.resolve(screenshotName + ".png");

            Files.write(screenshotPath, screenshot);

        }catch (IOException exception){

            throw new RuntimeException("Failed to save screenshot", exception);
        }
    }


}
