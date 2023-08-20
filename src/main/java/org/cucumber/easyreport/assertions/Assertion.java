package org.cucumber.easyreport.assertions;

import com.fasterxml.jackson.core.JsonProcessingException;

public interface Assertion {

    public Assertions assertEqualsTo(String label, Object actual, Object expected, String failureMsg, String passMessage) throws JsonProcessingException;
    public Assertions assertNotEqualsTo(String label, Object actual, Object expected, String failureMsg, String passMessage) throws JsonProcessingException;
    public Assertions assertGreaterThan(String label, Object actual, Object expected, String failureMsg, String passMessage) throws JsonProcessingException;
    public Assertions assertLesserThan(String label, Object actual, Object expected, String failureMsg, String passMessage) throws JsonProcessingException;
    public Assertions assertFail(String label, String failureMsg) throws JsonProcessingException;
    public Assertions isTrue(String label, boolean actual, String failureMsg) throws JsonProcessingException;
    public Assertions isFalse(String label, boolean actual, String failureMsg) throws JsonProcessingException;

}
