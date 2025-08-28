package com.mobelite.e2e.web.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.mobelite.e2e.shared.constants.TestIds;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class AuthorPage {

    private final Page page;

    public AuthorPage(Page page) {
        this.page = page;
    }

    // ============================
    // LOCATORS
    // ============================

    public Locator table() {
        return page.locator("[data-testid='" + TestIds.AUTHORS.AUTHORS_TABLE + "']");
    }

    public Locator tableHeader() {
        return page.locator("[data-testid='" + TestIds.AUTHORS.AUTHORS_TABLE_HEADER + "']");
    }

    public Locator tableRows() {
        // use tr with data-testid instead of tbody
        return page.locator("tr[data-testid='" + TestIds.AUTHORS.AUTHORS_TABLE_ROW + "']");
    }

    public Locator addAuthorButton() {
        return page.locator("[data-testid='" + TestIds.AUTHORS.ADD_AUTHOR_BUTTON + "']");
    }

    public Locator paginator() {
        return page.locator("[data-testid='" + TestIds.AUTHORS.AUTHORS_PAGINATOR + "']");
    }

    public Locator dialog() {
        return page.locator("[data-testid='" + TestIds.AUTHORS.AUTHOR_DIALOG + "']");
    }

    public Locator nameInput() {
        return page.locator("[data-testid='" + TestIds.AUTHORS.AUTHOR_NAME_INPUT + "']");
    }

    public Locator birthDateInput() {
        return page.locator("[data-testid='" + TestIds.AUTHORS.AUTHOR_BIRTH_DATE_INPUT + "']");
    }

    public Locator nationalityInput() {
        return page.locator("[data-testid='" + TestIds.AUTHORS.AUTHOR_NATIONALITY_INPUT + "']");
    }

    public Locator saveButton() {
        return page.locator("[data-testid='" + TestIds.AUTHORS.AUTHOR_SAVE_BUTTON + "']");
    }

    public Locator cancelButton() {
        return page.locator("[data-testid='" + TestIds.AUTHORS.AUTHOR_CANCEL_BUTTON + "']");
    }

    public Locator nameRequiredError() {
        return page.locator("[data-testid='" + TestIds.AUTHORS.AUTHOR_NAME_REQUIRED_ERROR + "']");
    }

    public Locator nameMinLengthError() {
        return page.locator("[data-testid='" + TestIds.AUTHORS.AUTHOR_NAME_MIN_LENGTH_ERROR + "']");
    }

    public Locator nameMaxLengthError() {
        return page.locator("[data-testid='" + TestIds.AUTHORS.AUTHOR_NAME_MAX_LENGTH_ERROR + "']");
    }

    public Locator nationalityMaxLengthError() {
        return page.locator("[data-testid='" + TestIds.AUTHORS.AUTHOR_NATIONALITY_MAX_LENGTH_ERROR + "']");
    }

    public Locator deleteConfirm() {
        return page.locator("[data-testid='" + TestIds.AUTHORS.AUTHOR_DELETE_CONFIRM + "']");
    }

    // ============================
    // SMART ROW LOCATORS
    // ============================

    private Locator rowByAuthorName(String authorName) {
        return tableRows().filter(new Locator.FilterOptions().setHasText(authorName));
    }

    public Locator editButtonForAuthor(String authorName) {
        return rowByAuthorName(authorName)
                .locator("[data-testid='" + TestIds.AUTHORS.AUTHOR_EDIT_BUTTON + "']");
    }

    public Locator deleteButtonForAuthor(String authorName) {
        return rowByAuthorName(authorName)
                .locator("[data-testid='" + TestIds.AUTHORS.AUTHOR_DELETE_BUTTON + "']");
    }

    // ============================
    // METHODS
    // ============================

    public void waitForTableToLoad() {
        table().waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
    }

    public boolean isTableVisible() {
        return table().isVisible();
    }

    public List<String> getTableHeaders() {
        List<String> headers = new ArrayList<>();
        Locator headersLocator = page.locator("p-table .p-datatable-table thead tr th");
        long count = headersLocator.count();

        log.info("Found {} table headers", count);

        for (int i = 0; i < count; i++) {
            headers.add(headersLocator.nth(i).innerText().trim());
        }

        log.info("Headers: {}", headers);
        return headers;
    }

    public boolean isPaginatorVisible() {
        return paginator().isVisible();
    }

    public void clickAddAuthorButton() {
        addAuthorButton().click();
    }

    public void fillName(String name) {
        nameInput().fill(name);
    }

    public void fillBirthDate(String date) {
        if (date != null && !date.isEmpty()) {
            birthDateInput().fill(date);
        }
    }

    public void fillNationality(String nationality) {
        if (nationality != null && !nationality.isEmpty()) {
            nationalityInput().fill(nationality);
        }
    }

    public void clickSaveButton() {
        saveButton().click();
    }

    public void clickCancelButton() {
        cancelButton().click();
    }

    public void waitForDialogToClose() {
        dialog().waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.HIDDEN)
                .setTimeout(5000));
    }

    public boolean isAuthorInTable(String name, String birthDate, String nationality) {
        Locator row = rowByAuthorName(name);
        if (!row.isVisible()) {
            log.warn("Author row not found: {}", name);
            return false;
        }
        if (birthDate != null && !birthDate.isEmpty()) {
            assertEquals(birthDate, row.locator("td").nth(1).innerText());
        }
        if (nationality != null && !nationality.isEmpty()) {
            assertEquals(nationality, row.locator("td").nth(2).innerText());
        }
        return true;
    }

    public void verifyTableRowCount(long expectedCount) {
        long actualCount = tableRows().count();

        log.info("amira html size"+actualCount);

        assertEquals(expectedCount, actualCount, "Table row count does not match expected");
    }

    public boolean isNameRequiredErrorVisible() {
        return nameRequiredError().isVisible();
    }

    public boolean isNameMinLengthErrorVisible() {
        return nameMinLengthError().isVisible();
    }

    public boolean isNameMaxLengthErrorVisible() {
        return nameMaxLengthError().isVisible();
    }

    public boolean isNationalityMaxLengthErrorVisible() {
        return nationalityMaxLengthError().isVisible();
    }

    public void clickEditForAuthor(String authorName) {
        editButtonForAuthor(authorName).click();
    }

    public void clickDeleteForAuthor(String authorName) {
        deleteButtonForAuthor(authorName).click();
    }

    public void confirmDelete() {
        deleteConfirm().locator("button[label='Confirm']").click();
    }
}