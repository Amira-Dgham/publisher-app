Feature: Manage authors in UI
  This feature allows managing authors in the UI:
  - loading the authors table
  - adding new authors
  - deleting authors
  - validating form errors

  Background:
    Given I navigate to the authors page

  Scenario: Load authors table
    Then I should see the authors table with 6 columns

  Scenario: Add a new author
    And I open the add author dialog
    When I fill the author form with name "John Doe", birthDate "1990-01-01", nationality "US"
    And I save the author
    Then I should see the author "John Doe" in the table

  Scenario: Delete an author
    And an author "John Doe" exists
    When I select to delete the author "John Doe"
    And I confirm delete
    Then the author "John Doe" should not be visible in the table

  Scenario: Validate form errors
    And I open the add author dialog
    When I save the author without filling fields
    Then I should see name required error