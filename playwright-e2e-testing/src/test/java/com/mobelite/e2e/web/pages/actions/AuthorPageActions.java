package com.mobelite.e2e.web.pages.actions;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.mobelite.e2e.shared.constants.AuthorTestIds;
import com.mobelite.e2e.web.pages.locators.AuthorPageLocators;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthorPageActions {

    private final Page page;

    public AuthorPageActions(Page page) {
        this.page = page;
    }

    public void waitForTableToLoad() {
        page.locator(AuthorPageLocators.TABLE)
                .waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(10000));
    }

    public void clickAddAuthor() {
        page.locator(AuthorPageLocators.ADD_AUTHOR_BUTTON).click();
    }

    public void fillAuthorForm(String name, String birthDate, String nationality) {
        page.locator(AuthorPageLocators.NAME_INPUT).fill(name);
        if (birthDate != null && !birthDate.isEmpty()) {
            page.locator(AuthorPageLocators.BIRTH_DATE_INPUT).fill(birthDate);
        }
        if (nationality != null && !nationality.isEmpty()) {
            page.locator(AuthorPageLocators.NATIONALITY_INPUT).fill(nationality);
        }
    }

    public void clickSave() {
        Locator btn = page.locator(AuthorPageLocators.SAVE_BUTTON);
        if (btn.count() > 0) btn.click();
        else log.warn("Save button not found!");
    }

    public void clickCancel() {
        page.locator(AuthorPageLocators.CANCEL_BUTTON).click();
    }

    public void clickDeleteForAuthor(String authorName) {
        page.locator(AuthorPageLocators.rowByAuthorName(authorName))
                .locator("[data-testid='" + AuthorTestIds.AUTHORS.AUTHOR_DELETE_BUTTON + "']").click();
    }

    public void confirmDelete() {
        page.locator(AuthorPageLocators.DELETE_CONFIRM).locator("button[label='Confirm']").click();
    }

    public void waitForDialogToClose() {
        page.locator(AuthorPageLocators.DIALOG)
                .waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(5000));
    }

}