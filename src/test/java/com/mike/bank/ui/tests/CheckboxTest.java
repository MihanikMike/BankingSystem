package com.mike.bank.ui.tests;

import com.mike.bank.ui.base.BaseTest;
import com.mike.bank.ui.pages.CheckboxesPage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CheckboxTest extends BaseTest {

    @Test
    void shouldSelectFirstCheckbox(){

        CheckboxesPage checkboxesPage = new CheckboxesPage(driver).open();

        assertFalse(checkboxesPage.isFirstCheckboxSelected());

        checkboxesPage.clickFirstCheckbox();

        assertTrue(checkboxesPage.isFirstCheckboxSelected());
    }

    @Test
    void shouldSelectAndUnselectFirstCheckbox(){

        CheckboxesPage checkboxesPage = new CheckboxesPage(driver).open();

        checkboxesPage.selectFirstCheckbox();

        assertTrue(checkboxesPage.isFirstCheckboxSelected());

        checkboxesPage.unselectFirstCheckbox();

        assertFalse(checkboxesPage.isFirstCheckboxSelected());
    }
}
