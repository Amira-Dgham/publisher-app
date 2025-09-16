Feature: Manage authors in UI

  Scenario: Load authors table
    Given I navigate to the authors page
    Then I should see the authors table with 6 columns

  Scenario: Add a new author
    Given I open the add author dialog
    When I fill the author form with name "John Doe", birthDate "1990-01-01", nationality "US"
    And I save the author
    Then I should see the author "John Doe" in the table

  Scenario: Delete an author
    Given an author "John Doe" exists
    When I delete the author "John Doe"
    Then the author "John Doe" should not be visible in the table

  Scenario: Validate form errors
    Given I open the add author dialog
    When I save the author without filling fields
    Then I should see name required error