package org.cucumber.easyreport;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class HtmlDataSet {
    private String overAllExecutionStatus;
    private Map<String, String> projectInfo = new HashMap<>();
    private Map<String, Long> featurePieChartDataMap = new HashMap<>();
    private Map<String, Long> testCasePieChartDataMap = new HashMap<>();
    private Map<String, Long> testStepPieChartDataMap = new HashMap<>();
    private Map<String, String> overallTestCaseStats = new HashMap<>();
    private List<Map<String, String>> featuresStats = new ArrayList<>();
}
