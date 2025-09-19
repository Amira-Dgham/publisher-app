#Feature: Manage authors in UI
#  In order to manage authors efficiently
#  As a user
#  I want to view, add, edit, delete, and validate authors in the UI
#
#  Background:
#    Given I navigate to the authors page
#
#  @smoke
#  Scenario: Load authors table
#    Then I should see the authors table with 6 columns
#
#  @smoke @regression
#  Scenario: Add a new author using data factory
#    Given an author is created using the data factory
#    Then I should see the author in the table
#
#  @regression
#  Scenario: Delete a dynamically created author
#    Given an author is created using the data factory
#    When I select to delete that author
#    And I confirm delete
#    Then the author should not be visible in the table
#
#  @regression
#  Scenario: Validate form errors
#    And I open the add author dialog
#    When I save the author without filling fields
#    Then I should see name required error
#
#  @regression
#  Scenario: Navigate through author pages
#    Given there are more authors than fit on one page
#    When the user clicks to go to the next page
#    Then the next set of authors should be displayed