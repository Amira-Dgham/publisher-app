Feature: Manage magazines in UI
  In order to manage magazines efficiently
  As a user
  I want to view, add, edit, delete, and validate magazines in the UI

  Background:
    Given I navigate to the magazines page

  @smoke
  Scenario: Load magazines table
    Then I should see the magazines table with 5 columns

  @smoke @regression
  Scenario: Add a new magazine
    Given a magazine is created using the data factory
    Then I should see the magazine in the table

  @regression
  Scenario: Delete a dynamically created magazine
    Given a magazine is created using the data factory
    When I select to delete that magazine
    And I confirm delete
    Then the magazine should not be visible in the table
#
#  @regression
#  Scenario: Validate form errors
#    And I open the add magazine dialog
#    When I save the magazine without filling fields
#    Then I should see required errors
#
#  @regression
#  Scenario: Navigate through magazine pages
#    Given there are more magazines than fit on one page
#    When the user clicks to go to the next page
#    Then the next set of magazines should be displayed