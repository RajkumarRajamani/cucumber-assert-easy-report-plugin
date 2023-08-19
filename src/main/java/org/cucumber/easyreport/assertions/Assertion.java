package org.cucumber.easyreport.assertions;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface Assertion {

    public Assertions assertEqualsTo(String label, String actual, String expected, String failureMsg, String passMessage) throws JsonProcessingException;
    public void assertEqualsTo(String label, int actual, int expected, String failureMsg, String passMessage);
//    public void assertEqualsTo(String label, float actual, float expected, String failureMsg, String passMessage);
//    public void assertEqualsTo(String label, double actual, double expected, String failureMsg, String passMessage);
//    public void assertLessThanOrEqualTo(String label, String actual, String expected, String failureMsg, String passMessage);
//    public void assertGreaterThanOrEqualTo(String label, String actual, String expected, String failureMsg, String passMessage);

}
