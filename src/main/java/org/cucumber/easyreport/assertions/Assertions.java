package org.cucumber.easyreport.assertions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.validator.GenericValidator;
import org.assertj.core.api.SoftAssertionError;
import org.assertj.core.api.SoftAssertions;
import org.cucumber.easyreport.exception.EasyReportException;
import org.cucumber.easyreport.util.VersionHelper;
import org.cucumber.easyreport.util.dateutils.DateUtils;

import java.util.*;

@Slf4j
public class Assertions implements Assertion {

    private static final Set<String> KNOWN_FAILURE_LABEL = new HashSet<>();

    public final Map<String, String> failures = new HashMap<>();

    private SoftAssertions assertions;

    public Assertions() {
        assertions = new SoftAssertions();
    }

    public Assertions addKnownFailureLabels(String... labels) {
        KNOWN_FAILURE_LABEL.addAll(List.of(labels));
        return this;
    }

    public static void addKnownFailuresLabels(String... labels) {
        KNOWN_FAILURE_LABEL.addAll(List.of(labels));
    }

    public static Set<String> getKnownFailureLabels() {
        return KNOWN_FAILURE_LABEL;
    }

    /**
     * Verifies that the actual value is equal to given value.
     *
     * @param label       name of the field of assertion
     * @param actual      actual value to compare
     * @param expected    expected value to compare against
     * @param failureMsg  failure message to include in assertion exception
     * @param passMessage success message to display on console or log for debug
     * @return @{@code Assertions}
     */
    @SneakyThrows
    @Override
    public Assertions assertEqualsTo(String label, Object actual, Object expected, String failureMsg, String passMessage) {
        String actualValue = Objects.nonNull(actual) ? getLabelValue(actual.toString()) : StringUtils.EMPTY;
        String expectedValue = Objects.nonNull(expected) ? getLabelValue(expected.toString()) : StringUtils.EMPTY;

        if (NumberUtils.isCreatable(actualValue) && NumberUtils.isCreatable(expectedValue)) {
            if (VersionHelper.compare(NumberUtils.createDouble(actualValue).toString(), NumberUtils.createDouble(expectedValue).toString()) != 0)
                assertions.assertThat(actual).as(failureMsg).isEqualTo(expected);
            else
                log.info(passMessage);
        } else if (!actualValue.equals(expectedValue)) {
            assertions.assertThat(actual).as(failureMsg).isEqualTo(expected);
        } else {
            log.info(passMessage);
        }

        try {
            assertions.assertAll();
        } catch (SoftAssertionError error) {
            List<String> list = error.getErrors().stream().map(er -> er.replaceAll("\\\\", "").replaceAll("\"", "")).toList();
            failures.put(label, new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(list));
        } finally {
            assertions = new SoftAssertions();
        }
        return this;
    }

    /**
     * Verifies that the actual value is not equal to given value.
     *
     * @param label       name of the field of assertion
     * @param actual      actual value to compare
     * @param expected    expected value to compare against
     * @param failureMsg  failure message to include in assertion exception
     * @param passMessage success message to display on console or log for debug
     * @return @{@code Assertions}
     */
    @Override
    public Assertions assertNotEqualsTo(String label, Object actual, Object expected, String failureMsg, String passMessage) throws JsonProcessingException {
        String actualValue = Objects.nonNull(actual) ? getLabelValue(actual.toString()) : StringUtils.EMPTY;
        String expectedValue = Objects.nonNull(expected) ? getLabelValue(expected.toString()) : StringUtils.EMPTY;

        if (NumberUtils.isCreatable(actualValue) && NumberUtils.isCreatable(expectedValue)) {
            if (VersionHelper.compare(NumberUtils.createDouble(actualValue).toString(), NumberUtils.createDouble(expectedValue).toString()) == 0)
                assertions.assertThat(actual).as(failureMsg).isNotEqualTo(expected);
            else
                log.info(passMessage);
        } else if (actualValue.equals(expectedValue)) {
            assertions.assertThat(actual).as(failureMsg).isNotEqualTo(expected);
        } else {
            log.info(passMessage);
        }

        try {
            assertions.assertAll();
        } catch (SoftAssertionError error) {
            List<String> list = error.getErrors().stream().map(er -> er.replaceAll("\\\\", "").replaceAll("\"", "")).toList();
            failures.put(label, new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(list));
        } finally {
            assertions = new SoftAssertions();
        }
        return this;
    }

    /**
     * Verifies that the actual value greater than given value
     *
     * @param label       name of the field of assertion
     * @param actual      actual value to compare
     * @param expected    expected value to compare against
     * @param failureMsg  failure message to include in assertion exception
     * @param passMessage success message to display on console or log for debug
     * @return @{@code Assertions}
     */
    @SneakyThrows
    @Override
    public Assertions assertGreaterThan(String label, Object actual, Object expected, String failureMsg, String passMessage) {
        String actualValue = Objects.nonNull(actual) ? getLabelValue(actual.toString()) : StringUtils.EMPTY;
        String expectedValue = Objects.nonNull(expected) ? getLabelValue(expected.toString()) : StringUtils.EMPTY;

        if (NumberUtils.isCreatable(actualValue) && NumberUtils.isCreatable(expectedValue)) {
            Double actualNumber = NumberUtils.createDouble(actualValue);
            Double expectedNumber = NumberUtils.createDouble(expectedValue);
            if (VersionHelper.compare(actualNumber.toString(), expectedNumber.toString()) != 1) // version helper returns 1 if greater, else returns 0 or -1
                assertions.assertThat(actualNumber).as(failureMsg).isGreaterThan(expectedNumber);
            else
                log.info(passMessage);
        } else {
            throw new EasyReportException("Provided arguments are not number to perform 'GreaterThan' check");
        }

        try {
            assertions.assertAll();
        } catch (SoftAssertionError error) {
            List<String> list = error.getErrors().stream().map(er -> er.replaceAll("\\\\", "").replaceAll("\"", "")).toList();
            failures.put(label, new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(list));
        } finally {
            assertions = new SoftAssertions();
        }
        return this;
    }

    /**
     * Verifies that the actual value is less than given value
     *
     * @param label       name of the field of assertion
     * @param actual      actual value to compare
     * @param expected    expected value to compare against
     * @param failureMsg  failure message to include in assertion exception
     * @param passMessage success message to display on console or log for debug
     * @return @{@code Assertions}
     */
    @Override
    public Assertions assertLesserThan(String label, Object actual, Object expected, String failureMsg, String passMessage) throws JsonProcessingException {
        String actualValue = Objects.nonNull(actual) ? getLabelValue(actual.toString()) : StringUtils.EMPTY;
        String expectedValue = Objects.nonNull(expected) ? getLabelValue(expected.toString()) : StringUtils.EMPTY;

        if (NumberUtils.isCreatable(actualValue) && NumberUtils.isCreatable(expectedValue)) {
            Double actualNumber = NumberUtils.createDouble(actualValue);
            Double expectedNumber = NumberUtils.createDouble(expectedValue);
            if (VersionHelper.compare(actualNumber.toString(), expectedNumber.toString()) != -1) // version helper returns -1 if lesser, else returns 0 or 1
                assertions.assertThat(actualNumber).as(failureMsg).isLessThan(expectedNumber);
            else
                log.info(passMessage);
        } else {
            throw new EasyReportException("Provided arguments are not number to perform 'GreaterThan' check");
        }

        try {
            assertions.assertAll();
        } catch (SoftAssertionError error) {
            List<String> list = error.getErrors().stream().map(er -> er.replaceAll("\\\\", "").replaceAll("\"", "")).toList();
            failures.put(label, new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(list));
        } finally {
            assertions = new SoftAssertions();
        }
        return this;
    }

    /**
     * Fails the assertion with given failure message
     *
     * @param label      name of the field of assertion
     * @param failureMsg failure message to include in assertion exception
     * @return @{@code Assertions}
     */
    @Override
    public Assertions assertFail(String label, String failureMsg) throws JsonProcessingException {
        try {
            assertions.fail(failureMsg);
            assertions.assertAll();
        } catch (SoftAssertionError error) {
            List<String> list = error.getErrors().stream().map(er -> er.replaceAll("\\\\", "").replaceAll("\"", "")).toList();
            failures.put(label, new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(list));
        } finally {
            assertions = new SoftAssertions();
        }
        return this;
    }

    /**
     * Checks if given boolean is true
     *
     * @param label      name of the field of assertion
     * @param failureMsg failure message to include in assertion exception
     * @return @{@code Assertions}
     */
    @SneakyThrows
    @Override
    public Assertions isTrue(String label, boolean actual, String failureMsg) {
        try {
            assertions.assertThat(actual).as(failureMsg).isTrue();
            assertions.assertAll();
        } catch (SoftAssertionError error) {
            List<String> list = error.getErrors().stream().map(er -> er.replaceAll("\\\\", "").replaceAll("\"", "")).toList();
            failures.put(label, new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(list));
        } finally {
            assertions = new SoftAssertions();
        }
        return this;
    }

    /**
     * Checks if given boolean is false
     *
     * @param label      name of the field of assertion
     * @param failureMsg failure message to include in assertion exception
     * @return @{@code Assertions}
     */
    @Override
    public Assertions isFalse(String label, boolean actual, String failureMsg) throws JsonProcessingException {
        try {
            assertions.assertThat(actual).as(failureMsg).isFalse();
            assertions.assertAll();
        } catch (SoftAssertionError error) {
            List<String> list = error.getErrors().stream().map(er -> er.replaceAll("\\\\", "").replaceAll("\"", "")).toList();
            failures.put(label, new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(list));
        } finally {
            assertions = new SoftAssertions();
        }
        return this;
    }


    @SneakyThrows
    public void assertAll() {
        try {
            if (!failures.isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(failures);
                throw new SoftAssertionError(List.of(json));
            }
        } finally {
            failures.clear();
        }
    }

    private static String getLabelValue(String value) {
        try {
            if (value.length() >= 10 && DateUtils.isDateValue(value.substring(0, 10)))
                return value.substring(0, 10);

            else
                return value;
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            throw new EasyReportException("Exception while getting value for SoftAssertion");
        }
    }

}
