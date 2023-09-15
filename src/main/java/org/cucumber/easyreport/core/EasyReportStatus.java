package org.cucumber.easyreport.core;

import lombok.Getter;

@Getter
public enum EasyReportStatus {

    PASSED("passed"),
    FAILED("failed"),
    FAILED_DEFERRED("failed with deferred issue"),
    SKIPPED("skipped"),
    PENDING("pending"),
    UNDEFINED("undefined"),
    AMBIGUOUS("ambiguous"),
    UNUSED("unused");

    private final String status;

    EasyReportStatus(String status) {
        this.status = status;
    }
}
