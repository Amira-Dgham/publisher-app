export const TEST_IDS = {
  // Shared Components
  SHARED: {
    NAVBAR: 'navbar',
    NAVBAR_SEARCH_FORM: 'navbar-search-form',
    NAVBAR_SEARCH_INPUT: 'navbar-search-input',
    NAVBAR_SEARCH_BUTTON: 'navbar-search-button',
    LOADER: 'global-loader',
    CONFIRM_DIALOG: 'confirm-dialog',
    CONFIRM_DIALOG_ACCEPT: 'confirm-dialog-accept',
    CONFIRM_DIALOG_REJECT: 'confirm-dialog-reject',
    CONFIRM_DIALOG_MESSAGE: 'confirm-dialog-message',
  },

  // Authors Feature
 AUTHORS: {
    AUTHORS_TABLE: 'authors-table',
    AUTHORS_TABLE_HEADER: 'authors-table-header',
    AUTHORS_TABLE_BODY: 'authors-table-body',
    AUTHORS_TABLE_ROW: 'authors-table-row',
    AUTHORS_TABLE_NAME_COLUMN: 'authors-table-name-column',
    AUTHORS_TABLE_BIRTH_DATE_COLUMN: 'authors-table-birth-date-column',
    AUTHORS_TABLE_NATIONALITY_COLUMN: 'authors-table-nationality-column',
    AUTHORS_TABLE_BOOKS_COLUMN: 'authors-table-books-column',
    AUTHORS_TABLE_MAGAZINES_COLUMN: 'authors-table-magazines-column',
    ADD_AUTHOR_BUTTON: 'add-author-button',
    AUTHOR_DIALOG: 'author-dialog',
    AUTHORS_PAGINATOR: 'authors-paginator',
     AUTHOR_DIALOG_HEADER: 'author-dialog-header',
    AUTHOR_NAME_INPUT: 'author-name-input',
    AUTHOR_BIRTH_DATE_INPUT: 'author-birth-date-input',
    AUTHOR_NATIONALITY_INPUT: 'author-nationality-input',
    AUTHOR_SAVE_BUTTON: 'author-save-button',
    AUTHOR_CANCEL_BUTTON: 'author-cancel-button',
    
    AUTHOR_NAME_ERROR: 'author-name-error',
    AUTHOR_NAME_REQUIRED_ERROR: 'author-name-required-error',
    AUTHOR_NAME_MIN_LENGTH_ERROR: 'author-name-min-length-error',
    AUTHOR_NAME_MAX_LENGTH_ERROR: 'author-name-max-length-error',
    AUTHOR_NATIONALITY_ERROR: 'author-nationality-error',
    AUTHOR_NATIONALITY_MAX_LENGTH_ERROR: 'author-nationality-max-length-error',
    AUTHOR_DELETE_CONFIRM: 'author-delete-confirm',
    AUTHOR_EDIT_BUTTON: 'author-edit-button',       // NEW
    AUTHOR_DELETE_BUTTON: 'author-delete-button'    // NEW
  },
  // Books Feature
  BOOKS: {
    // Table
    BOOKS_TABLE: 'books-table',
    BOOKS_TABLE_HEADER: 'books-table-header',
    BOOKS_TABLE_BODY: 'books-table-body',
    BOOKS_TABLE_ROW: 'books-table-row',
    BOOKS_TABLE_TITLE_COLUMN: 'books-table-title-column',
    BOOKS_TABLE_ISBN_COLUMN: 'books-table-isbn-column',
    BOOKS_TABLE_PUBLICATION_DATE_COLUMN: 'books-table-publication-date-column',
    BOOKS_TABLE_AUTHOR_COLUMN: 'books-table-author-column',
    BOOKS_TABLE_ACTIONS_COLUMN: 'books-table-actions-column',
    
    // Pagination
    BOOKS_PAGINATOR: 'books-paginator',
    
    // Buttons
    ADD_BOOK_BUTTON: 'add-book-button',
    EDIT_BOOK_BUTTON: 'edit-book-button',
    DELETE_BOOK_BUTTON: 'delete-book-button',
    
    // Dialog
    BOOK_DIALOG: 'book-dialog',
    BOOK_DIALOG_HEADER: 'book-dialog-header',
    
    // Form Fields
    BOOK_TITLE_INPUT: 'book-title-input',
    BOOK_ISBN_INPUT: 'book-isbn-input',
    BOOK_PUBLICATION_DATE_INPUT: 'book-publication-date-input',
    BOOK_AUTHOR_SELECT: 'book-author-select',
    
    // Form Validation
    BOOK_TITLE_ERROR: 'book-title-error',
    BOOK_TITLE_REQUIRED_ERROR: 'book-title-required-error',
    BOOK_TITLE_MIN_LENGTH_ERROR: 'book-title-min-length-error',
    BOOK_TITLE_MAX_LENGTH_ERROR: 'book-title-max-length-error',
    BOOK_ISBN_ERROR: 'book-isbn-error',
    BOOK_ISBN_REQUIRED_ERROR: 'book-isbn-required-error',
    BOOK_ISBN_PATTERN_ERROR: 'book-isbn-pattern-error',
    BOOK_PUBLICATION_DATE_ERROR: 'book-publication-date-error',
    BOOK_PUBLICATION_DATE_REQUIRED_ERROR: 'book-publication-date-required-error',
    BOOK_AUTHOR_ERROR: 'book-author-error',
    BOOK_AUTHOR_REQUIRED_ERROR: 'book-author-required-error',
    
    // Form Actions
    BOOK_SAVE_BUTTON: 'book-save-button',
    BOOK_CANCEL_BUTTON: 'book-cancel-button',
    
    // Delete Confirmation
    BOOK_DELETE_CONFIRM: 'book-delete-confirm',
  },

  // Magazines Feature
  MAGAZINES: {
    // Table
    MAGAZINES_TABLE: 'magazines-table',
    MAGAZINES_TABLE_HEADER: 'magazines-table-header',
    MAGAZINES_TABLE_BODY: 'magazines-table-body',
    MAGAZINES_TABLE_ROW: 'magazines-table-row',
    MAGAZINES_TABLE_TITLE_COLUMN: 'magazines-table-title-column',
    MAGAZINES_TABLE_ISSUE_NUMBER_COLUMN: 'magazines-table-issue-number-column',
    MAGAZINES_TABLE_PUBLICATION_DATE_COLUMN: 'magazines-table-publication-date-column',
    MAGAZINES_TABLE_AUTHORS_COLUMN: 'magazines-table-authors-column',
    MAGAZINES_TABLE_ACTIONS_COLUMN: 'magazines-table-actions-column',
    
    // Pagination
    MAGAZINES_PAGINATOR: 'magazines-paginator',
    
    // Buttons
    ADD_MAGAZINE_BUTTON: 'add-magazine-button',
    EDIT_MAGAZINE_BUTTON: 'edit-magazine-button',
    DELETE_MAGAZINE_BUTTON: 'delete-magazine-button',
    
    // Dialog
    MAGAZINE_DIALOG: 'magazine-dialog',
    MAGAZINE_DIALOG_HEADER: 'magazine-dialog-header',
    
    // Form Fields
    MAGAZINE_TITLE_INPUT: 'magazine-title-input',
    MAGAZINE_ISSUE_NUMBER_INPUT: 'magazine-issue-number-input',
    MAGAZINE_PUBLICATION_DATE_INPUT: 'magazine-publication-date-input',
    MAGAZINE_AUTHOR_IDS_INPUT: 'magazine-author-ids-input',
    
    // Form Validation
    MAGAZINE_TITLE_ERROR: 'magazine-title-error',
    MAGAZINE_TITLE_REQUIRED_ERROR: 'magazine-title-required-error',
    MAGAZINE_TITLE_MIN_LENGTH_ERROR: 'magazine-title-min-length-error',
    MAGAZINE_TITLE_MAX_LENGTH_ERROR: 'magazine-title-max-length-error',
    MAGAZINE_ISSUE_NUMBER_ERROR: 'magazine-issue-number-error',
    MAGAZINE_ISSUE_NUMBER_REQUIRED_ERROR: 'magazine-issue-number-required-error',
    MAGAZINE_ISSUE_NUMBER_MIN_ERROR: 'magazine-issue-number-min-error',
    MAGAZINE_ISSUE_NUMBER_MAX_ERROR: 'magazine-issue-number-max-error',
    MAGAZINE_PUBLICATION_DATE_ERROR: 'magazine-publication-date-error',
    MAGAZINE_PUBLICATION_DATE_REQUIRED_ERROR: 'magazine-publication-date-required-error',
    MAGAZINE_AUTHOR_IDS_ERROR: 'magazine-author-ids-error',
    MAGAZINE_AUTHOR_IDS_REQUIRED_ERROR: 'magazine-author-ids-required-error',
    MAGAZINE_AUTHOR_IDS_PATTERN_ERROR: 'magazine-author-ids-pattern-error',
    
    // Form Actions
    MAGAZINE_SAVE_BUTTON: 'magazine-save-button',
    MAGAZINE_CANCEL_BUTTON: 'magazine-cancel-button',
    
    // Delete Confirmation
    MAGAZINE_DELETE_CONFIRM: 'magazine-delete-confirm',
  },

  // Publications Feature
  PUBLICATIONS: {
    // Table
    PUBLICATIONS_TABLE: 'publications-table',
    PUBLICATIONS_TABLE_HEADER: 'publications-table-header',
    PUBLICATIONS_TABLE_BODY: 'publications-table-body',
    PUBLICATIONS_TABLE_ROW: 'publications-table-row',
    PUBLICATIONS_TABLE_TITLE_COLUMN: 'publications-table-title-column',
    PUBLICATIONS_TABLE_PUBLICATION_DATE_COLUMN: 'publications-table-publication-date-column',
    PUBLICATIONS_TABLE_ACTIONS_COLUMN: 'publications-table-actions-column',
    
    // Pagination
    PUBLICATIONS_PAGINATOR: 'publications-paginator',
    
    // Buttons
    ADD_PUBLICATION_BUTTON: 'add-publication-button',
    EDIT_PUBLICATION_BUTTON: 'edit-publication-button',
    DELETE_PUBLICATION_BUTTON: 'delete-publication-button',
    
    // Dialog
    PUBLICATION_DIALOG: 'publication-dialog',
    PUBLICATION_DIALOG_HEADER: 'publication-dialog-header',
    
    // Form Fields
    PUBLICATION_TITLE_INPUT: 'publication-title-input',
    PUBLICATION_PUBLICATION_DATE_INPUT: 'publication-publication-date-input',
    
    // Form Validation
    PUBLICATION_TITLE_ERROR: 'publication-title-error',
    PUBLICATION_TITLE_REQUIRED_ERROR: 'publication-title-required-error',
    PUBLICATION_TITLE_MIN_LENGTH_ERROR: 'publication-title-min-length-error',
    PUBLICATION_TITLE_MAX_LENGTH_ERROR: 'publication-title-max-length-error',
    PUBLICATION_PUBLICATION_DATE_ERROR: 'publication-publication-date-error',
    PUBLICATION_PUBLICATION_DATE_REQUIRED_ERROR: 'publication-publication-date-required-error',
    
    // Form Actions
    PUBLICATION_SAVE_BUTTON: 'publication-save-button',
    PUBLICATION_CANCEL_BUTTON: 'publication-cancel-button',
    
    // Delete Confirmation
    PUBLICATION_DELETE_CONFIRM: 'publication-delete-confirm',
  },
}; 