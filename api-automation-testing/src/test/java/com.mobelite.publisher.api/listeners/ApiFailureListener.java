package com.mobelite.publisher.api.listeners;

import com.mobelite.publisher.api.base.BaseTest;
import io.qameta.allure.Allure;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;

public class ApiFailureListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        Object instance = result.getInstance();
        if (instance instanceof BaseTest test) { // base class holding last request/response

            String lastRequest = test.getLastRequest();
            String lastResponse = test.getLastResponse();

            try {
                if (lastRequest != null) {
                    Allure.addAttachment(
                            "Failed API Request",
                            new ByteArrayInputStream(lastRequest.getBytes())
                    );
                }
                if (lastResponse != null) {
                    Allure.addAttachment(
                            "Failed API Response",
                            new ByteArrayInputStream(lastResponse.getBytes())
                    );
                }
            } catch (Exception e) {
                // swallow errors, do not break tests
            }
        }
    }
}