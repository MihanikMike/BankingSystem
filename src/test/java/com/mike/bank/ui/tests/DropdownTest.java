package com.mike.bank.ui.tests;

import com.mike.bank.ui.base.BaseTest;
import com.mike.bank.ui.pages.DropdownPage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DropdownTest extends BaseTest {

    @Test
    void shouldSelectOptionFromDropdown(){

        DropdownPage dropdownPage = new DropdownPage(driver).open();

        dropdownPage.selectByVisibleText("Option 1");

        assertEquals("Option 1", dropdownPage.getSelectOption());
    }
}
