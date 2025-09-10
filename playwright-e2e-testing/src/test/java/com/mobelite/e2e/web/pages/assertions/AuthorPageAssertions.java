package com.mobelite.e2e.web.pages.assertions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.mobelite.e2e.web.pages.locators.AuthorPageLocators;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AuthorPageAssertions {

    private final Page page;

    public AuthorPageAssertions(Page page) {
        this.page = page;
    }

    public boolean isTableVisible() {
        return page.locator(AuthorPageLocators.TABLE).isVisible();
    }

    public List<String> getTableHeaders() {
        List<String> headers = new ArrayList<>();
        Locator headersLocator = page.locator("p-table .p-datatable-table thead tr th");
        long count = headersLocator.count();
        for (int i = 0; i < count; i++) {
            headers.add(headersLocator.nth(i).innerText().trim());
        }
        return headers;
    }

    public boolean isPaginatorVisible() {
        return page.locator(AuthorPageLocators.PAGINATOR).isVisible();
    }

    public boolean isAuthorInTable(String name, String birthDate, String nationality) {
        Locator row = page.locator(AuthorPageLocators.rowByAuthorName(name));
        if (!row.isVisible()) return false;
        if (birthDate != null && !birthDate.isEmpty()) {
            assertEquals(birthDate, row.locator("td").nth(1).innerText());
        }
        if (nationality != null && !nationality.isEmpty()) {
            assertEquals(nationality, row.locator("td").nth(2).innerText());
        }
        return true;
    }

    public void verifyTableRowCount(long expectedCount) {
        long actualCount = page.locator(AuthorPageLocators.TABLE_ROW).count();
        assertEquals(expectedCount, actualCount, "Table row count does not match expected");
    }

    public boolean isNameRequiredErrorVisible() {
        return page.locator(AuthorPageLocators.NAME_REQUIRED_ERROR).isVisible();
    }

    public boolean isNameMinLengthErrorVisible() {
        return page.locator(AuthorPageLocators.NAME_MIN_LENGTH_ERROR).isVisible();
    }

}