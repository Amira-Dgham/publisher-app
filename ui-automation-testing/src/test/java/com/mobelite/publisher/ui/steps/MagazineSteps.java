package com.mobelite.publisher.ui.steps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mobelite.factory.PlaywrightFactory;
import com.mobelite.models.Magazine;
import com.mobelite.models.request.MagazineRequest;
import com.mobelite.models.response.ApiResponse;
import com.mobelite.models.response.AuthorDataWrapper;
import com.mobelite.models.response.MagazineDataWrapper;
import com.mobelite.publisher.ui.hooks.Hooks;
import com.mobelite.publisher.ui.pages.MagazinePage;
import com.mobelite.publisher.ui.test_data.MagazineTestDataFactory;
import com.mobelite.utils.ApiUtils;
import io.cucumber.java.After;
import io.cucumber.java.en.*;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

import static com.mobelite.publisher.ui.constants.ApiEndpoints.AUTHORS_BASE;
import static com.mobelite.publisher.ui.constants.ApiEndpoints.MAGAZINES_BASE;

public class MagazineSteps {

    private final MagazinePage magazinePage= new MagazinePage(PlaywrightFactory.getPage());;
    private final List<String> createdMagazines = new ArrayList<>();
    private String magazineTitle;
    List<Long> authors = List.of((long) Hooks.getAuthorId().intValue());
    TypeReference<ApiResponse<MagazineDataWrapper>> typeRef = new TypeReference<>() {};
    private final ApiUtils apiUtils = new ApiUtils(PlaywrightFactory.getApiRequestContext());
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

    @And("I confirm delete of the magazine")
    public void iConfirmDelete() {
        magazinePage.confirmDelete();
        magazinePage.waitForTableToLoad();
        createdMagazines.remove(magazineTitle);
    }

    @Then("the magazine should not be visible in the table")
    public void theMagazineShouldNotBeVisibleInTheTable() {
        Assert.assertFalse(magazinePage.isMagazineInTable(magazineTitle), "Magazine should not be visible in table");
    }

    // --- Validation ---
    @And("I open the add magazine dialog")
    public void iOpenTheAddMagazineDialog() {
        magazinePage.openAddMagazineDialog();
    }

    @When("I save the magazine without filling fields")
    public void iSaveTheMagazineWithoutFillingFields() {
        magazinePage.saveMagazine();
    }

    @Then("I should see required errors")
    public void iShouldSeeRequiredErrors() {
        Assert.assertTrue(magazinePage.isTitleRequiredErrorVisible(), "Title required error should be visible");
        Assert.assertTrue(magazinePage.isIssueNumberRequiredErrorVisible(), "Issue number required error should be visible");
        Assert.assertTrue(magazinePage.isPublicationDateRequiredErrorVisible(), "Publication date required error should be visible");
    }
    @Given("there are more magazines than fit on one page")
    public void thereAreMoreMagazinesThanFitOnOnePage() {
        // Ensure enough magazines exist (e.g., > page size)
        for (int i = 0; i < 11; i++) {
            MagazineRequest request = MagazineTestDataFactory.createValidMagazineRequest(authors);

            // Debug: print authors for this magazine
            System.out.printf("[%s] Creating magazine '%s' with authors: %s%n",
                    java.time.LocalTime.now(), request.getTitle(), request.getAuthorIds());

            magazinePage.openAddMagazineDialog();
            magazinePage.fillMagazineForm(
                    request.getTitle(),
                    request.getIssueNumber(),
                    request.getPublicationDate(),
                    request.getAuthorIds()
            );
            magazinePage.saveMagazine();
            magazinePage.waitForDialogToClose();
            createdMagazines.add(request.getTitle());
        }
        Assert.assertTrue(magazinePage.isPaginatorVisible(), "Paginator should be visible");
    }

    @When("the user clicks to go to the next page")
    public void theUserClicksToGoToTheNextPage() {
        magazinePage.goToNextPage();
    }

    @Then("the next set of magazines should be displayed")
    public void theNextSetOfMagazinesShouldBeDisplayed() {
        Assert.assertTrue(magazinePage.isOnPage(2), "Should be on page 2 of magazines");
    }

    // --- Cleanup using API ---
    @After("@regression or @pagination or @smoke")
    public void cleanupCreatedMagazines() {
        for (String name : new ArrayList<>(createdMagazines)) {
            try {
                Long magazineId = apiUtils.getIdByName(
                        MAGAZINES_BASE,
                        name,
                        typeRef,
                        MagazineDataWrapper::getContent,
                        Magazine::getId
                );
                if (magazineId != null) {
                    apiUtils.deleteById(MAGAZINES_BASE, magazineId);
                }
            } catch (Exception e) {
                System.err.println("Failed to delete author via API: " + name + " - " + e.getMessage());
            }
        }
        createdMagazines.clear();
    }
}