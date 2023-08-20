package org.cucumber.easyreport.core;

import lombok.Getter;

@Getter
public enum EasyReportStatus {

    PASSED("passed"),
    FAILED("failed"),
    SKIPPED("skipped"),
    KNOWN_FAILURES("known failures"),
    PASSED_WITH_KNOWN_FAILURES("passed with known failures"),
    PENDING("pending"),
    UNDEFINED("undefined"),
    AMBIGUOUS("ambiguous"),
    UNUSED("unused");

    private final String status;

    EasyReportStatus(String status) {
        this.status = status;
    }
}
