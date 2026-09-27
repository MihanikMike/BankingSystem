package com.mike.bank.ui.tests;

import com.mike.bank.ui.base.BaseTest;
import com.mike.bank.ui.pages.HoverPage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HoverTest extends BaseTest {

    @Test
    void shouldShowUserInfoOnHover(){

        HoverPage hoverPage = new HoverPage(driver).open();

        hoverPage.hoverOverFirstUser();

        assertTrue(hoverPage.getFirstUserCaption().contains("name: user1"));

        assertEquals("name: user1", hoverPage.getFirstUserName());
    }
}
