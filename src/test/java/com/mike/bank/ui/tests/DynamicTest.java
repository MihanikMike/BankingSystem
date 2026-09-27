package com.mike.bank.ui.tests;

import com.mike.bank.ui.base.BaseTest;
import com.mike.bank.ui.pages.DynamicLoadingPage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DynamicTest extends BaseTest {

    @Test
    void shouldWaitForDynamicText(){

        DynamicLoadingPage dynamicLoadingPage = new DynamicLoadingPage(driver).open();

        dynamicLoadingPage.clickStart();

        dynamicLoadingPage.waitForLoadingToFinish();

        assertEquals("Hello World!", dynamicLoadingPage.getFinishText());
    }
}
