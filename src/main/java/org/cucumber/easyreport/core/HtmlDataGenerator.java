package org.cucumber.easyreport.core;

import org.cucumber.easyreport.pojo.ReportJsonFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.SneakyThrows;
import org.apache.commons.text.CaseUtils;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.cucumber.easyreport.core.EasyReportStatus.*;
import static java.util.stream.Collectors.toSet;

public class HtmlDataGenerator {

    private static final Map<String, Object> htmlDataMap = new HashMap<>();
    private List<ReportJsonFeature> features = new ArrayList<>();

    @Getter
    private HtmlDataSet htmlDataSet;

    public HtmlDataGenerator(List<ReportJsonFeature> features) {
        this.features = features;
        this.processData();
    }

    @SneakyThrows
    private void processData() {
        this.processStep();
        this.processTestCase();
        this.processFeature();
        htmlDataSet = new HtmlDataSet();
        this.loadProjectInformation(htmlDataSet);
        this.generateDataForFeaturePieChart(features, htmlDataSet);
        this.generateDataForTestCasePieChart(features, htmlDataSet);
        this.generateDataForTestStepPieChart(features, htmlDataSet);
        this.generateFeaturesEntireResults(htmlDataSet);
//        String json = new ObjectMapper().writerWithDefaultPrettyPrinter() .writeValueAsString(htmlDataSet);
//        System.out.println(json);
    }

    private void loadProjectInformation(HtmlDataSet htmlDataSet) {
        EasyReportConfigReader configReader = new EasyReportConfigReader();
        Map<String, String> projectInfo = new HashMap<>();
        projectInfo.put("environment", configReader.getEnvironment());
        projectInfo.put("browser", configReader.getBrowser());
        projectInfo.put("appName", configReader.getApplicationName());
        projectInfo.put("os", configReader.getOs());
        projectInfo.put("description", configReader.getProjectDescription());

        LocalDateTime startTime = features.stream()
                .map(ReportJsonFeature::getElements)
                .flatMap(Collection::stream)
                .map(ReportJsonFeature.Element::getStart_timestamp)
                .map(dt -> dt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                .min(Comparator.naturalOrder()).orElseThrow();

        long totalDuration = features.stream()
                .map(ReportJsonFeature::getElements)
                .flatMap(Collection::stream).toList()
                .stream().mapToLong(ReportJsonFeature.Element::getTotalScenarioDuration)
                .sum();
        LocalDateTime endTime = startTime.plus(Duration.ofNanos(totalDuration));
        String totalExecutionDuration = this.getReadableTime(totalDuration);

        projectInfo.put("startTime", startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS")));
        projectInfo.put("endTime", endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS")));
        projectInfo.put("totalDuration", totalExecutionDuration);

        htmlDataSet.setProjectInfo(projectInfo);
    }

    private void processStep() {
        features
                .forEach(feature -> {
                    feature.getElements().forEach(testCase -> {
                        testCase.getSteps().forEach(step -> {
                            step.setStatus(step.getResult().getStatus());
                            long beforeDuration = step.getBefore().stream().map(ReportJsonFeature.Before::getResult).map(ReportJsonFeature.Result::getDuration).mapToLong(Long::longValue).sum();
                            long afterDuration = step.getAfter().stream().map(ReportJsonFeature.After::getResult).map(ReportJsonFeature.Result::getDuration).mapToLong(Long::longValue).sum();
                            step.setTotalStepDuration(beforeDuration + step.getResult().getDuration() + afterDuration);
                        });
                    });
                });
    }

    private void processTestCase() {
        features
                .forEach(feature -> {
                    feature.getElements().forEach(testCase -> {
                        long beforeDuration = testCase.getBefore().stream().map(ReportJsonFeature.Before::getResult).mapToLong(ReportJsonFeature.Result::getDuration).sum();
                        long afterDuration = testCase.getAfter().stream().map(ReportJsonFeature.After::getResult).mapToLong(ReportJsonFeature.Result::getDuration).sum();
                        long stepsDuration = testCase.getSteps().stream().mapToLong(ReportJsonFeature.Step::getTotalStepDuration).sum();
                        testCase.setTotalScenarioDuration(beforeDuration + stepsDuration + afterDuration);

                        Set<String> stepStatus = testCase.getSteps().stream().map(ReportJsonFeature.Step::getStatus).collect(toSet());
                        System.out.println(testCase.getName() + " : " + stepStatus);


                        if (stepStatus.contains(FAILED.getStatus()))
                            testCase.setStatus(FAILED.getStatus());
                        else if (stepStatus.stream().allMatch(status -> status.equals(PASSED.getStatus())))
                            testCase.setStatus(PASSED.getStatus());
                        else if (Set.of(PASSED.getStatus(), KNOWN_FAILURES.getStatus(), SKIPPED.getStatus()).containsAll(stepStatus)
                                || Set.of(PASSED.getStatus(), KNOWN_FAILURES.getStatus()).containsAll(stepStatus))
                            testCase.setStatus(PASSED_WITH_KNOWN_FAILURES.getStatus());
                        else if (stepStatus.stream().allMatch(status -> status.equals(KNOWN_FAILURES.getStatus())))
                            testCase.setStatus(KNOWN_FAILURES.getStatus());
                        else
                            testCase.setStatus(SKIPPED.getStatus());
                    });
                });
    }

    private void processFeature() {
        features
                .forEach(feature -> {
                    long featureDuration = feature.getElements().stream().mapToLong(ReportJsonFeature.Element::getTotalScenarioDuration).sum();
                    feature.setTotalFeatureDuration(featureDuration);

                    Set<String> testCaseStatus = feature.getElements().stream().map(ReportJsonFeature.Element::getStatus).collect(toSet());
                    System.out.println(feature.getName() + " : " + testCaseStatus);

                    if (testCaseStatus.contains(EasyReportStatus.FAILED.getStatus()))
                        feature.setStatus(EasyReportStatus.FAILED.getStatus());
                    else if (testCaseStatus.stream().allMatch(status -> status.equals(EasyReportStatus.PASSED.getStatus())))
                        feature.setStatus(EasyReportStatus.PASSED.getStatus());
                    else if (Set.of(PASSED.getStatus(), KNOWN_FAILURES.getStatus(), PASSED_WITH_KNOWN_FAILURES.getStatus(), SKIPPED.getStatus()).containsAll(testCaseStatus)
                            || Set.of(PASSED.getStatus(), KNOWN_FAILURES.getStatus(), PASSED_WITH_KNOWN_FAILURES.getStatus()).containsAll(testCaseStatus)
                            || Set.of(PASSED.getStatus(), KNOWN_FAILURES.getStatus(), SKIPPED.getStatus()).containsAll(testCaseStatus)
                            || Set.of(PASSED.getStatus(), KNOWN_FAILURES.getStatus()).containsAll(testCaseStatus)
                            || Set.of(PASSED.getStatus(), PASSED_WITH_KNOWN_FAILURES.getStatus(), SKIPPED.getStatus()).containsAll(testCaseStatus)
                            || Set.of(PASSED.getStatus(), PASSED_WITH_KNOWN_FAILURES.getStatus()).containsAll(testCaseStatus)
                            || Set.of(PASSED_WITH_KNOWN_FAILURES.getStatus()).containsAll(testCaseStatus)
                    )
                        feature.setStatus(PASSED_WITH_KNOWN_FAILURES.getStatus());
                    else if (testCaseStatus.stream().allMatch(status -> status.equals(EasyReportStatus.KNOWN_FAILURES.getStatus())))
                        feature.setStatus(EasyReportStatus.KNOWN_FAILURES.getStatus());
                    else
                        feature.setStatus(EasyReportStatus.SKIPPED.getStatus());

                });
    }

    private void generateDataForFeaturePieChart(List<ReportJsonFeature> features, HtmlDataSet htmlDataSet) {
        Map<String, Long> featurePieChartDataMap = new HashMap<>();
        List<Map<String, String>> featuresStats = new ArrayList<>();

        long featureCount = features.size();
        long featurePassCount = features.stream().filter(feature -> feature.getStatus().equals(EasyReportStatus.PASSED.getStatus())).count();
        long featureFailCount = features.stream().filter(feature -> feature.getStatus().equals(EasyReportStatus.FAILED.getStatus())).count();
        long featureKnownFailureCount = features.stream().filter(feature -> feature.getStatus().equals(EasyReportStatus.KNOWN_FAILURES.getStatus())).count();
        long featurePassWithKnownFailureCount = features.stream().filter(feature -> feature.getStatus().equals(PASSED_WITH_KNOWN_FAILURES.getStatus())).count();
        long featureSkipCount = features.stream().filter(feature -> feature.getStatus().equals(EasyReportStatus.SKIPPED.getStatus())).count();

        // prepare data for feature pie chart
        featurePieChartDataMap.put("totalFeatures", featureCount);
        featurePieChartDataMap.put(PASSED.getStatus(), featurePassCount);
        featurePieChartDataMap.put(FAILED.getStatus(), featureFailCount);
        featurePieChartDataMap.put(CaseUtils.toCamelCase(KNOWN_FAILURES.getStatus(), false), featureKnownFailureCount);
        featurePieChartDataMap.put(CaseUtils.toCamelCase(PASSED_WITH_KNOWN_FAILURES.getStatus(), false), featurePassWithKnownFailureCount);
        featurePieChartDataMap.put(SKIPPED.getStatus(), featureSkipCount);

        // prepare data for feature specific status for "Feature Status Summary" table
        features.forEach(feature -> {
            Map<String, String> featureStats = new HashMap<>();
            String featureName = feature.getName();
            DecimalFormat df = new DecimalFormat("## %");
            long totalCases = feature.getElements().size();
            long passCount = feature.getElements().stream().filter(testCase -> testCase.getStatus().equals(PASSED.getStatus())).count();
            long failCount = feature.getElements().stream().filter(testCase -> testCase.getStatus().equals(FAILED.getStatus())).count();
            long skippedCount = feature.getElements().stream().filter(testCase -> testCase.getStatus().equals(EasyReportStatus.SKIPPED.getStatus())).count();
            long knownFailCount = feature.getElements().stream().filter(testCase -> testCase.getStatus().equals(KNOWN_FAILURES.getStatus())).count();
            long passWithKnownFailCount = feature.getElements().stream().filter(testCase -> testCase.getStatus().equals(PASSED_WITH_KNOWN_FAILURES.getStatus())).count();

            String passPercent = passCount != 0 ? df.format(((Long) passCount).doubleValue() / totalCases) : "0 %";
            String failPercent = failCount != 0 ? df.format(((Long) failCount).doubleValue() / totalCases) : "0 %";
            String skipPercent = skippedCount != 0 ? df.format(((Long) skippedCount).doubleValue() / totalCases) : "0 %";
            String knownFailPercent = knownFailCount != 0 ? df.format(((Long) knownFailCount).doubleValue() / totalCases) : "0 %";
            String passWithKnownFailPercent = passWithKnownFailCount != 0 ? df.format(((Long) passWithKnownFailCount).doubleValue() / totalCases) : "0 %";

            featureStats.put("featureName", this.replaceEscapesWithHtml(featureName));
            featureStats.put("totalCases", String.valueOf(totalCases));
            featureStats.put(PASSED.getStatus(), String.valueOf(passCount));
            featureStats.put(FAILED.getStatus(), String.valueOf(failCount));
            featureStats.put(CaseUtils.toCamelCase(KNOWN_FAILURES.getStatus(), false), String.valueOf(knownFailCount));
            featureStats.put(CaseUtils.toCamelCase(PASSED_WITH_KNOWN_FAILURES.getStatus(), false), String.valueOf(passWithKnownFailCount));
            featureStats.put(SKIPPED.getStatus(), String.valueOf(skippedCount));
            featureStats.put("passPercent", passPercent);
            featureStats.put("failPercent", failPercent);
            featureStats.put("skippedPercent", skipPercent);
            featureStats.put("knownFailPercent", knownFailPercent);
            featureStats.put("passedWithKnownFailPercent", passWithKnownFailPercent);
            featureStats.put("status", feature.getStatus());
            System.out.println(feature.getTotalFeatureDuration());
            featureStats.put("duration", this.getReadableTime(feature.getTotalFeatureDuration()));
            featuresStats.add(featureStats);

        });

        htmlDataSet.setFeaturePieChartDataMap(featurePieChartDataMap);
        htmlDataSet.setFeaturesStats(featuresStats);
    }

    private String getReadableTime(Long elapsedTime) {
        Duration duration = Duration.ofNanos(elapsedTime);
        return String.format(
                "%1dh %1dm %1ds",
                duration.toHours() % 24, duration.toMinutes() % 60, duration.toSeconds() % 60);

    }

    private void generateDataForTestCasePieChart(List<ReportJsonFeature> features, HtmlDataSet htmlDataSet) {
        Map<String, Long> testCasePieChartDataMap = new HashMap<>();
        Map<String, String> overallTestCaseStats = new HashMap<>();
        DecimalFormat df = new DecimalFormat("## %");

        long testCaseCount = features.stream().mapToLong(feature -> feature.getElements().size()).sum();
        long testCasePassCount = features.stream().mapToLong(feature -> feature.getElements().stream().filter(testCase -> testCase.getStatus().equals("passed")).count()).sum();
        long testCaseFailCount = features.stream().mapToLong(feature -> feature.getElements().stream().filter(testCase -> testCase.getStatus().equals("failed")).count()).sum();
        long testCaseSkipCount = features.stream().mapToLong(feature -> feature.getElements().stream().filter(testCase -> testCase.getStatus().equals("skipped")).count()).sum();
        long testCaseKnownFailureCount = features.stream().mapToLong(feature -> feature.getElements().stream().filter(testCase -> testCase.getStatus().equals("known failures")).count()).sum();
        long testCasePassWithKnownFailureCount = features.stream().mapToLong(feature -> feature.getElements().stream().filter(testCase -> testCase.getStatus().equals("passed with known failures")).count()).sum();

        String passPercent = testCasePassCount != 0 ? df.format(((Long) testCasePassCount).doubleValue() / testCaseCount) : "0 %";
        String failPercent = testCaseFailCount != 0 ? df.format(((Long) testCaseFailCount).doubleValue() / testCaseCount) : "0 %";
        String skipPercent = testCaseSkipCount != 0 ? df.format(((Long) testCaseSkipCount).doubleValue() / testCaseCount) : "0 %";
        String knownFailPercent = testCaseKnownFailureCount != 0 ? df.format(((Long) testCaseKnownFailureCount).doubleValue() / testCaseCount) : "0 %";
        String passWithKnownFailPercent = testCasePassWithKnownFailureCount != 0 ? df.format(((Long) testCasePassWithKnownFailureCount).doubleValue() / testCaseCount) : "0 %";

        // prepare data for testcase pie chart
        testCasePieChartDataMap.put("totalCases", testCaseCount);
        testCasePieChartDataMap.put(PASSED.getStatus(), testCasePassCount);
        testCasePieChartDataMap.put(FAILED.getStatus(), testCaseFailCount);
        testCasePieChartDataMap.put(CaseUtils.toCamelCase(KNOWN_FAILURES.getStatus(), false), testCaseKnownFailureCount);
        testCasePieChartDataMap.put(CaseUtils.toCamelCase(PASSED_WITH_KNOWN_FAILURES.getStatus(), false), testCasePassWithKnownFailureCount);
        testCasePieChartDataMap.put(SKIPPED.getStatus(), testCaseSkipCount);

        // prepare data for overall testcases table summary
        overallTestCaseStats.put("totalCases", String.valueOf(testCaseCount));
        overallTestCaseStats.put(PASSED.getStatus(), String.valueOf(testCasePassCount));
        overallTestCaseStats.put(FAILED.getStatus(), String.valueOf(testCaseFailCount));
        overallTestCaseStats.put(CaseUtils.toCamelCase(KNOWN_FAILURES.getStatus(), false), String.valueOf(testCaseKnownFailureCount));
        overallTestCaseStats.put(CaseUtils.toCamelCase(PASSED_WITH_KNOWN_FAILURES.getStatus(), false), String.valueOf(testCasePassWithKnownFailureCount));
        overallTestCaseStats.put(SKIPPED.getStatus(), String.valueOf(testCaseSkipCount));
        overallTestCaseStats.put("passPercent", passPercent);
        overallTestCaseStats.put("failPercent", failPercent);
        overallTestCaseStats.put("skippedPercent", skipPercent);
        overallTestCaseStats.put("knownFailPercent", knownFailPercent);
        overallTestCaseStats.put("passedWithKnownFailPercent", passWithKnownFailPercent);

        long totalDuration = features.stream().map(ReportJsonFeature::getElements).flatMap(Collection::stream).toList()
                .stream().mapToLong(ReportJsonFeature.Element::getTotalScenarioDuration).sum();
        overallTestCaseStats.put("overallDuration", this.getReadableTime(totalDuration));

        Set<String> testCaseStatus = features.stream().map(ReportJsonFeature::getElements).flatMap(Collection::stream).toList()
                .stream().map(ReportJsonFeature.Element::getStatus).collect(toSet());

        if (testCaseStatus.contains(EasyReportStatus.FAILED.getStatus()))
            overallTestCaseStats.put("overAllStatus", FAILED.getStatus());
        else if (testCaseStatus.stream().allMatch(status -> status.equals(EasyReportStatus.PASSED.getStatus())))
            overallTestCaseStats.put("overAllStatus", PASSED.getStatus());
        else if (Set.of(PASSED.getStatus(), KNOWN_FAILURES.getStatus(), PASSED_WITH_KNOWN_FAILURES.getStatus(), SKIPPED.getStatus()).containsAll(testCaseStatus)
                || Set.of(PASSED.getStatus(), KNOWN_FAILURES.getStatus(), PASSED_WITH_KNOWN_FAILURES.getStatus()).containsAll(testCaseStatus)
                || Set.of(PASSED.getStatus(), KNOWN_FAILURES.getStatus(), SKIPPED.getStatus()).containsAll(testCaseStatus)
                || Set.of(PASSED.getStatus(), KNOWN_FAILURES.getStatus()).containsAll(testCaseStatus)
                || Set.of(PASSED.getStatus(), PASSED_WITH_KNOWN_FAILURES.getStatus(), SKIPPED.getStatus()).containsAll(testCaseStatus)
                || Set.of(PASSED.getStatus(), PASSED_WITH_KNOWN_FAILURES.getStatus()).containsAll(testCaseStatus)
                || Set.of(PASSED_WITH_KNOWN_FAILURES.getStatus()).containsAll(testCaseStatus)
        )
            overallTestCaseStats.put("overAllStatus", PASSED_WITH_KNOWN_FAILURES.getStatus());
        else if (testCaseStatus.stream().allMatch(status -> status.equals(EasyReportStatus.KNOWN_FAILURES.getStatus())))
            overallTestCaseStats.put("overAllStatus", KNOWN_FAILURES.getStatus());
        else
            overallTestCaseStats.put("overAllStatus", SKIPPED.getStatus());

        htmlDataSet.setOverallTestCaseStats(overallTestCaseStats);
        htmlDataSet.setTestCasePieChartDataMap(testCasePieChartDataMap);
        htmlDataSet.setOverAllExecutionStatus(overallTestCaseStats.get("overAllStatus"));
    }

    private void generateDataForTestStepPieChart(List<ReportJsonFeature> features, HtmlDataSet htmlDataSet) {
        Map<String, Long> testStepPieChartDataMap = new HashMap<>();

        List<ReportJsonFeature.Step> steps = features.stream().map(ReportJsonFeature::getElements).flatMap(Collection::stream).toList()
                .stream().map(ReportJsonFeature.Element::getSteps).flatMap(Collection::stream).toList();
        long totalStepsCount = steps.size();
        long stepPassCount = steps.stream().filter(step -> step.getStatus().equals(PASSED.getStatus())).count();
        long stepFailCount = steps.stream().filter(step -> step.getStatus().equals(FAILED.getStatus())).count();
        long stepSkipCount = steps.stream().filter(step -> step.getStatus().equals(SKIPPED.getStatus())).count();
        long stepKnownFailCount = steps.stream().filter(step -> step.getStatus().equals(KNOWN_FAILURES.getStatus())).count();
        long stepPendingCount = steps.stream().filter(step -> step.getStatus().equals(PENDING.getStatus())).count();
        long stepUndefinedCount = steps.stream().filter(step -> step.getStatus().equals(UNDEFINED.getStatus())).count();
        long stepAmbiguousCount = steps.stream().filter(step -> step.getStatus().equals(AMBIGUOUS.getStatus())).count();
        long stepUnused = steps.stream().filter(step -> step.getStatus().equals(UNUSED.getStatus())).count();

        testStepPieChartDataMap.put("totalSteps", totalStepsCount);
        testStepPieChartDataMap.put(PASSED.getStatus(), stepPassCount);
        testStepPieChartDataMap.put(FAILED.getStatus(), stepFailCount);
        testStepPieChartDataMap.put(SKIPPED.getStatus(), stepSkipCount);
        testStepPieChartDataMap.put(CaseUtils.toCamelCase(KNOWN_FAILURES.getStatus(), false), stepKnownFailCount);
        testStepPieChartDataMap.put(PENDING.getStatus(), stepPendingCount);
        testStepPieChartDataMap.put(UNDEFINED.getStatus(), stepUndefinedCount);
        testStepPieChartDataMap.put(AMBIGUOUS.getStatus(), stepAmbiguousCount);
        testStepPieChartDataMap.put(UNUSED.getStatus(), stepUnused);

        htmlDataSet.setTestStepPieChartDataMap(testStepPieChartDataMap);

    }

    private void generateFeaturesEntireResults(HtmlDataSet htmlDataSet) {
        List<Object> featureMapList = new ArrayList<>();

        features
                .forEach(feature -> {
                    Map<String, Object> featuresSet = new HashMap<>();
                    List<Object> scenarioMapList = new ArrayList<>();

                    List<ReportJsonFeature.Element> scenarios = feature.getElements();
                    scenarios
                            .forEach(scenario -> {
                                Map<String, Object> scenariosSet = new HashMap<>();

                                List<Object> stepMapList = new ArrayList<>();
                                List<ReportJsonFeature.Step> steps = scenario.getSteps();
                                steps
                                        .forEach(step -> {
                                            Map<String, Object> stepMap = new HashMap<>();
                                            stepMap.put("name", this.replaceEscapesWithHtml(step.getName()));
                                            stepMap.put("line", step.getLine());
                                            stepMap.put("duration", this.getReadableTime(step.getTotalStepDuration()));
                                            stepMap.put("status", step.getStatus());
                                            String stepError = step.getResult().getError_message();
                                            if (Objects.nonNull(stepError)) {
                                                stepError = this.replaceEscapesWithHtml(stepError);
                                            }
                                            stepMap.put("error", stepError);

                                            List<ReportJsonFeature.Embedding> beforeScreenshots = new ArrayList<>();
                                            List<ReportJsonFeature.Embedding> afterScreenshots = new ArrayList<>();
                                            beforeScreenshots = Optional.of(step.getBefore().stream().filter(Objects::nonNull).map(ReportJsonFeature.Before::getEmbeddings).filter(Objects::nonNull).flatMap(Collection::stream).toList()).orElse(new ArrayList<>());
                                            afterScreenshots = Optional.of(step.getAfter().stream().filter(Objects::nonNull).map(ReportJsonFeature.After::getEmbeddings).filter(Objects::nonNull).flatMap(Collection::stream).toList()).orElse(new ArrayList<>());
                                            List<ReportJsonFeature.Embedding> screenshots = Stream.concat(beforeScreenshots.stream(), afterScreenshots.stream()).toList();

                                            if(!screenshots.isEmpty()) {
                                                screenshots.forEach(embedding -> {
                                                    if(embedding.getMime_type().equals("text/plain")) {
                                                        byte[] byteArray = Base64.getDecoder().decode(embedding.getData());
                                                        String text = new String(byteArray, StandardCharsets.UTF_8);
                                                        embedding.setData(this.replaceEscapesWithHtml(text));
                                                        embedding.setName(this.replaceEscapesWithHtml(embedding.getName()));
                                                    }
                                                });
                                            }

                                            stepMap.put("embeddings", screenshots);
                                            stepMapList.add(stepMap);
                                        });
                                scenariosSet.put("id", this.replaceEscapesWithHtml(scenario.getId()));
                                scenariosSet.put("name", this.replaceEscapesWithHtml(scenario.getName()));
                                scenariosSet.put("status", scenario.getStatus());
                                scenariosSet.put("duration", this.getReadableTime(scenario.getTotalScenarioDuration()));
                                scenariosSet.put("steps", stepMapList);
                                scenarioMapList.add(scenariosSet);
                            });
                    featuresSet.put("id", this.replaceEscapesWithHtml(feature.getId()));
                    featuresSet.put("name", this.replaceEscapesWithHtml(feature.getName()));
                    featuresSet.put("status", feature.getStatus());
                    featuresSet.put("duration", this.getReadableTime(feature.getTotalFeatureDuration()));
                    featuresSet.put("scenarios", scenarioMapList);
                    featureMapList.add(featuresSet);
                });

        htmlDataSet.setFeatureMapList(featureMapList);

    }

    private String replaceEscapesWithHtml(String value) {
        return value.replaceAll("\r", "<br>")
                .replaceAll("\n", "<br>")
                .replaceAll("\t", "&emsp")
                .replaceAll("\"", "'")
                .replaceAll(";", "-");
    }

}
