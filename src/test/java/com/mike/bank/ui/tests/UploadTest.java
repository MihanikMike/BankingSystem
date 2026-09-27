package com.mike.bank.ui.tests;

import com.mike.bank.ui.base.BaseTest;
import com.mike.bank.ui.pages.UploadPage;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UploadTest extends BaseTest {

    @Test
    void shouldUploadFile(){

        String filePath = Path.of("src", "test", "resources", "test-files", "upload-test.txt").toAbsolutePath().toString();

        UploadPage uploadPage = new UploadPage(driver).open();

        uploadPage.uploadFile(filePath);

        String uploadedFileName = uploadPage.getUploadedFileName();

        assertEquals("upload-test.txt", uploadedFileName);

    }
}
