package com.mobelite.publisher.ui.steps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mobelite.factory.PlaywrightFactory;
import com.mobelite.models.request.AuthorRequest;
import com.mobelite.models.response.ApiResponse;
import com.mobelite.models.response.AuthorDataWrapper;
import com.mobelite.publisher.ui.pages.AuthorPage;
import com.mobelite.publisher.ui.test_data.AuthorTestDataFactory;
import com.mobelite.utils.ApiUtils;
import io.cucumber.java.After;
import io.cucumber.java.en.*;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

import static com.mobelite.publisher.ui.constants.ApiEndpoints.AUTHORS_BASE;

public class AuthorSteps {

    TypeReference<ApiResponse<AuthorDataWrapper>> typeRef = new TypeReference<>() {};
    private final AuthorPage authorPage = new AuthorPage(PlaywrightFactory.getPage());;
    private final List<String> createdAuthors = new ArrayList<>();
    private String authorName;
    private final ApiUtils apiUtils = new ApiUtils(PlaywrightFactory.getApiRequestContext());

    // --- Navigation & table ---
    @Given("I navigate to the authors page")
    public void iNavigateToTheAuthorsPage() {
        authorPage.navigateToAuthorsPage();
        authorPage.waitForTableToLoad();
    }

    @Then("I should see the authors table with {int} columns")
    public void iShouldSeeTheAuthorsTableWithColumns(int columns) {
        authorPage.waitForTableToLoad();
        Assert.assertTrue(authorPage.isTableVisible(), "Authors table should be visible");
        Assert.assertEquals(authorPage.getTableHeaders().size(), columns, "Table column count mismatch");
    }

    // --- Author creation using data factory ---
    @Given("an author is created using the data factory")
    public void anAuthorIsCreatedUsingTheDataFactory() {
        AuthorRequest request = AuthorTestDataFactory.createValidAuthorRequest();
        authorName = request.getName();
        String birthDate = request.getBirthDate() != null ? request.getBirthDate().toString() : "1990-01-01";
        String nationality = request.getNationality() != null ? request.getNationality() : "US";

        authorPage.openAddAuthorDialog();
        authorPage.fillAuthorForm(authorName, birthDate, nationality);
        authorPage.saveAuthor();
        authorPage.waitForDialogToClose();
        authorPage.waitForTableToLoad();

        createdAuthors.add(authorName);
    }

    @Then("I should see the author in the table")
    public void iShouldSeeTheAuthorInTheTable() {
        Assert.assertTrue(authorPage.isAuthorInTable(authorName, "", ""), "Author should be visible in table");
    }

    // --- Delete author ---
    @When("I select to delete that author")
    public void iSelectToDeleteTheAuthor() {
        authorPage.deleteAuthor(authorName);
    }

    @And("I confirm delete")
    public void iConfirmDelete() {
        authorPage.confirmDelete();
        authorPage.waitForDialogToClose();
        authorPage.waitForTableToLoad();
        createdAuthors.remove(authorName);
    }

    @Then("the author should not be visible in the table")
    public void theAuthorShouldNotBeVisibleInTheTable() {
        Assert.assertFalse(authorPage.isAuthorInTable(authorName, "", ""), "Author should not be visible in table");
    }

    // --- Form validation ---
    @Given("I open the add author dialog")
    public void iOpenTheAddAuthorDialog() {
        authorPage.openAddAuthorDialog();
    }

    @When("I save the author without filling fields")
    public void iSaveTheAuthorWithoutFillingFields() {
        authorPage.saveAuthor();
    }

    @Then("I should see name required error")
    public void iShouldSeeNameRequiredError() {
        Assert.assertTrue(authorPage.isNameRequiredErrorVisible(), "Name required error should be visible");
        authorPage.waitForDialogToClose();
    }

    // --- Pagination scenario ---
    @Given("there are more authors than fit on one page")
    public void thereAreMoreAuthorsThanFitOnOnePage() {
        int pageSize = 10;
        int maxTries = pageSize * 2;
        int attempts = 0;

        while (authorPage.getTableRowCount() < pageSize + 1 && attempts < maxTries) {
            AuthorRequest request = AuthorTestDataFactory.createValidAuthorRequest();
            String name = request.getName();
            String birthDate = request.getBirthDate() != null ? request.getBirthDate().toString() : "1990-01-01";
            String nationality = request.getNationality() != null ? request.getNationality() : "US";

            authorPage.openAddAuthorDialog();
            authorPage.fillAuthorForm(name, birthDate, nationality);
            authorPage.saveAuthor();
            authorPage.waitForDialogToClose();
            authorPage.waitForTableToLoad();

            createdAuthors.add(name);
            attempts++;
        }

        Assert.assertTrue(authorPage.isPaginatorVisible(), "Paginator should be visible after adding enough authors");
    }

    @When("the user clicks to go to the next page")
    public void theUserClicksToGoToTheNextPage() {
        authorPage.goToNextPage();
    }

    @Then("the next set of authors should be displayed")
    public void theNextSetOfAuthorsShouldBeDisplayed() {
        authorPage.waitForTableToLoad();
        Assert.assertTrue(authorPage.isOnPage(2), "The paginator should indicate page 2");
        Assert.assertTrue(authorPage.getTableRowCount() > 0, "Next page should display authors");
    }

    // --- Cleanup using API ---
    @After("@regression or @pagination or @smoke")
    public void cleanupCreatedAuthors() {
        for (String name : new ArrayList<>(createdAuthors)) {
            try {
                Long authorId = apiUtils.getIdByName(
                        AUTHORS_BASE,
                        name,
                        typeRef,
                        AuthorDataWrapper::getContent,
                        author -> author.getId()
                );
                if (authorId != null) {
                    apiUtils.deleteById(AUTHORS_BASE, authorId);
                }
            } catch (Exception e) {
                System.err.println("Failed to delete author via API: " + name + " - " + e.getMessage());
            }
        }
        createdAuthors.clear();
    }
}