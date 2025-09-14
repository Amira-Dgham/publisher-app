package com.mobelite.publisher.api.listeners;

import com.mobelite.publisher.api.core.ApiClient;
import io.qameta.allure.Allure;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;

public class ApiFailureListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        Object instance = result.getInstance();
        if (instance instanceof ApiClientHolder) {
            ApiClient client = ((ApiClientHolder) instance).getApiClient();

            if (client != null) {
                if (client.getLastRequestInfo() != null) {
                    Allure.addAttachment("Failed API Request",
                            new ByteArrayInputStream(client.getLastRequestInfo().getBytes()));
                }
                if (client.getLastResponseInfo() != null) {
                    Allure.addAttachment("Failed API Response",
                            new ByteArrayInputStream(client.getLastResponseInfo().getBytes()));
                }
            }
        }
    }
}