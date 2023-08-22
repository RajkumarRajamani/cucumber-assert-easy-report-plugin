@test @test-b
Feature: Home Page pass with known fails

  Scenario: scenario 4
    Given start4
    When process4
    Then check4

  Scenario: scenario 5
    Given start5
    When process5
    Then check5

  Scenario: scenario 6
    Given start6
    When process6
    Then check6