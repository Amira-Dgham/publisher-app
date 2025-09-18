package com.mobelite.publisher.ui.steps;

import com.mobelite.config.ConfigManager;
import com.mobelite.factory.PlaywrightFactory;
import com.mobelite.models.request.AuthorRequest;
import com.mobelite.publisher.ui.pages.AuthorPage;
import com.mobelite.publisher.ui.test_data.AuthorTestDataFactory;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

import static com.mobelite.publisher.ui.constants.PagesNavigate.AUTHORS_NAVIGATE;
public class AuthorSteps {

    private AuthorPage authorPage;
    private List<String> createdAuthors = new ArrayList<>();

    private void ensureAuthorPage() {
        if (authorPage == null) {
            authorPage = new AuthorPage(PlaywrightFactory.getPage());
            PlaywrightFactory.getPage().navigate(ConfigManager.getInstance().getUiBaseUrl() + AUTHORS_NAVIGATE);
        }
    }

    @Given("I navigate to the authors page")
    public void iNavigateToTheAuthorsPage() {
        ensureAuthorPage();
    }

    @Then("I should see the authors table with {int} columns")
    public void iShouldSeeTheAuthorsTableWithColumns(int columns) {
        ensureAuthorPage();
        authorPage.waitForTableToLoad();
        Assert.assertTrue(authorPage.isTableVisible());
        Assert.assertEquals(authorPage.getTableHeaders().size(), columns);
    }

    @Given("I open the add author dialog")
    public void iOpenTheAddAuthorDialog() {
        ensureAuthorPage();
        authorPage.openAddAuthorDialog();
    }

    @When("I fill the author form with name {string}, birthDate {string}, nationality {string}")
    public void iFillTheAuthorFormWithNameBirthDateNationality(String name, String birthDate, String nationality) {
        ensureAuthorPage();
        authorPage.fillAuthorForm(name, birthDate, nationality);
    }

    @And("I save the author")
    public void iSaveTheAuthor() {
        ensureAuthorPage();
        authorPage.saveAuthor();
        authorPage.waitForDialogToClose();
    }

    @Then("I should see the author {string} in the table")
    public void iShouldSeeTheAuthorInTheTable(String name) {
        ensureAuthorPage();
        authorPage.waitForTableToLoad();
        Assert.assertTrue(authorPage.isAuthorInTable(name, "", ""));
        createdAuthors.add(name);
    }

    @Given("an author {string} exists")
    public void anAuthorExists(String name) {
        ensureAuthorPage();
        if (!authorPage.isAuthorInTable(name, "", "")) {
            AuthorRequest request = AuthorTestDataFactory.createValidAuthorRequest();
            request.setName(name);
            String birthDate = request.getBirthDate() != null ? request.getBirthDate().toString() : "";
            String nationality = request.getNationality();

            authorPage.openAddAuthorDialog();
            authorPage.fillAuthorForm(name, birthDate, nationality);
            authorPage.saveAuthor();
            authorPage.waitForDialogToClose();
            authorPage.waitForTableToLoad();
            createdAuthors.add(name);
        }
    }

    @When("I select to delete the author {string}")
    public void iSelectToDeleteTheAuthor(String name) {
        ensureAuthorPage();
        authorPage.deleteAuthor(name);
    }

    @And("I confirm delete")
    public void iConfirmDelete() {
        ensureAuthorPage();
        authorPage.confirmDelete();
        authorPage.waitForDialogToClose();
        authorPage.waitForTableToLoad();
    }

    @Then("the author {string} should not be visible in the table")
    public void theAuthorShouldNotBeVisibleInTheTable(String name) {
        ensureAuthorPage();
        Assert.assertFalse(authorPage.isAuthorInTable(name, "", ""));
        createdAuthors.remove(name);
    }
}