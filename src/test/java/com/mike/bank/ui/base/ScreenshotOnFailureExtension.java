package com.mike.bank.ui.base;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;

public class ScreenshotOnFailureExtension implements TestExecutionExceptionHandler {

    @Override
    public void handleTestExecutionException(
            ExtensionContext context, Throwable throwable
    ) throws Throwable{

        Object testInstance =
                context.getRequiredTestInstance();

        if(testInstance instanceof BaseTest baseTest) {

            String testName = context.getRequiredTestMethod().getName();

            baseTest.takeScreenshot(testName);
        }

        throw throwable;
    }
}
