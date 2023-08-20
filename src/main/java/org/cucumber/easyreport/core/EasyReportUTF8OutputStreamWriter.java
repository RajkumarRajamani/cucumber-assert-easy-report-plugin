package org.cucumber.easyreport.core;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

final class EasyReportUTF8OutputStreamWriter extends OutputStreamWriter {
    EasyReportUTF8OutputStreamWriter(OutputStream out) {
        super(out, StandardCharsets.UTF_8);
    }
}