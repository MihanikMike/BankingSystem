package com.mike.bank.ui.tests;

import com.mike.bank.ui.pages.AlertsPage;
import com.mike.bank.ui.base.BaseTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AlertTest extends BaseTest {

    @Test
    void shouldAcceptJavaScriptAlert(){

        AlertsPage alertsPage = new AlertsPage(driver).open();

        alertsPage.clickJsAlert();

        alertsPage.acceptAlert();

        assertEquals("You successfully clicked an alert", alertsPage.getResult());
    }
}
