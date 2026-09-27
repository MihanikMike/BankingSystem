package com.mike.bank.ui.tests;

import com.mike.bank.ui.base.BaseTest;
import com.mike.bank.ui.pages.WindowsPage;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WindowsTest extends BaseTest {

    @Test
    void shouldSwitchToNewWindowAndBack(){

        WindowsPage windowsPage = new WindowsPage(driver).open();

        String mainWindow = driver.getWindowHandle();

        windowsPage.clickHere();

        Set<String> windows = driver.getWindowHandles();

        for(String window : windows){
            if(!window.equals(mainWindow)){
                driver.switchTo().window(window);
            }
        }

        assertEquals("New Window", windowsPage.getHeading());

        driver.close();

        driver.switchTo().window(mainWindow);
    }

}
