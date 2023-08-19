package org.cucumber.easyreport.assertions;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.SoftAssertionError;
import org.assertj.core.api.SoftAssertions;

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
     * @param label name of the field of assertion
     * @param actual actual value to compare
     * @param expected expected value to compare against
     * @param failureMsg failure message to include in assertion exception
     * @param passMessage success message to display on console or log for debug
     * @return @{@code Assertions}
     */
    @SneakyThrows
    @Override
    public Assertions assertEqualsTo(String label, String actual, String expected, String failureMsg, String passMessage) {
        if(actual.equals(expected)) {
            log.info(passMessage);
        } else {
                log.error(failureMsg);
                assertions.assertThat(actual).as(failureMsg).isEqualTo(expected);
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
     * @param label
     * @param actual
     * @param expected
     * @param failureMsg
     * @param passMessage
     */
    @Override
    public void assertEqualsTo(String label, int actual, int expected, String failureMsg, String passMessage) {

    }

    @SneakyThrows
    public void assertAll() {
        try {
            if(!failures.isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(failures);
                throw new SoftAssertionError(List.of(json));
            }
        } finally {
            failures.clear();
        }
    }

}
