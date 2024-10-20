@test @test-b @test-b-and-c
Feature: Home Page pass with known fails

  @SID-SC01 @Scenario!
  Scenario: scenario 4
    Given start4
    When process4
    Then check4

  @SID-SC02 @Scenario2
  Scenario: scenario 5
    Given start5
    When process5
    Then check5

  @SID-SC03 @Scenario3
  Scenario: scenario 6
    Given start6
    When process6
    Then check6