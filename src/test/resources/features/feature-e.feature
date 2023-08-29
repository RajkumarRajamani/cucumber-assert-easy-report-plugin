@test @test-e
Feature: Home Page pass with skipped

  Scenario: scenario 13
    Given start13
    When process13
    Then check13

  Scenario: scenario 14
    Given start14
    When process1
    Then check14

  Scenario: scenario 12
    Given start12
    When process12
    Then check12