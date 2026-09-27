package com.mike.bank.ui.driver;

import com.mike.bank.ui.config.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

public class DriverFactory {

    public static WebDriver createDriver(){

        String browser = ConfigReader.get("browser");

        boolean headless = Boolean.parseBoolean(ConfigReader.get("headless"));

        if(browser.equalsIgnoreCase("chrome")){

            ChromeOptions options = new ChromeOptions();

            if(headless){
                options.addArguments("--headless=new");
            }

            return new ChromeDriver(options);
        }

        if(browser.equalsIgnoreCase("firefox")){

            FirefoxOptions options = new FirefoxOptions();

            if(headless){

                options.addArguments("-headless");
            }

            return new FirefoxDriver(options);
        }

        throw new RuntimeException("Unsupported browser: " + browser);
    }


}
