package com.mobelite.publisher.ui.tests;

import com.mobelite.publisher.ui.base.BaseTest;
import com.mobelite.publisher.ui.factory.AuthorFactory;
import com.mobelite.publisher.ui.models.AuthorRequest;
import com.mobelite.publisher.ui.pages.AuthorPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static com.mobelite.publisher.ui.constants.PagesNavigate.AUTHORS_NAVIGATE;

public class AuthorUiTest extends BaseTest {

    private AuthorPage authorPage;

    @BeforeMethod
    public void initPage() {
        navigateTo(AUTHORS_NAVIGATE);// open authors page
        authorPage = new AuthorPage(page);
    }

    @Test
    void loadAuthorsTable() {
        // Wait for the table to appear
        authorPage.waitForTableToLoad();

        // Verify table visibility
        Assert.assertTrue(authorPage.isTableVisible(), "Authors table should be visible");

        // Verify table headers
        List<String> headers = authorPage.getTableHeaders();
        Assert.assertEquals(headers.size(), 6, "Table should have 6 columns");
        Assert.assertEquals(headers.get(0), "Name");
        Assert.assertEquals(headers.get(1), "Birth Date");
        Assert.assertEquals(headers.get(2), "Nationality");
        Assert.assertEquals(headers.get(3), "Books");
        Assert.assertEquals(headers.get(4), "Magazines");

        // Verify paginator is visible
        Assert.assertTrue(authorPage.isPaginatorVisible(), "Paginator should be visible");
    }

    @Test
    void addNewAuthor() {
        AuthorRequest request = AuthorFactory.createValidAuthorRequest();
        String name = request.getName();
        String birthDate = request.getBirthDate() != null ? request.getBirthDate().toString() : "";
        String nationality = request.getNationality();
        authorPage.openAddAuthorDialog();
        authorPage.fillAuthorForm(name, birthDate, nationality);
        authorPage.saveAuthor();

        Assert.assertTrue(authorPage.isAuthorInTable(name ,birthDate, nationality));
    }

    @Test
    void deleteAuthor() {
        authorPage.deleteAuthor("John Doe");

    }

    @Test
    void validateFormErrors() {
        authorPage.openAddAuthorDialog();
        authorPage.saveAuthor(); // save without filling

        Assert.assertTrue(authorPage.isNameRequiredErrorVisible(),
                "Name required error should be visible");
    }
}