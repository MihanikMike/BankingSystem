package com.mike.bank.ui.pages;

import com.mike.bank.ui.base.BasePage;
import com.mike.bank.ui.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class UploadPage extends BasePage {

    private By fileInput = By.id("file-upload");

    private By uploadButtom = By.id("file-submit");

    private By uploadedFiles = By.id("uploaded-files");

    public UploadPage(WebDriver driver){
        super(driver);
    }

    public UploadPage open(){

        driver.get(ConfigReader.get("base.url")+ "/upload");
        return this;
    }

    public void uploadFile(String filePath){

        waitForVisible(fileInput).sendKeys(filePath);

        waitForClickable(uploadButtom).click();
    }

    public String getUploadedFileName(){

        return getText(uploadedFiles);
    }

}
