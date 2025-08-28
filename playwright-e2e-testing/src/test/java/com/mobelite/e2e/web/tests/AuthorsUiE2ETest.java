package com.mobelite.e2e.web.tests;

import com.mobelite.e2e.api.endpoints.AuthorApiEndPoint;
import com.mobelite.e2e.api.fixtures.AuthorFixtures;
import com.mobelite.e2e.api.models.Author;
import com.mobelite.e2e.api.models.request.AuthorRequest;
import com.mobelite.e2e.config.BaseTest;
import com.mobelite.e2e.web.pages.AuthorPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Epic("Author Management")
@Feature("Author UI")
@Story("E2E Testing")
@DisplayName("Author UI E2E Tests")
@Slf4j
public class AuthorsUiE2ETest extends BaseTest {

    private AuthorPage authorsPage;
    private AuthorApiEndPoint authorApi;
    private AuthorFixtures authorFixtures;

    @BeforeEach
    void setup() {
        navigateTo("/authors");
        authorsPage = new AuthorPage(page);
        authorApi = new AuthorApiEndPoint();
        authorApi.init(); // Initialize ApiClient and shared entity
        authorFixtures = new AuthorFixtures();
    }

    @AfterEach
    void cleanup() {
        authorApi.cleanUpEach();
        authorApi.tearDown();
    }

    @Test
    @DisplayName("Load authors table and verify pagination")
    void loadAuthorsTable() {
        authorsPage.waitForTableToLoad();

        assertTrue(authorsPage.isTableVisible());
        List<String> headers = authorsPage.getTableHeaders();
        assertEquals(5, headers.size());
        assertEquals("Name", headers.get(0));
        assertEquals("Birth Date", headers.get(1));
        assertEquals("Nationality", headers.get(2));
        assertEquals("Books", headers.get(3));
        assertEquals("Magazines", headers.get(4));

        assertTrue(authorsPage.isPaginatorVisible());

        var pageData = authorApi.getAllAndValidate(authorApi.getBaseEndpoint());
        assertTrue(pageData.hasContent());
        authorsPage.verifyTableRowCount(pageData.getTotalElements());

        log.info("Authors table loaded successfully with pagination");
    }

    @Test
    @DisplayName("Add new author via UI and verify in table")
    void addNewAuthor() {
        AuthorRequest request = authorFixtures.createValidAuthorRequest();
        String name = request.getName();
        String birthDate = request.getBirthDate() != null ? request.getBirthDate().toString() : "";
        String nationality = request.getNationality();

        authorsPage.clickAddAuthorButton();
        authorsPage.fillName(name);
        authorsPage.fillBirthDate(birthDate);
        authorsPage.fillNationality(nationality);
        authorsPage.clickSaveButton();

        authorsPage.waitForDialogToClose();
        authorsPage.waitForTableToLoad();

        assertTrue(authorsPage.isAuthorInTable(name, birthDate, nationality));

        Author created = authorApi.getByName(name);
        assertNotNull(created);
        assertEquals(name, created.getName());
        assertEquals(birthDate, created.getBirthDate() != null ? created.getBirthDate().toString() : "");
        assertEquals(nationality, created.getNationality());

        authorApi.trackForCleanup(created.getId());

        log.info("New author added successfully via UI: {}", name);
    }

    @Test
    @DisplayName("Edit existing author via UI")
    void editAuthor() {
        AuthorRequest request = authorFixtures.createValidAuthorRequest();
        Author created = authorApi.createAndValidate(request, authorApi.getBaseEndpoint());
        authorApi.trackForCleanup(created.getId());

        page.reload();
        authorsPage.waitForTableToLoad();

        authorsPage.clickEditForAuthor(created.getName());

        AuthorRequest updatedRequest = authorFixtures.createValidAuthorRequest();
        String updatedName = updatedRequest.getName();
        authorsPage.fillName(updatedName);
        authorsPage.clickSaveButton();

        authorsPage.waitForTableToLoad();
        assertTrue(authorsPage.isAuthorInTable(updatedName, created.getBirthDate() != null ? created.getBirthDate().toString() : "", created.getNationality()));

        Author updatedAuthor = authorApi.getByIdAndValidate(created.getId(), authorApi.getItemByIdEndpoint());
        assertEquals(updatedName, updatedAuthor.getName());

        log.info("Author edited successfully via UI: {}", updatedName);
    }

    @Test
    @DisplayName("Delete author via UI")
    void deleteAuthor() {
        AuthorRequest request = authorFixtures.createValidAuthorRequest();
        Author created = authorApi.createAndValidate(request, authorApi.getBaseEndpoint());
        authorApi.trackForCleanup(created.getId());

        page.reload();
        authorsPage.waitForTableToLoad();

        authorsPage.clickDeleteForAuthor(created.getName());
        authorsPage.confirmDelete();

        authorsPage.waitForTableToLoad();
        assertFalse(authorsPage.isAuthorInTable(created.getName(), null, null));

        APIResponse response = api.get(authorApi.getBaseEndpoint() + "/" + created.getId());
        assertEquals(404, response.status(), "Author should not exist");

        log.info("Author deleted successfully via UI: {}", created.getName());
    }

    @Test
    @DisplayName("Validate form errors on invalid input")
    void validateFormErrors() {
        authorsPage.clickAddAuthorButton();

        authorsPage.clickSaveButton();
        assertTrue(authorsPage.isNameRequiredErrorVisible());
        assertTrue(authorsPage.isNameMinLengthErrorVisible());

        authorsPage.fillName("A");
        authorsPage.clickSaveButton();
        assertTrue(authorsPage.isNameMinLengthErrorVisible());

        authorsPage.fillName("A".repeat(101));
        authorsPage.clickSaveButton();
        assertTrue(authorsPage.isNameMaxLengthErrorVisible());

        authorsPage.fillNationality("N".repeat(51));
        authorsPage.clickSaveButton();
        assertTrue(authorsPage.isNationalityMaxLengthErrorVisible());

        authorsPage.clickCancelButton();

        log.info("Form validation errors tested successfully");
    }
}