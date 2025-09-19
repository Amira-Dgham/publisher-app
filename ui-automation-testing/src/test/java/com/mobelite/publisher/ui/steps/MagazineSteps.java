package com.mobelite.publisher.ui.steps;

import com.mobelite.factory.PlaywrightFactory;
import com.mobelite.models.Author;
import com.mobelite.models.Magazine;
import com.mobelite.models.request.AuthorRequest;
import com.mobelite.models.request.MagazineRequest;
import com.mobelite.publisher.ui.pages.MagazinePage;
import com.mobelite.publisher.ui.test_data.AuthorTestDataFactory;
import com.mobelite.publisher.ui.test_data.MagazineTestDataFactory;
import com.mobelite.utils.ApiUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.testng.Assert;
import com.mobelite.models.response.ApiResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.mobelite.publisher.ui.constants.ApiEndpoints.AUTHORS_BASE;

public class MagazineSteps {

    private final MagazinePage magazinePage= new MagazinePage(PlaywrightFactory.getPage());;
    private final List<String> createdMagazines = new ArrayList<>();
    private String magazineTitle;
    private static Long authorId;
    private final ApiUtils apiUtils = new ApiUtils(PlaywrightFactory.getApiRequestContext());;

    // This hook runs after Playwright context is ready
    @Before(order = 1) // order > Hooks.beforeScenario() which is usually order = 0
    public void setupScenario() {
        // Create author if not yet created
        if (authorId == null) {
            AuthorRequest authorRequest = AuthorTestDataFactory.createValidAuthorRequest();
            ApiResponse<Author> response = apiUtils.post(
                    AUTHORS_BASE,
                    authorRequest,
                    new TypeReference<ApiResponse<Author>>() {}
            );
            authorId = response.getData().getId();
            System.out.println("Created author with ID: " + authorId);
        }
    }
    // --- Navigation & Table ---
    @Given("I navigate to the magazines page")
    public void iNavigateToTheMagazinesPage() {
        magazinePage.navigateToMagazinesPage();
    }

    @Then("I should see the magazines table with {int} columns")
    public void iShouldSeeTheMagazinesTableWithColumns(int columns) {
        magazinePage.waitForTableToLoad();
        Assert.assertTrue(magazinePage.isTableVisible(), "Magazines table should be visible");
        Assert.assertEquals(magazinePage.getTableHeaders().size(), columns, "Table column count mismatch");
    }

    // --- Magazine Creation ---
    @Given("a magazine is created using the data factory")
    public void aMagazineIsCreatedUsingTheDataFactory() {
        List<Integer> authors = List.of(authorId.intValue());
        MagazineRequest request = MagazineTestDataFactory.createValidMagazineRequest(authors);

        magazineTitle = request.getTitle();
        magazinePage.openAddMagazineDialog();
        magazinePage.fillMagazineForm(request.getTitle(), request.getIssueNumber(), request.getPublicationDate(), request.getAuthorIds());
        magazinePage.saveMagazine();
        magazinePage.waitForDialogToClose();
        createdMagazines.add(magazineTitle);
    }

    @Then("I should see the magazine in the table")
    public void iShouldSeeTheMagazineInTheTable() {
        Assert.assertTrue(magazinePage.isMagazineInTable(magazineTitle), "Magazine should be visible in table");
    }

    // --- Delete Magazine ---
    @When("I select to delete that magazine")
    public void iSelectToDeleteTheMagazine() {
        magazinePage.deleteMagazine(magazineTitle);
    }

    @And("I confirm delete")
    public void iConfirmDelete() {
        magazinePage.confirmDelete();
        magazinePage.waitForTableToLoad();
        createdMagazines.remove(magazineTitle);
    }

    @Then("the magazine should not be visible in the table")
    public void theMagazineShouldNotBeVisibleInTheTable() {
        Assert.assertFalse(magazinePage.isMagazineInTable(magazineTitle), "Magazine should not be visible in table");
    }

    // --- Cleanup ---
//    @After("@regression or @smoke")
//    public void cleanupCreatedMagazines() {
//        for (String title : new ArrayList<>(createdMagazines)) {
//            try {
//                magazinePage.deleteMagazine(title);
//            } catch (Exception e) {
//                System.err.println("Failed to delete magazine: " + title);
//            }
//        }
//        createdMagazines.clear();
//    }
}