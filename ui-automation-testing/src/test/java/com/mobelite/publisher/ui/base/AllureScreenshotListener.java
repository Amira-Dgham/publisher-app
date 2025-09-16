package com.mobelite.publisher.ui.base;
import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.ByteArrayInputStream;

@Slf4j
public class AllureScreenshotListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        Object testInstance = result.getInstance();

        if (testInstance instanceof BaseTest) {
            Page page = ((BaseTest) testInstance).getPage();

            if (page != null) {
                try {
                    byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
                    Allure.addAttachment(result.getName() + " - screenshot",
                            "image/png",
                            new ByteArrayInputStream(screenshot),
                            ".png");
                    log.info("📸 Screenshot captured for failed test: {}", result.getName());
                } catch (Exception e) {
                    log.error("❌ Failed to capture screenshot for test: {}", result.getName(), e);
                }
            }
        }
    }
}
