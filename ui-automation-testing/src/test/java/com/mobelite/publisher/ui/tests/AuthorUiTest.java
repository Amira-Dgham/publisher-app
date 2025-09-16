package com.mobelite.publisher.ui.tests;

import com.mobelite.publisher.ui.base.BaseTest;
import com.mobelite.publisher.ui.factory.AuthorFactory;
import com.mobelite.publisher.ui.models.AuthorRequest;
import com.mobelite.publisher.ui.pages.AuthorPage;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static com.mobelite.publisher.ui.constants.PagesNavigate.AUTHORS_NAVIGATE;

public class AuthorUiTest extends BaseTest {

    private AuthorPage authorPage;
    private List<String> createdAuthors = new ArrayList<>();

    @BeforeClass
    public void initPage() {
        navigateTo(AUTHORS_NAVIGATE);// open authors page
        authorPage = new AuthorPage(page);
    }
    @AfterMethod
    public void cleanup() {
        for (String name : createdAuthors) {
            if (authorPage.isAuthorInTable(name, "", "")) {
                authorPage.deleteAuthor(name);
                authorPage.confirmDelete();
            }
        }
        createdAuthors.clear();
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
        authorPage.waitForDialogToClose();
        authorPage.waitForTableToLoad();
        createdAuthors.add(name);
        Assert.assertTrue(authorPage.isAuthorInTable(name ,birthDate, nationality));

    }

    @Test
    void deleteAuthor() {
        AuthorRequest request = AuthorFactory.createValidAuthorRequest();
        String name =  request.getName();
        String birthDate = request.getBirthDate() != null ? request.getBirthDate().toString() : "";
        String nationality = request.getNationality();

        authorPage.openAddAuthorDialog();
        authorPage.fillAuthorForm(name, birthDate, nationality);
        authorPage.saveAuthor();
        authorPage.waitForDialogToClose();
        authorPage.waitForTableToLoad();
        Assert.assertTrue(authorPage.isAuthorInTable(name, birthDate, nationality));

        authorPage.deleteAuthor(name);
        authorPage.confirmDelete();
        authorPage.waitForDialogToClose();
        authorPage.waitForTableToLoad();
        Assert.assertFalse(authorPage.isAuthorInTable(name, birthDate, nationality));
    }

    @Test
    void validateFormErrors() {
        authorPage.openAddAuthorDialog();
        authorPage.saveAuthor();

        Assert.assertTrue(authorPage.isNameRequiredErrorVisible(),
                "Name required error should be visible");
    }
}