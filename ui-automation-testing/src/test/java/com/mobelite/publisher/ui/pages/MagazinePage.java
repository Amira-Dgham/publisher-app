package com.mobelite.publisher.ui.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.mobelite.config.ConfigManager;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.mobelite.publisher.ui.constants.PagesNavigate.MAGAZINES_NAVIGATE;

@Slf4j
public class MagazinePage {

    private final Page page;

    // --- Selectors ---
    private static final String TABLE = "[data-testid='magazines-table']";
    private static final String TABLE_ROW = "tr[data-testid='magazines-table-row']";
    private static final String ADD_BUTTON = "[data-testid='add-magazine-button']";
    private static final String DIALOG = "[data-testid='magazine-dialog']";
    private static final String TITLE_INPUT = "[data-testid='magazine-title-input']";
    private static final String ISSUE_NUMBER_INPUT = "[data-testid='magazine-issue-number-input']";
    private static final String PUBLICATION_DATE_INPUT = "[data-testid='magazine-publication-date-input']";
    private static final String AUTHOR_IDS_INPUT = "[data-testid='magazine-author-ids-input']";
    private static final String SAVE_BUTTON = "[data-testid='magazine-save-button']";
    private static final String CANCEL_BUTTON = "[data-testid='magazine-cancel-button']";
    private static final String DELETE_CONFIRM = "[data-testid='magazine-delete-confirm']";
    private static final String PAGINATOR = "[data-testid='magazines-paginator']";
    private static final String PAGINATOR_NEXT_BUTTON = "[data-testid='magazines-paginator'] .p-paginator-next";
    private static final String PAGINATOR_PAGE_BUTTONS = "[data-testid='magazines-paginator'] .p-paginator-page";

    private static final String TITLE_REQUIRED_ERROR = "[data-testid='magazine-title-required-error']";
    private static final String ISSUE_NUMBER_REQUIRED_ERROR = "[data-testid='magazine-issue-number-required-error']";
    private static final String PUBLICATION_DATE_REQUIRED_ERROR = "[data-testid='magazine-publication-date-required-error']";

    // --- Core locators ---
    private final Locator table;
    private final Locator addButton;
    private final Locator titleInput;
    private final Locator issueNumberInput;
    private final Locator publicationDateInput;
    private final Locator authorIdsInput;
    private final Locator saveButton;
    private final Locator cancelButton;

    public MagazinePage(Page page) {
        this.page = page;
        this.table = page.locator(TABLE);
        this.addButton = page.locator(ADD_BUTTON);
        this.titleInput = page.locator(TITLE_INPUT);
        this.issueNumberInput = page.locator(ISSUE_NUMBER_INPUT);
        this.publicationDateInput = page.locator(PUBLICATION_DATE_INPUT);
        this.authorIdsInput = page.locator(AUTHOR_IDS_INPUT);
        this.saveButton = page.locator(SAVE_BUTTON);
        this.cancelButton = page.locator(CANCEL_BUTTON);
    }

    // --- Navigation ---
    public void navigateToMagazinesPage() {
        page.navigate(ConfigManager.getInstance().getUiBaseUrl() + MAGAZINES_NAVIGATE);
    }

    public void waitForTableToLoad() {
        table.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
    }

    // --- Dialog ---
    public void openAddMagazineDialog() {
        addButton.click();
    }

    public void fillMagazineForm(String title, int issueNumber, String publicationDate, List<Integer> authorIds) {
        titleInput.fill(title);
        issueNumberInput.fill(String.valueOf(issueNumber));
        publicationDateInput.fill(publicationDate);
        if (authorIds != null && !authorIds.isEmpty()) {
            authorIdsInput.fill(String.join(",", authorIds.stream().map(String::valueOf).toList()));
        }
    }

    public void saveMagazine() {
        saveButton.click();
    }

    public void cancelMagazineDialog() {
        cancelButton.click();
    }

    public void waitForDialogToClose() {
        page.locator(DIALOG).waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(5000));
    }

    // --- Delete ---
    public void deleteMagazine(String title) {
        page.locator(rowByTitle(title))
                .locator("[data-testid='delete-magazine-button']")
                .click();
    }

    public void confirmDelete() {
        page.locator(DELETE_CONFIRM).locator("button[label='Yes']").click(); // adjust label if needed
        page.waitForTimeout(500); // wait for table refresh
    }

    // --- Pagination ---
    public void goToNextPage() {
        page.locator(PAGINATOR_NEXT_BUTTON).click();
        page.waitForTimeout(1000);
    }

    public boolean isOnPage(int pageNumber) {
        Locator pageButtons = page.locator(PAGINATOR_PAGE_BUTTONS);
        long count = pageButtons.count();
        for (int i = 0; i < count; i++) {
            Locator button = pageButtons.nth(i);
            String text = button.textContent().trim();
            String classes = button.getAttribute("class");
            if (text.equals(String.valueOf(pageNumber))) {
                return classes != null && (classes.contains("p-highlight") || classes.contains("p-paginator-page-selected"));
            }
        }
        return false;
    }

    // --- Table checks ---
    public boolean isTableVisible() {
        return table.isVisible();
    }

    public List<String> getTableHeaders() {
        List<String> headers = new ArrayList<>();
        Locator headersLocator = page.locator(TABLE + " thead th");
        long count = headersLocator.count();
        for (int i = 0; i < count; i++) {
            headers.add(headersLocator.nth(i).innerText().trim());
        }
        return headers;
    }

    public long getTableRowCount() {
        return page.locator(TABLE_ROW).count();
    }

    public boolean isPaginatorVisible() {
        return page.locator(PAGINATOR).isVisible();
    }

    public boolean isMagazineInTable(String title) {
        Locator row = page.locator(rowByTitle(title));
        return row.isVisible();
    }

    // --- Validation ---
    public boolean isTitleRequiredErrorVisible() {
        return page.locator(TITLE_REQUIRED_ERROR).isVisible();
    }

    public boolean isIssueNumberRequiredErrorVisible() {
        return page.locator(ISSUE_NUMBER_REQUIRED_ERROR).isVisible();
    }

    public boolean isPublicationDateRequiredErrorVisible() {
        return page.locator(PUBLICATION_DATE_REQUIRED_ERROR).isVisible();
    }

    // --- Helper ---
    private String rowByTitle(String title) {
        return TABLE_ROW + ":has-text('" + title + "')";
    }
}