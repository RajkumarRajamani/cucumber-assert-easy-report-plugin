package org.cucumber.easyreport.assertions;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Represents an assertion utility for various types of assertions.
 */
public interface Assertion {

    /**
     * Asserts that the actual value is equal to the expected value.
     *
     * @param label       the label for the assertion
     * @param actual      the actual value
     * @param expected    the expected value
     * @param failureMsg  the message to display if the assertion fails
     * @param passMessage the message to display if the assertion passes
     * @return the assertion result
     * @throws JsonProcessingException if there is an error processing JSON
     */
    Assertions assertEqualsTo(String label, Object actual, Object expected, String failureMsg, String passMessage) throws JsonProcessingException;

    /**
     * Asserts that the actual value is not equal to the expected value.
     *
     * @param label       the label for the assertion
     * @param actual      the actual value
     * @param expected    the expected value
     * @param failureMsg  the message to display if the assertion fails
     * @param passMessage the message to display if the assertion passes
     * @return the assertion result
     * @throws JsonProcessingException if there is an error processing JSON
     */
    Assertions assertNotEqualsTo(String label, Object actual, Object expected, String failureMsg, String passMessage) throws JsonProcessingException;

    /**
     * Asserts that the actual value is greater than the expected value.
     *
     * @param label       the label for the assertion
     * @param actual      the actual value
     * @param expected    the expected value
     * @param failureMsg  the message to display if the assertion fails
     * @param passMessage the message to display if the assertion passes
     * @return the assertion result
     * @throws JsonProcessingException if there is an error processing JSON
     */
    Assertions assertGreaterThan(String label, Object actual, Object expected, String failureMsg, String passMessage) throws JsonProcessingException;

    /**
     * Asserts that the actual value is lesser than the expected value.
     *
     * @param label       the label for the assertion
     * @param actual      the actual value
     * @param expected    the expected value
     * @param failureMsg  the message to display if the assertion fails
     * @param passMessage the message to display if the assertion passes
     * @return the assertion result
     * @throws JsonProcessingException if there is an error processing JSON
     */
    Assertions assertLesserThan(String label, Object actual, Object expected, String failureMsg, String passMessage) throws JsonProcessingException;

    /**
     * Asserts a failure with the given label and failure message.
     *
     * @param label      the label for the assertion
     * @param failureMsg the message to display if the assertion fails
     * @return the assertion result
     * @throws JsonProcessingException if there is an error processing JSON
     */
    Assertions assertFail(String label, String failureMsg) throws JsonProcessingException;


    /**
     * Asserts that the actual boolean value is true.
     *
     * @param label      the label for the assertion
     * @param actual     the actual boolean value
     * @param failureMsg the message to display if the assertion fails
     * @return the assertion result
     * @throws JsonProcessingException if there is an error processing JSON
     */
    Assertions isTrue(String label, boolean actual, String failureMsg) throws JsonProcessingException;

    /**
     * Asserts that the actual boolean value is false.
     *
     * @param label      the label for the assertion
     * @param actual     the actual boolean value
     * @param failureMsg the message to display if the assertion fails
     * @return the assertion result
     * @throws JsonProcessingException if there is an error processing JSON
     */
    Assertions isFalse(String label, boolean actual, String failureMsg) throws JsonProcessingException;
}
