@test @test-a
Feature: Home Page All Pass

  Scenario: scenario 1
    Given start1 "testdata"
    When process1
    Then check1

  Scenario: scenario 2
    Given start2
    When process2
    Then check2

  Scenario: scenario 3
    Given start3
    When process3
    Then check3

