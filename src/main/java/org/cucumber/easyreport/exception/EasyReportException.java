package org.cucumber.easyreport.exception;

public class EasyReportException extends RuntimeException {

    public EasyReportException() {
        super();
    }

    public EasyReportException(String errorMessage) {
        super(errorMessage);
    }

    public EasyReportException(String errorMessage, Throwable e) {
        super(errorMessage, e);
    }
}
