package com.mobelite.e2e.extension;

import com.microsoft.playwright.Page;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.*;
import java.io.ByteArrayInputStream;

import java.util.function.Supplier;

public class ScreenshotOnFailureExtension implements TestWatcher {
    private final Supplier<Page> pageSupplier;

    public ScreenshotOnFailureExtension(Supplier<Page> pageSupplier) {
        this.pageSupplier = pageSupplier;
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        Page page = pageSupplier.get();
        if (page != null) {
            byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
            Allure.addAttachment(
                    "Failure - " + context.getDisplayName(),
                    new ByteArrayInputStream(screenshot)
            );
        }
    }
}