package org.cucumber.easyreport.assertions;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.assertj.core.api.SoftAssertionError;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.error.AssertJMultipleFailuresError;
import org.cucumber.easyreport.exception.EasyReportException;
import org.cucumber.easyreport.util.VersionHelper;
import org.json.builder.core.JsonBuilder;
import org.utils.datetime.date.DateTimeFormat;
import org.utils.datetime.date.DateTimeUtils;
import org.utils.datetime.date.TimeZoneId;

import java.text.MessageFormat;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Consumer;

@Slf4j
public class Assertions implements Assertion {

    private final ThreadLocal<Map<String, String>> knownFailureLabelSet = ThreadLocal.withInitial(HashMap::new);

    private final ThreadLocal<Map<String, List<Map<String, String>>>> failures = ThreadLocal.withInitial(HashMap::new);

    private final ThreadLocal<SoftAssertions> assertions = ThreadLocal.withInitial(SoftAssertions::new);

    public Assertions() {
        knownFailureLabelSet.get().putAll(KnownFailuresReader.readKnownFailures());
    }

    public synchronized Assertions addKnownFailureLabels(String label, String trackingId) {
        knownFailureLabelSet.get().put(label, Objects.nonNull(trackingId) && !trackingId.isEmpty() ? trackingId : "No Tracking Id");
        return this;
    }

    public synchronized Map<String, String> getKnownFailureLabels() {
        return knownFailureLabelSet.get();
    }

    private synchronized Assertions assertCurrentStep(String label, SoftAssertions assertion) {
        String trackingId = getKnownFailureLabels().get(label);
        trackingId = Objects.nonNull(trackingId) && !trackingId.isEmpty() ? trackingId : "No Tracking Id";
        try {
            assertion.assertAll();
        } catch (AssertJMultipleFailuresError e) {
            String failure = e.getFailures().stream().map(Throwable::getMessage).toList()
                    .get(0).replaceAll("\n", " ")
                    .replaceAll("\r", " ")
                    .replaceAll("\"", "'");
            this.addIntoFailures(label, trackingId, failure);
        } catch (SoftAssertionError error) {
            String failure = error.getErrors()
                    .get(0).replaceAll("\r", " ")
                    .replaceAll("\n", " ")
                    .replaceAll("\"", "'");
            this.addIntoFailures(label, trackingId, failure);
        } finally {
            assertions.set(new SoftAssertions());
        }
        return this;
    }

    private synchronized void addIntoFailures(String label, String trackingId, String failure) {
        if (getKnownFailureLabels().containsKey(label)) {
            if (failures.get().containsKey("knownFailures")) {
                Map<String, String> fail = new HashMap<>();
                fail.put("label", label);
                fail.put("trackingId", trackingId);
                fail.put("failureMessage", failure);
                failures.get().get("knownFailures").add(fail);
            } else {
                List<Map<String, String>> failureList = new ArrayList<>();
                Map<String, String> fail = new HashMap<>();
                fail.put("label", label);
                fail.put("trackingId", trackingId);
                fail.put("failureMessage", failure);
                failureList.add(fail);
                failures.get().put("knownFailures", failureList);
            }
        } else {
            if (failures.get().containsKey("failures")) {
                Map<String, String> fail = new HashMap<>();
                fail.put("label", label);
                fail.put("trackingId", trackingId);
                fail.put("failureMessage", failure);
                failures.get().get("failures").add(fail);
            } else {
                List<Map<String, String>> failureList = new ArrayList<>();
                Map<String, String> fail = new HashMap<>();
                fail.put("label", label);
                fail.put("trackingId", trackingId);
                fail.put("failureMessage", failure);
                failureList.add(fail);
                failures.get().put("failures", failureList);
            }
        }
    }

    /**
     * Verifies that the actual value is equal to the given value.
     *
     * @param label       name of the field of assertion
     * @param actual      actual value to compare
     * @param expected    expected value to compare against
     * @param failureMsg  failure message to include in assertion exception
     * @param passMessage success message to display on console or log for debug
     * @return {@code Assertions}
     */
    @SneakyThrows
    @Override
    public synchronized Assertions assertEqualsTo(String label, Object actual, Object expected, String failureMsg, String passMessage) {
        String actualValue = Objects.nonNull(actual) ? getLabelValue(actual.toString()) : StringUtils.EMPTY;
        String expectedValue = Objects.nonNull(expected) ? getLabelValue(expected.toString()) : StringUtils.EMPTY;

        if (NumberUtils.isCreatable(actualValue) && NumberUtils.isCreatable(expectedValue)) {
            if (VersionHelper.compare(NumberUtils.createDouble(actualValue).toString(), NumberUtils.createDouble(expectedValue).toString()) != 0)
                assertions.get().assertThat(actual).as(failureMsg).isEqualTo(expected);
            else
                log.info(passMessage);
        } else if (!actualValue.equals(expectedValue)) {
            assertions.get().assertThat(actual).as(failureMsg).isEqualTo(expected);
        } else {
            log.info(passMessage);
        }

        return assertCurrentStep(label, assertions.get());
    }

    /**
     * Verifies that the actual value is not equal to given value.
     *
     * @param label       name of the field of assertion
     * @param actual      actual value to compare
     * @param expected    expected value to compare against
     * @param failureMsg  failure message to include in assertion exception
     * @param passMessage success message to display on console or log for debug
     * @return {@code Assertions}
     */
    @Override
    public synchronized Assertions assertNotEqualsTo(String label, Object actual, Object expected, String failureMsg, String passMessage) {
        String actualValue = Objects.nonNull(actual) ? getLabelValue(actual.toString()) : StringUtils.EMPTY;
        String expectedValue = Objects.nonNull(expected) ? getLabelValue(expected.toString()) : StringUtils.EMPTY;

        if (NumberUtils.isCreatable(actualValue) && NumberUtils.isCreatable(expectedValue)) {
            if (VersionHelper.compare(NumberUtils.createDouble(actualValue).toString(), NumberUtils.createDouble(expectedValue).toString()) == 0)
                assertions.get().assertThat(actual).as(failureMsg).isNotEqualTo(expected);
            else
                log.info(passMessage);
        } else if (actualValue.equals(expectedValue)) {
            assertions.get().assertThat(actual).as(failureMsg).isNotEqualTo(expected);
        } else {
            log.info(passMessage);
        }

        return assertCurrentStep(label, assertions.get());
    }

    /**
     * Verifies that the actual value greater than given value
     *
     * @param label       name of the field of assertion
     * @param actual      actual value to compare
     * @param expected    expected value to compare against
     * @param failureMsg  failure message to include in assertion exception
     * @param passMessage success message to display on console or log for debug
     * @return {@code Assertions}
     */
    @SneakyThrows
    @Override
    public synchronized Assertions assertGreaterThan(String label, Object actual, Object expected, String failureMsg, String passMessage) {
        String actualValue = Objects.nonNull(actual) ? getLabelValue(actual.toString()) : StringUtils.EMPTY;
        String expectedValue = Objects.nonNull(expected) ? getLabelValue(expected.toString()) : StringUtils.EMPTY;

        if (NumberUtils.isCreatable(actualValue) && NumberUtils.isCreatable(expectedValue)) {
            Double actualNumber = NumberUtils.createDouble(actualValue);
            Double expectedNumber = NumberUtils.createDouble(expectedValue);
            if (VersionHelper.compare(actualNumber.toString(), expectedNumber.toString()) != 1) // version helper returns 1 if greater, else returns 0 or -1
                assertions.get().assertThat(actualNumber).as(failureMsg).isGreaterThan(expectedNumber);
            else
                log.info(passMessage);
        } else {
            throw new EasyReportException("Provided arguments are not number to perform 'GreaterThan' check");
        }

        return assertCurrentStep(label, assertions.get());
    }

    /**
     * Verifies that the actual value is less than given value
     *
     * @param label       name of the field of assertion
     * @param actual      actual value to compare
     * @param expected    expected value to compare against
     * @param failureMsg  failure message to include in assertion exception
     * @param passMessage success message to display on console or log for debug
     * @return {@code Assertions}
     */
    @Override
    public synchronized Assertions assertLesserThan(String label, Object actual, Object expected, String failureMsg, String passMessage) {
        String actualValue = Objects.nonNull(actual) ? getLabelValue(actual.toString()) : StringUtils.EMPTY;
        String expectedValue = Objects.nonNull(expected) ? getLabelValue(expected.toString()) : StringUtils.EMPTY;

        if (NumberUtils.isCreatable(actualValue) && NumberUtils.isCreatable(expectedValue)) {
            Double actualNumber = NumberUtils.createDouble(actualValue);
            Double expectedNumber = NumberUtils.createDouble(expectedValue);
            if (VersionHelper.compare(actualNumber.toString(), expectedNumber.toString()) != -1) // version helper returns -1 if lesser, else returns 0 or 1
                assertions.get().assertThat(actualNumber).as(failureMsg).isLessThan(expectedNumber);
            else
                log.info(passMessage);
        } else {
            throw new EasyReportException("Provided arguments are not number to perform 'GreaterThan' check");
        }

        return assertCurrentStep(label, assertions.get());
    }

    /**
     * Fails the assertion with given failure message
     *
     * @param label      name of the field of assertion
     * @param failureMsg failure message to include in assertion exception
     * @return {@code Assertions}
     */
    @Override
    public synchronized Assertions assertFail(String label, String failureMsg) {
        assertions.get().fail(failureMsg);
        return assertCurrentStep(label, assertions.get());
    }

    /**
     * Checks if given boolean is true
     *
     * @param label      name of the field of assertion
     * @param failureMsg failure message to include in assertion exception
     * @return {@code Assertions}
     */
    @SneakyThrows
    @Override
    public synchronized Assertions isTrue(String label, boolean actual, String failureMsg) {
        assertions.get().assertThat(actual).as(failureMsg).isTrue();
        return assertCurrentStep(label, assertions.get());
    }

    /**
     * Checks if given boolean is false
     *
     * @param label      name of the field of assertion
     * @param failureMsg failure message to include in assertion exception
     * @return {@code Assertions}
     */
    @Override
    public synchronized Assertions isFalse(String label, boolean actual, String failureMsg) {
        assertions.get().assertThat(actual).as(failureMsg).isFalse();
        return assertCurrentStep(label, assertions.get());
    }

    public synchronized Assertions addAssertions(String label, Consumer<SoftAssertions> assertions) {
        assertions.accept(this.assertions.get());
        return assertCurrentStep(label, this.assertions.get());
    }

    @SneakyThrows
    public synchronized void assertAll() {
        try {
            if (!failures.get().isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(failures.get());
                throw new SoftAssertionError(List.of(json));
            }
        } finally {
            failures.get().clear();
            knownFailureLabelSet.get().clear();
        }
    }

    private synchronized static String getLabelValue(String value) {
        try {
            DateTimeUtils.FormatOptions options = DateTimeUtils.FormatOptions.builder()
                    .locale(Locale.US)
                    .zoneId(TimeZoneId.UTC.id()) // Use the desired time zone
                    .zoneOffset(ZoneOffset.UTC) // Use the desired offset
                    .build();
            DateTimeUtils util = new DateTimeUtils();
            util.setDefaultOptions(options);
            if (util.isValid(value))
                return util.formatTo(value, DateTimeFormat.FORMAT_DD_MM_YYYY);
            else
                return value;
        } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
            throw new EasyReportException("Exception while getting value for SoftAssertion");
        }
    }

    private synchronized <T> List<String> compare(JsonNode actualNode, JsonNode expectedNode) {
        String printFormat = "%-15s %-50s %-50s %-100s";
        List<String> logs = new ArrayList<>();

        List<String> jsonPaths = JsonBuilder.collectJsonPaths(actualNode, StringUtils.EMPTY, new ArrayList<>());
        jsonPaths.forEach(path -> {
            String actualValueFromNode = JsonBuilder.getValueAtNodeAsText(actualNode, path);
            String expectedValueFromNode = JsonBuilder.getValueAtNodeAsText(expectedNode, path);
            try {
                String actualValue = Objects.nonNull(actualValueFromNode) ? getLabelValue(actualValueFromNode) : StringUtils.EMPTY;
                String expectedValue = Objects.nonNull(expectedValueFromNode) ? getLabelValue(expectedValueFromNode) : StringUtils.EMPTY;
                String failureMessage = MessageFormat.format("Mismatch is Found.\nExpected: {0} \n\nbut was: \n\n Actual: {1}", expectedValue, actualValue);
                String successLog = String.format(printFormat, "Matching", expectedValue, actualValue, path);
                String failureLog = String.format(printFormat, "Not-Matching", expectedValue, actualValue, path);

                if (NumberUtils.isCreatable(actualValue) && NumberUtils.isCreatable(expectedValue)) {
                    if (VersionHelper.compare(NumberUtils.createNumber(actualValue).toString(), NumberUtils.createNumber(expectedValue).toString()) != 0) {
                        this.addAssertions(path, assertion -> assertion.assertThat(actualValue).as(failureMessage).isEqualTo(expectedValue));
                        log.error(failureMessage);
                        logs.add(failureLog);
                    } else {
                        logs.add(successLog);
                    }
                } else if (!actualValue.equals(expectedValue)) {
                    this.addAssertions(path, assertion -> assertion.assertThat(actualValue).as(failureMessage).isEqualTo(expectedValue));
                    log.error(failureMessage);
                    logs.add(failureLog);
                } else {
                    logs.add(successLog);
                }
            } catch (Exception e) {
                String error = MessageFormat.format("Error Occurred while validating values for path: {0} with \nActual: {1} and \nExpected: {2}", path, actualValueFromNode, expectedValueFromNode);
                log.error(error);
                e.printStackTrace();
            }
        });
        return logs;
    }

    public synchronized List<String> compare(String actual, String expected) {
        JsonNode actualNode = JsonBuilder.transformJsonStringToJsonNode(actual);
        JsonNode expectedNode = JsonBuilder.transformJsonStringToJsonNode(expected);
        return compare(actualNode, expectedNode);
    }

    public synchronized List<String> compare(Object actual, Object expected) {
        JsonNode actualNode = JsonBuilder.transformPojoToJsonNode(actual);
        JsonNode expectedNode = JsonBuilder.transformPojoToJsonNode(expected);
        return compare(actualNode, expectedNode);
    }

}
