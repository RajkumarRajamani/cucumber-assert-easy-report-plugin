@test @test-c @test-b-and-c
Feature: Home Page pass with fails

#  Scenario: scenario 7
#    Given start7
#    When process7
#    Then check7
#
#  Scenario: scenario 8
#    Given start8
#    When process8
#    Then check8

  @CrossBrowser(env="Local",platform="Windows",browser={"Chrome","firefox","Safari"},version="131")
  Scenario Outline: scenario 9
    Given start9
      | name | value | details                                                                                      | city    | town    | pin    | state     | status | name2 | value2 | details2 | city2   | town2   | pin2   | state2    | status2 |
      | raj  | 100   | presentdfadsfakdjhfkasdfasdhfaskljdfkadfkahsdfjaskdfjhalsjdfkadlfjasdjfla;sdjf;ksajdfkabsdkj | chennai | chennai | 600042 | tamilnadu | active | raj   | 100    | present  | chennai | chennai | 600042 | tamilnadu | active  |
      | raj  | 100   | presentdfadsfakdjhfkasdfasdhfaskljdfkadfkahsdfjaskdfjhalsjdfkadlfjasdjfla;sdjf;ksajdfkabsdkj | chennai | chennai | 600042 | tamilnadu | active | raj   | 100    | present  | chennai | chennai | 600042 | tamilnadu | active  |
      | raj  | 100   | presentdfadsfakdjhfkasdfasdhfaskljdfkadfkahsdfjaskdfjhalsjdfkadlfjasdjfla;sdjf;ksajdfkabsdkj | chennai | chennai | 600042 | tamilnadu | active | raj   | 100    | present  | chennai | chennai | 600042 | tamilnadu | active  |
      | raj  | 100   | presentdfadsfakdjhfkasdfasdhfaskljdfkadfkahsdfjaskdfjhalsjdfkadlfjasdjfla;sdjf;ksajdfkabsdkj | chennai | chennai | 600042 | tamilnadu | active | raj   | 100    | present  | chennai | chennai | 600042 | tamilnadu | active  |
    When process9
    Then check9

    Examples:
      | header1 | header2 |
      | val1    | val2    |
