package org.cucumber.easyreport.assertions;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface Assertion {

    Assertions assertEqualsTo(String label, Object actual, Object expected, String failureMsg, String passMessage) throws JsonProcessingException;
    Assertions assertNotEqualsTo(String label, Object actual, Object expected, String failureMsg, String passMessage) throws JsonProcessingException;
    Assertions assertGreaterThan(String label, Object actual, Object expected, String failureMsg, String passMessage) throws JsonProcessingException;
    Assertions assertLesserThan(String label, Object actual, Object expected, String failureMsg, String passMessage) throws JsonProcessingException;
    Assertions assertFail(String label, String failureMsg) throws JsonProcessingException;
    Assertions isTrue(String label, boolean actual, String failureMsg) throws JsonProcessingException;
    Assertions isFalse(String label, boolean actual, String failureMsg) throws JsonProcessingException;

}
