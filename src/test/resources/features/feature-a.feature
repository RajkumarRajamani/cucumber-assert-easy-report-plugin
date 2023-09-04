@test @test-a
Feature: Home Page All Pass

  Scenario Outline: scenario 1
    Given start1 "<testdataA>" and "<column2>"
    When process1
    Then check1

    Examples:
    |testdataA| column2|
    |case1       |    casea |
    |case2       |   caseb  |

  Scenario: scenario 2
    Given start2
    When process2
    Then check2

  Scenario: scenario 3
    Given start2
    When process2
    Then check2

