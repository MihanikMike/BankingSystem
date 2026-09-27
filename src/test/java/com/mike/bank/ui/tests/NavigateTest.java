package com.mike.bank.ui.tests;

import com.mike.bank.ui.base.BaseTest;
import com.mike.bank.ui.config.ConfigReader;
import com.mike.bank.ui.pages.FramesPage;
import com.mike.bank.ui.pages.LoginPage;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.*;

public class NavigateTest extends BaseTest {

    @Test
    void shouldSwitchToEditorPageAndBackToDefaultPage(){

        FramesPage framesPage = new FramesPage(driver).open();

        framesPage.switchToEditorFrame();

        assertEquals("Your content goes here.", framesPage.getEditorText());

        framesPage.switchToMainPage();

        assertEquals("An iFrame containing the TinyMCE WYSIWYG Editor", framesPage.getPageHeading());
    }

    @Test
    void shouldNavigateBackAndForward(){

        driver.get(ConfigReader.get("base.url")+ "/login");

        String loginUrl = driver.getCurrentUrl();

        driver.get(ConfigReader.get("base.url")+ "/checkboxes");

        driver.navigate().back();

        assertEquals(loginUrl, driver.getCurrentUrl());

        driver.navigate().forward();

        assertTrue(driver.getCurrentUrl().contains("/checkboxes"));
    }

    @Test
    void shouldDemonstrateStaleElement(){

        LoginPage loginPage = new LoginPage(driver).open();

        WebElement userName = driver.findElement(By.id("username"));

        driver.navigate().refresh();

        //loginPage.enterUsername("tomsmith");

        assertThrows(StaleElementReferenceException.class, () -> userName.sendKeys("tomsmith"));
    }
}
