package com.mobelite.e2e.web.pages.locators;


import com.mobelite.e2e.shared.constants.AuthorTestIds;

public class AuthorPageLocators {

    public static final String TABLE = "[data-testid='" + AuthorTestIds.AUTHORS.AUTHORS_TABLE + "']";
    public static final String TABLE_ROW = "tr[data-testid='" + AuthorTestIds.AUTHORS.AUTHORS_TABLE_ROW + "']";
    public static final String ADD_AUTHOR_BUTTON = "[data-testid='" + AuthorTestIds.AUTHORS.ADD_AUTHOR_BUTTON + "']";
    public static final String PAGINATOR = "[data-testid='" + AuthorTestIds.AUTHORS.AUTHORS_PAGINATOR + "']";
    public static final String DIALOG = "[data-testid='" + AuthorTestIds.AUTHORS.AUTHOR_DIALOG + "']";
    public static final String NAME_INPUT = "[data-testid='" + AuthorTestIds.AUTHORS.AUTHOR_NAME_INPUT + "']";
    public static final String BIRTH_DATE_INPUT = "[data-testid='" + AuthorTestIds.AUTHORS.AUTHOR_BIRTH_DATE_INPUT + "']";
    public static final String NATIONALITY_INPUT = "[data-testid='" + AuthorTestIds.AUTHORS.AUTHOR_NATIONALITY_INPUT + "']";
    public static final String SAVE_BUTTON = "[data-testid='" + AuthorTestIds.AUTHORS.AUTHOR_SAVE_BUTTON + "']";
    public static final String CANCEL_BUTTON = "[data-testid='" + AuthorTestIds.AUTHORS.AUTHOR_CANCEL_BUTTON + "']";
    public static final String DELETE_CONFIRM = "[data-testid='" + AuthorTestIds.AUTHORS.AUTHOR_DELETE_CONFIRM + "']";

    // Error messages
    public static final String NAME_REQUIRED_ERROR = "[data-testid='" + AuthorTestIds.AUTHORS.AUTHOR_NAME_REQUIRED_ERROR + "']";
    public static final String NAME_MIN_LENGTH_ERROR = "[data-testid='" + AuthorTestIds.AUTHORS.AUTHOR_NAME_MIN_LENGTH_ERROR + "']";

    // Smart locators
    public static String rowByAuthorName(String name) {
        return TABLE_ROW + ":has-text('" + name + "')";
    }

}