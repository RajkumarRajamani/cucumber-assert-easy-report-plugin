package org.cucumber.easyreport.util.dateutils;

public enum DateFormats {
    YYYYMMDD_HYPHEN("yyyy-MM-dd"),
    YYYYMMDD_SLASH("yyyy/MM/dd"),
    YYYYMMMDD_HYPHEN("yyyy-MMM-dd"),

    YYYYDDMM_HYPHEN("yyyy-dd-MM"),
    YYYYDDMM_SLASH("yyyy/dd/MM"),


    MMDDYYYY_HYPHEN("MM-dd-yyyy"),
    MMDDYYYY_SLASH("MM/dd/yyyy"),

    DDMMYYYY_HYPHEN("dd-MM-YYYY"),
    DDMMYYYY_SLASH("dd/MM/yyyy"),
    DDMMYYYY("ddMMyyyy"),
    DDMMMYYYY_HYPHEN("dd-MMM-yyyy");

    private final String format;

    DateFormats(String format) {
        this.format = format;
    }

    public String get() {
        return this.format;
    }
}