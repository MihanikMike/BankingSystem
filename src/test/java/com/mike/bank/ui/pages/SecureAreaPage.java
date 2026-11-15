package com.mike.bank.ui.pages;

import com.mike.bank.ui.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;


public class SecureAreaPage extends BasePage {

    private By flashMessage = By.id("flash");

    private By logoutButton = By.cssSelector("a[href='/logout']");

    public SecureAreaPage(WebDriver driver){
        super(driver);
    }

    public String getFlashMessage(){

        return getText(flashMessage);
    }

    public LoginPage logout() {

        waitForClickable(logoutButton).click();

        System.out.println("After logout click URL: " + driver.getCurrentUrl());

        WebDriverWait wait = new WebDriverWait(
                driver,
                Duration.ofSeconds(10)
        );

        wait.until(
                ExpectedConditions.urlContains("/login")
        );

        return new LoginPage(driver);
    }

    @Bean
    public JwtEncoder jwtEncoder(KeyPair jwtKeyPair) {
        RSAPublicKey publicKey =
                (RSAPublicKey) jwtKeyPair.getPublic();

        RSAPrivateKey privateKey =
                (RSAPrivateKey) jwtKeyPair.getPrivate();

        return NimbusJwtEncoder
                .withKeyPair(publicKey, privateKey)
                .build();
    }


}
