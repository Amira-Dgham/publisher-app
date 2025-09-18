package com.mobelite.publisher.ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.mobelite.publisher.ui.constants.AuthorTestIds;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class AuthorPage {

    private final Page page;

    // --- Selectors as constants ---
    private static final String TABLE = "[data-testid='" + AuthorTestIds.AUTHORS.AUTHORS_TABLE + "']";
    private static final String TABLE_ROW = "tr[data-testid='" + AuthorTestIds.AUTHORS.AUTHORS_TABLE_ROW + "']";
    private static final String ADD_AUTHOR_BUTTON = "[data-testid='" + AuthorTestIds.AUTHORS.ADD_AUTHOR_BUTTON + "']";
    private static final String PAGINATOR = "[data-testid='" + AuthorTestIds.AUTHORS.AUTHORS_PAGINATOR + "']";
    private static final String DIALOG = "[data-testid='" + AuthorTestIds.AUTHORS.AUTHOR_DIALOG + "']";
    private static final String NAME_INPUT = "[data-testid='" + AuthorTestIds.AUTHORS.AUTHOR_NAME_INPUT + "']";
    private static final String BIRTH_DATE_INPUT = "[data-testid='" + AuthorTestIds.AUTHORS.AUTHOR_BIRTH_DATE_INPUT + "']";
    private static final String NATIONALITY_INPUT = "[data-testid='" + AuthorTestIds.AUTHORS.AUTHOR_NATIONALITY_INPUT + "']";
    private static final String SAVE_BUTTON = "[data-testid='" + AuthorTestIds.AUTHORS.AUTHOR_SAVE_BUTTON + "']";
    private static final String CANCEL_BUTTON = "[data-testid='" + AuthorTestIds.AUTHORS.AUTHOR_CANCEL_BUTTON + "']";
    private static final String DELETE_CONFIRM = "[data-testid='" + AuthorTestIds.AUTHORS.AUTHOR_DELETE_CONFIRM + "']";
    private static final String NAME_REQUIRED_ERROR = "[data-testid='" + AuthorTestIds.AUTHORS.AUTHOR_NAME_REQUIRED_ERROR + "']";
    private static final String NAME_MIN_LENGTH_ERROR = "[data-testid='" + AuthorTestIds.AUTHORS.AUTHOR_NAME_MIN_LENGTH_ERROR + "']";

    // --- Core locators (frequently used) ---
    private final Locator table;
    private final Locator addAuthorButton;
    private final Locator nameInput;
    private final Locator birthDateInput;
    private final Locator nationalityInput;
    private final Locator saveButton;
    private final Locator cancelButton;

    public AuthorPage(Page page) {
        this.page = page;
        this.table = page.locator(TABLE);
        this.addAuthorButton = page.locator(ADD_AUTHOR_BUTTON);
        this.nameInput = page.locator(NAME_INPUT);
        this.birthDateInput = page.locator(BIRTH_DATE_INPUT);
        this.nationalityInput = page.locator(NATIONALITY_INPUT);
        this.saveButton = page.locator(SAVE_BUTTON);
        this.cancelButton = page.locator(CANCEL_BUTTON);
    }

    // --- Actions ---

    public void waitForTableToLoad() {
        table.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10000));
    }

    public void openAddAuthorDialog() {
        addAuthorButton.click();
    }

    public void fillAuthorForm(String name, String birthDate, String nationality) {
        nameInput.fill(name);
        if (birthDate != null && !birthDate.isEmpty()) {
            birthDateInput.fill(birthDate);
        }
        if (nationality != null && !nationality.isEmpty()) {
            nationalityInput.fill(nationality);
        }
    }

    public void saveAuthor() {
        if (saveButton.count() > 0) {
            saveButton.click();
        }
    }

    public void cancelAuthorDialog() {
        cancelButton.click();
    }

    public void deleteAuthor(String authorName) {
        page.locator(rowByAuthorName(authorName))
                .locator("[data-testid='" + AuthorTestIds.AUTHORS.AUTHOR_DELETE_BUTTON + "']")
                .click();
    }

    public void confirmDelete() {
        page.locator(DELETE_CONFIRM).locator("button[label='Confirm']").click();
    }

    public void waitForDialogToClose() {
        page.locator(DIALOG).waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.HIDDEN)
                .setTimeout(5000));
    }

    // --- Queries / Verification helpers (no asserts here) ---

    public boolean isTableVisible() {
        return table.isVisible();
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
        return page.locator(PAGINATOR).isVisible();
    }

    public boolean isAuthorInTable(String name, String birthDate, String nationality) {
        Locator row = page.locator(rowByAuthorName(name));
        if (!row.isVisible()) return false;

        if (birthDate != null && !birthDate.isEmpty()) {
            String actual = row.locator("td").nth(1).innerText();
            if (!birthDate.equals(actual)) return false;
        }
        if (nationality != null && !nationality.isEmpty()) {
            String actual = row.locator("td").nth(2).innerText();
            if (!nationality.equals(actual)) return false;
        }
        return true;
    }

    public long getTableRowCount() {
        return page.locator(TABLE_ROW).count();
    }

    public boolean isNameRequiredErrorVisible() {
        return page.locator(NAME_REQUIRED_ERROR).isVisible();
    }

    public boolean isNameMinLengthErrorVisible() {
        return page.locator(NAME_MIN_LENGTH_ERROR).isVisible();
    }
    public boolean isSaveButtonDisabled() {
        saveButton.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(5000));
        return saveButton.isDisabled();
    }

    // --- Helper ---
    private String rowByAuthorName(String name) {
        return TABLE_ROW + ":has-text('" + name + "')";
    }
}