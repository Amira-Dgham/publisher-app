package com.mobelite.e2e.web.tests;

import com.mobelite.e2e.api.endpoints.AuthorApiEndPoint;
import com.mobelite.e2e.api.fixtures.AuthorFixtures;
import com.mobelite.e2e.api.models.Author;
import com.mobelite.e2e.api.models.request.AuthorRequest;
import com.mobelite.e2e.config.BaseTest;
import com.mobelite.e2e.web.pages.actions.AuthorPageActions;
import com.mobelite.e2e.web.pages.assertions.AuthorPageAssertions;
import io.qameta.allure.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import java.util.List;
import static com.mobelite.e2e.shared.constants.ApiEndpoints.AUTHOR_BY_ID;
import static com.mobelite.e2e.shared.constants.HttpStatusCodes.STATUS_NOT_FOUND;
import static com.mobelite.e2e.shared.constants.PagesNavigate.AUTHORS_NAVIGATE;
import static org.junit.jupiter.api.Assertions.*;

@Epic("Author Management")
@Feature("Author UI")
@Story("E2E Testing")
@DisplayName("Author UI E2E Tests")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Slf4j
public class AuthorsUiE2ETest extends BaseTest {

    private final AuthorApiEndPoint authorApi = new AuthorApiEndPoint();
    private final AuthorFixtures authorFixtures = new AuthorFixtures();

    private AuthorPageActions actions;
    private AuthorPageAssertions asserts;

    @BeforeEach
    void setup() {
        authorApi.init(api);
        navigateTo(AUTHORS_NAVIGATE);
        actions = new AuthorPageActions(page);
        asserts = new AuthorPageAssertions(page);
    }

    @AfterEach
    void tearDown() {
        authorApi.cleanUpEach(AUTHOR_BY_ID);
    }

    @Test
    @DisplayName("Load authors table and verify pagination")
    void loadAuthorsTable() {
        actions.waitForTableToLoad();
        assertTrue(asserts.isTableVisible());
        List<String> headers = asserts.getTableHeaders();
        assertEquals(6, headers.size());
        assertEquals("Name", headers.get(0));
        assertEquals("Birth Date", headers.get(1));
        assertEquals("Nationality", headers.get(2));
        assertEquals("Books", headers.get(3));
        assertEquals("Magazines", headers.get(4));
        assertTrue(asserts.isPaginatorVisible());

        var pageData = authorApi.getAllAuthors();
        asserts.verifyTableRowCount(pageData.getContent().size());
        log.info("Authors table loaded successfully with pagination");
    }

    @Test
    @DisplayName("Add new author via UI and verify in table")
    void addNewAuthor() {
        AuthorRequest request = authorFixtures.createValidAuthorRequest();
        String name = request.getName();
        String birthDate = request.getBirthDate() != null ? request.getBirthDate().toString() : "";
        String nationality = request.getNationality();

        actions.clickAddAuthor();
        actions.fillAuthorForm(name, birthDate, nationality);
        actions.clickSave();
        actions.waitForDialogToClose();
        actions.waitForTableToLoad();

        assertTrue(asserts.isAuthorInTable(name, birthDate, nationality));

        Author created = authorApi.getByName(name);
        assertNotNull(created);
        assertEquals(name, created.getName());
        assertEquals(birthDate, created.getBirthDate() != null ? created.getBirthDate().toString() : "");
        assertEquals(nationality, created.getNationality());

        log.info("New author added successfully via UI: {}", name);
    }

    @Test
    @DisplayName("Delete author via UI")
    void deleteAuthor() {
        AuthorRequest request = authorFixtures.createValidAuthorRequest();
        Author created = authorApi.createAuthor(request, false);

        page.reload();
        actions.waitForTableToLoad();

        actions.clickDeleteForAuthor(created.getName());
        actions.confirmDelete();
        actions.waitForTableToLoad();
        actions.waitForTableToLoad();

        assertFalse(asserts.isAuthorInTable(created.getName(), null, null));
        authorApi.getNonExistentAuthor(created.getId(), STATUS_NOT_FOUND);
        log.info("Author deleted successfully via UI: {}", created.getName());
    }

    @Test
    @DisplayName("Validate form errors on invalid input")
    void validateFormErrors() {
        actions.clickAddAuthor();
        actions.clickSave();
        assertTrue(asserts.isNameRequiredErrorVisible());

        actions.fillAuthorForm("A", null, null);
        actions.clickSave();
        assertTrue(asserts.isNameMinLengthErrorVisible());

        actions.clickCancel();
        log.info("Form validation errors tested successfully");
    }
}