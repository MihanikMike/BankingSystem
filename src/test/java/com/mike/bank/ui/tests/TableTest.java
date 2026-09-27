package com.mike.bank.ui.tests;

import com.mike.bank.ui.base.BaseTest;
import com.mike.bank.ui.pages.TablesPage;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TableTest extends BaseTest {

    @Test
    void shouldHaveFourRowsInTable(){

        TablesPage tablesPage = new TablesPage(driver).open();

        assertEquals(4, tablesPage.getNumberOfRows());
    }

    @Test
    void shouldReturnAllLastNames(){

        List<String> expected = List.of("Smith", "Bach", "Doe", "Conway");

        TablesPage tablesPage = new TablesPage(driver).open();

        List<String> actualLastNames = tablesPage.getLastNames();

        assertEquals(expected, actualLastNames);
    }

    @Test
    void shouldReturnEmailByLastName(){

        TablesPage tablesPage = new TablesPage(driver).open();

        Optional<String> email = tablesPage.getEmailByLastName("Bach");

        assertEquals("fbach@yahoo.com", email.get());
    }

    @Test
    void shouldReturnEmptyWhenLastNameDoesNotExist(){

        TablesPage tablesPage =
                new TablesPage(driver).open();

        Optional<String> email =
                tablesPage.getEmailByLastName("Unknown");

        assertTrue(email.isEmpty());
    }

    @Test
    void shouldSortLastNameAscending(){

        TablesPage tablesPage = new TablesPage(driver).open();

        tablesPage.sortByLastName();

        List<String> actual = tablesPage.getLastNames();

        List<String> expected = new ArrayList<>(actual);

        Collections.sort(expected);

        assertEquals(expected, actual);
    }

    @Test
    void shouldSortLastNameDescending(){

        TablesPage tablesPage = new TablesPage(driver).open();

        tablesPage.sortByLastName();

        List<String> actual = tablesPage.getLastNames();

        List<String> expected = new ArrayList<>(actual);

        expected.sort(Comparator.reverseOrder());

        if(!actual.equals(expected)){
            tablesPage.sortByLastName();

            actual = tablesPage.getLastNames();
        }

        //Collections.sort(expected, Comparator.reverseOrder());

        assertEquals(expected, actual);
    }
}
