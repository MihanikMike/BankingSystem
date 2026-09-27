package com.mike.bank.ui.pages;

import com.mike.bank.ui.base.BasePage;
import com.mike.bank.ui.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class TablesPage extends BasePage {

    private By rows = By.cssSelector("#table1 tbody tr");

    private By lastNames = By.cssSelector("#table1 tbody tr td:nth-child(1)");

    private By lastNameHeader = By.cssSelector("#table1 th:nth-child(1)");

    By locator = rowByLastName("Bach");

    public TablesPage(WebDriver driver){
        super(driver);
    }

    public TablesPage open(){

        driver.get(ConfigReader.get("base.url") + "/tables");
        return this;
    }

    public int getNumberOfRows(){

        List<WebElement> tablesRows = driver.findElements(rows);

        return tablesRows.size();
    }

    public List<String> getLastNames(){

        List<WebElement> elements = driver.findElements(lastNames);

        List<String> names = new ArrayList<>();

        for(WebElement element : elements){
            String lastName = element.getText();
            names.add(lastName);
        }

        return names;

    }

    public Optional<String> getEmailByLastName(String targetLastName){

       List <WebElement> matchingRows = driver.findElements(rowByLastName(targetLastName));

       if(matchingRows.isEmpty()){
                return Optional.empty();
            }

        WebElement row = matchingRows.get(0);

       String email = row.findElement(By.cssSelector("td:nth-child(3)")).getText();

       return Optional.of(email);
     }


    public void clickEditByLastName(String targetLastName){

        List<WebElement> matchingRows = driver.findElements(rowByLastName(targetLastName));

        if(matchingRows.isEmpty()){

            return;
        }

        WebElement row = matchingRows.get(0);

        row.findElement(By.linkText("edit")).click();

        /*return findRowByLastName(targetLastName)
            .map(row ->
                    row.findElement(
                            By.cssSelector("td:nth-child(3)")
                    ).getText()
            );*/
    }


    private Optional<WebElement> findRowByLastName(String targetLastName){

        List<WebElement> tablesRows = driver.findElements(rows);

        for(WebElement row : tablesRows){

            WebElement lastNameCell = row.findElement(By.cssSelector("td:nth-child(1)"));

            if(lastNameCell.getText().equals(targetLastName)){

                return Optional.of(row);

                /*public void clickEditByLastName(String targetLastName){

                findRowByLastName(targetLastName)
                        .ifPresent(row ->
                             row.findElement(By.linkText("edit")).click());
                             }*/
            }
        }
        return Optional.empty();
    }

    private By rowByLastName(String lastName){

        return By.xpath("//table[@id='table1']//tr[td[1][text()='"+lastName+"']]");
    }

    public void sortByLastName(){

        waitForClickable(lastNameHeader).click();
    }

    public void sortLastNameDescending(){

        List<String> actual = getLastNames();

        List<String> expected = new ArrayList<>(actual);

        expected.sort(Comparator.reverseOrder());

        if(actual.equals(expected)){
            return;
        }

        sortByLastName();

        actual = getLastNames();

        if(!actual.equals(expected)){
            sortByLastName();
        }

    }

}
