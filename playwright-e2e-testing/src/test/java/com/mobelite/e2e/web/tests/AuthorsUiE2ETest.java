package com.mobelite.e2e.web.tests;

import com.mobelite.e2e.api.endpoints.AuthorApiEndPoint;
import com.mobelite.e2e.api.fixtures.AuthorFixtures;
import com.mobelite.e2e.api.models.Author;
import com.mobelite.e2e.api.models.request.AuthorRequest;
import com.mobelite.e2e.config.BaseTest;
import com.mobelite.e2e.web.pages.AuthorPage;
import com.microsoft.playwright.APIResponse;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.util.List;

import static com.mobelite.e2e.shared.constants.ApiEndpoints.AUTHORS_BASE;
import static com.mobelite.e2e.shared.constants.ApiEndpoints.AUTHOR_BY_ID;
import static org.junit.jupiter.api.Assertions.*;

@Epic("Author Management")
@Feature("Author UI")
@Story("E2E Testing")
@DisplayName("Author UI E2E Tests")
@Slf4j
public class AuthorsUiE2ETest extends BaseTest {

    private final AuthorPage authorsPage= new AuthorPage(page);
    private final AuthorApiEndPoint authorApi= new AuthorApiEndPoint();;
    private final AuthorFixtures authorFixtures = new AuthorFixtures();;

    @BeforeEach
    void setup() {
        authorApi.init(api); // Initialize ApiClient and shared entity
        navigateTo("/authors");
    }

    @AfterEach
    void tearDown() {
        takeScreenshot("AuthorFormTest");
        authorApi.cleanUpEach(AUTHOR_BY_ID);

    }


    @Test
    @DisplayName("Load authors table and verify pagination")
    void loadAuthorsTable() {
        authorsPage.waitForTableToLoad();

        assertTrue(authorsPage.isTableVisible());
        List<String> headers = authorsPage.getTableHeaders();
        assertEquals(6, headers.size());
        assertEquals("Name", headers.get(0));
        assertEquals("Birth Date", headers.get(1));
        assertEquals("Nationality", headers.get(2));
        assertEquals("Books", headers.get(3));
        assertEquals("Magazines", headers.get(4));

        assertTrue(authorsPage.isPaginatorVisible());

        var pageData = authorApi.getAllAndValidate(AUTHORS_BASE);
        log.info("mira api size"+pageData.getContent().size());
        authorsPage.verifyTableRowCount(pageData.getContent().size());

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
    @DisplayName("Delete author via UI")
    void deleteAuthor() {
        AuthorRequest request = authorFixtures.createValidAuthorRequest();
        Author created = authorApi.createAndValidate(request, AUTHORS_BASE);

        page.reload();
        authorsPage.waitForTableToLoad();

        authorsPage.clickDeleteForAuthor(created.getName());
        authorsPage.confirmDelete();

        authorsPage.waitForTableToLoad();
        assertFalse(authorsPage.isAuthorInTable(created.getName(), null, null));

        APIResponse response = api.get(AUTHORS_BASE + "/" + created.getId());
        assertEquals(404, response.status(), "Author should not exist");

        log.info("Author deleted successfully via UI: {}", created.getName());
    }

    @Test
    @DisplayName("Validate form errors on invalid input")
    void validateFormErrors() {
        authorsPage.clickAddAuthorButton();

        authorsPage.clickSaveButton();
        assertTrue(authorsPage.isNameRequiredErrorVisible());

        authorsPage.fillName("A");
        authorsPage.clickSaveButton();
        assertTrue(authorsPage.isNameMinLengthErrorVisible());

        authorsPage.clickCancelButton();

        log.info("Form validation errors tested successfully");
    }
}