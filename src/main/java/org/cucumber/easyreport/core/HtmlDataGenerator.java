package org.cucumber.easyreport.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.cucumber.core.exception.ExceptionUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.cucumber.easyreport.exception.EasyReportException;
import org.cucumber.easyreport.pojo.ReportJsonFeature;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
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
        this.generateDefectsStats(htmlDataSet);
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
        projectInfo.put("descriptionOrReleaseNotes", configReader.getProjectDescription());

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
        projectInfo.put("projectManager", configReader.getProjectManger());
        projectInfo.put("dqManager", configReader.getDeliveryQualityManager());
        projectInfo.put("dqLead", configReader.getDeliveryQualityLead());
        projectInfo.put("releaseName", configReader.getProdReleaseName());
        projectInfo.put("releaseDate", configReader.getProdReleaseDate());
        projectInfo.put("sprint", configReader.getSprintName());

        htmlDataSet.setProjectInfo(projectInfo);
    }

    private void processStep() {
        features
                .forEach(feature -> {
                    feature.getElements().forEach(testCase -> {
                        testCase.getSteps().forEach(step -> {
                            step.setStepSeq("Step " + (testCase.getSteps().indexOf(step) + 1));
                            ReportJsonFeature.Before failedBefore = step.getBefore().stream().filter(before -> !Set.of(PASSED.getStatus(), SKIPPED.getStatus()).contains(before.getResult().getStatus())).findFirst().orElse(null);
                            if(Objects.nonNull(failedBefore)) {
                                step.setBeforeStatus(failedBefore.getResult().getStatus());
                                step.setBeforeError(this.replaceEscapesWithHtml(failedBefore.getResult().getError_message()));
                            }

                            ReportJsonFeature.After failedAfter = step.getAfter().stream().filter(after -> !Set.of(PASSED.getStatus(), SKIPPED.getStatus()).contains(after.getResult().getStatus())).findFirst().orElse(null);
                            if(Objects.nonNull(failedAfter)) {
                                step.setAfterStatus(failedAfter.getResult().getStatus());
                                step.setAfterError(this.replaceEscapesWithHtml(failedAfter.getResult().getError_message()));
                            }

                            if(step.getBeforeStatus().equals(FAILED.getStatus()) || step.getAfterStatus().equals(FAILED.getStatus()))
                                step.setStepFinalStatus(FAILED.getStatus());
                            else
                                step.setStepFinalStatus(step.getResult().getStatus());

                            step.setStepStatus(step.getResult().getStatus());

                            if (Objects.nonNull(step.getResult().getError_message())) {
                                step.setStepError(this.replaceEscapesWithHtml(step.getResult().getError_message()));
                            }
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
                        testCase.setScenarioSeq("Scenario " + (feature.getElements().indexOf(testCase) + 1));
                        List<ReportJsonFeature.Before> beforeList = Optional.ofNullable(testCase.getBefore()).orElse(new ArrayList<>());
                        List<ReportJsonFeature.After> afterList = Optional.ofNullable(testCase.getAfter()).orElse(new ArrayList<>());

                        ReportJsonFeature.Before failedBefore = beforeList.stream().filter(before -> !Set.of(PASSED.getStatus(), SKIPPED.getStatus()).contains(before.getResult().getStatus())).findFirst().orElse(null);
                        if(Objects.nonNull(failedBefore)) {
                            testCase.setBeforeStatus(failedBefore.getResult().getStatus());
                            testCase.setBeforeError(this.replaceEscapesWithHtml(failedBefore.getResult().getError_message()));
                        }

                        ReportJsonFeature.After failedAfter = afterList.stream().filter(after -> !Set.of(PASSED.getStatus(), SKIPPED.getStatus()).contains(after.getResult().getStatus())).findFirst().orElse(null);
                        if(Objects.nonNull(failedAfter)) {
                            testCase.setAfterStatus(failedAfter.getResult().getStatus());
                            testCase.setAfterError(this.replaceEscapesWithHtml(failedAfter.getResult().getError_message()));
                        }

                        if(testCase.getBeforeStatus().equals(FAILED.getStatus()) || testCase.getAfterStatus().equals(FAILED.getStatus()))
                            testCase.setScenarioStatus(FAILED.getStatus());
                        else {
                            Set<String> consolidatedStatus = testCase.getSteps().stream().map(ReportJsonFeature.Step::getStepFinalStatus).collect(toSet());

                            if (consolidatedStatus.contains(FAILED.getStatus()))
                                testCase.setScenarioStatus(FAILED.getStatus());
                            else if (consolidatedStatus.stream().allMatch(status -> status.equals(PASSED.getStatus())))
                                testCase.setScenarioStatus(PASSED.getStatus());
                            else if (consolidatedStatus.contains(KNOWN_FAILURES.getStatus()))
                                testCase.setScenarioStatus(KNOWN_FAILURES.getStatus());
                            else
                                testCase.setScenarioStatus(SKIPPED.getStatus());
                        }

                        long beforeDuration = beforeList.stream().map(ReportJsonFeature.Before::getResult).mapToLong(ReportJsonFeature.Result::getDuration).sum();
                        long afterDuration = afterList.stream().map(ReportJsonFeature.After::getResult).mapToLong(ReportJsonFeature.Result::getDuration).sum();
                        long stepsDuration = testCase.getSteps().stream().mapToLong(ReportJsonFeature.Step::getTotalStepDuration).sum();
                        testCase.setTotalScenarioDuration(beforeDuration + stepsDuration + afterDuration);
                    });
                });
    }

    private void processFeature() {
        features
                .forEach(feature -> {
                    feature.setFeatureSeq("Feature " + (features.indexOf(feature) + 1));
                    long featureDuration = feature.getElements().stream().mapToLong(ReportJsonFeature.Element::getTotalScenarioDuration).sum();
                    feature.setTotalFeatureDuration(featureDuration);

                    Set<String> testCaseStatus = feature.getElements().stream().map(ReportJsonFeature.Element::getScenarioStatus).collect(toSet());
                    System.out.println(feature.getName() + " : " + testCaseStatus);

                    if (testCaseStatus.contains(EasyReportStatus.FAILED.getStatus()))
                        feature.setStatus(EasyReportStatus.FAILED.getStatus());
                    else if (testCaseStatus.stream().allMatch(status -> status.equals(EasyReportStatus.PASSED.getStatus())))
                        feature.setStatus(EasyReportStatus.PASSED.getStatus());
                    else if (testCaseStatus.containsAll(Set.of(PASSED.getStatus(), KNOWN_FAILURES.getStatus())))
                        feature.setStatus(PASSED_WITH_KNOWN_FAILURES.getStatus());
                    else if (testCaseStatus.contains(KNOWN_FAILURES.getStatus()))
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
            long passCount = feature.getElements().stream().filter(testCase -> testCase.getScenarioStatus().equals(PASSED.getStatus())).count();
            long failCount = feature.getElements().stream().filter(testCase -> testCase.getScenarioStatus().equals(FAILED.getStatus())).count();
            long skippedCount = feature.getElements().stream().filter(testCase -> testCase.getScenarioStatus().equals(EasyReportStatus.SKIPPED.getStatus())).count();
            long knownFailCount = feature.getElements().stream().filter(testCase -> testCase.getScenarioStatus().equals(KNOWN_FAILURES.getStatus())).count();

            String passPercent = passCount != 0 ? df.format(((Long) passCount).doubleValue() / totalCases) : "0 %";
            String failPercent = failCount != 0 ? df.format(((Long) failCount).doubleValue() / totalCases) : "0 %";
            String skipPercent = skippedCount != 0 ? df.format(((Long) skippedCount).doubleValue() / totalCases) : "0 %";
            String knownFailPercent = knownFailCount != 0 ? df.format(((Long) knownFailCount).doubleValue() / totalCases) : "0 %";

            featureStats.put("featureName", this.replaceEscapesWithHtml(featureName));
            featureStats.put("totalCases", String.valueOf(totalCases));
            featureStats.put(PASSED.getStatus(), String.valueOf(passCount));
            featureStats.put(FAILED.getStatus(), String.valueOf(failCount));
            featureStats.put(CaseUtils.toCamelCase(KNOWN_FAILURES.getStatus(), false), String.valueOf(knownFailCount));
            featureStats.put(SKIPPED.getStatus(), String.valueOf(skippedCount));
            featureStats.put("passPercent", passPercent);
            featureStats.put("failPercent", failPercent);
            featureStats.put("skippedPercent", skipPercent);
            featureStats.put("knownFailPercent", knownFailPercent);
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
        long testCasePassCount = features.stream().mapToLong(feature -> feature.getElements().stream().filter(testCase -> testCase.getScenarioStatus().equals("passed")).count()).sum();
        long testCaseFailCount = features.stream().mapToLong(feature -> feature.getElements().stream().filter(testCase -> testCase.getScenarioStatus().equals("failed")).count()).sum();
        long testCaseSkipCount = features.stream().mapToLong(feature -> feature.getElements().stream().filter(testCase -> testCase.getScenarioStatus().equals("skipped")).count()).sum();
        long testCaseKnownFailureCount = features.stream().mapToLong(feature -> feature.getElements().stream().filter(testCase -> testCase.getScenarioStatus().equals("known failures")).count()).sum();

        String passPercent = testCasePassCount != 0 ? df.format(((Long) testCasePassCount).doubleValue() / testCaseCount) : "0 %";
        String failPercent = testCaseFailCount != 0 ? df.format(((Long) testCaseFailCount).doubleValue() / testCaseCount) : "0 %";
        String skipPercent = testCaseSkipCount != 0 ? df.format(((Long) testCaseSkipCount).doubleValue() / testCaseCount) : "0 %";
        String knownFailPercent = testCaseKnownFailureCount != 0 ? df.format(((Long) testCaseKnownFailureCount).doubleValue() / testCaseCount) : "0 %";

        // prepare data for testcase pie chart
        testCasePieChartDataMap.put("totalCases", testCaseCount);
        testCasePieChartDataMap.put(PASSED.getStatus(), testCasePassCount);
        testCasePieChartDataMap.put(FAILED.getStatus(), testCaseFailCount);
        testCasePieChartDataMap.put(SKIPPED.getStatus(), testCaseSkipCount);
        testCasePieChartDataMap.put(CaseUtils.toCamelCase(KNOWN_FAILURES.getStatus(), false), testCaseKnownFailureCount);

        // prepare data for overall testcases table summary
        overallTestCaseStats.put("totalCases", String.valueOf(testCaseCount));
        overallTestCaseStats.put(PASSED.getStatus(), String.valueOf(testCasePassCount));
        overallTestCaseStats.put(FAILED.getStatus(), String.valueOf(testCaseFailCount));
        overallTestCaseStats.put(SKIPPED.getStatus(), String.valueOf(testCaseSkipCount));
        overallTestCaseStats.put(CaseUtils.toCamelCase(KNOWN_FAILURES.getStatus(), false), String.valueOf(testCaseKnownFailureCount));
        overallTestCaseStats.put("passPercent", passPercent);
        overallTestCaseStats.put("failPercent", failPercent);
        overallTestCaseStats.put("skippedPercent", skipPercent);
        overallTestCaseStats.put("knownFailPercent", knownFailPercent);

        long totalDuration = features.stream().map(ReportJsonFeature::getElements).flatMap(Collection::stream).toList()
                .stream().mapToLong(ReportJsonFeature.Element::getTotalScenarioDuration).sum();
        overallTestCaseStats.put("overallDuration", this.getReadableTime(totalDuration));

        Set<String> testCaseStatus = features.stream().map(ReportJsonFeature::getElements).flatMap(Collection::stream).toList()
                .stream().map(ReportJsonFeature.Element::getScenarioStatus).collect(toSet());

        if (testCaseStatus.contains(EasyReportStatus.FAILED.getStatus()))
            overallTestCaseStats.put("overAllStatus", FAILED.getStatus());
        else if (testCaseStatus.stream().allMatch(status -> status.equals(EasyReportStatus.PASSED.getStatus())))
            overallTestCaseStats.put("overAllStatus", PASSED.getStatus());
        else if (testCaseStatus.contains(KNOWN_FAILURES.getStatus()))
            overallTestCaseStats.put("overAllStatus", PASSED_WITH_KNOWN_FAILURES.getStatus());
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
        long stepPassCount = steps.stream().filter(step -> step.getStepFinalStatus().equals(PASSED.getStatus())).count();
        long stepFailCount = steps.stream().filter(step -> step.getStepFinalStatus().equals(FAILED.getStatus())).count();
        long stepSkipCount = steps.stream().filter(step -> step.getStepFinalStatus().equals(SKIPPED.getStatus())).count();
        long stepKnownFailCount = steps.stream().filter(step -> step.getStepFinalStatus().equals(KNOWN_FAILURES.getStatus())).count();
        long stepPendingCount = steps.stream().filter(step -> step.getStepFinalStatus().equals(PENDING.getStatus())).count();
        long stepUndefinedCount = steps.stream().filter(step -> step.getStepFinalStatus().equals(UNDEFINED.getStatus())).count();
        long stepAmbiguousCount = steps.stream().filter(step -> step.getStepFinalStatus().equals(AMBIGUOUS.getStatus())).count();
        long stepUnused = steps.stream().filter(step -> step.getStepFinalStatus().equals(UNUSED.getStatus())).count();

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
                                            stepMap.put("stepSeq", step.getStepSeq());
                                            stepMap.put("name", this.getEncodedText(step.getStepSeq() + " : " + this.replaceEscapesWithHtml(step.getName())));
                                            stepMap.put("line", step.getLine());
                                            stepMap.put("duration", this.getReadableTime(step.getTotalStepDuration()));
                                            stepMap.put("beforeStatus", step.getBeforeStatus());
                                            stepMap.put("beforeError", this.getEncodedText(step.getBeforeError()));
                                            stepMap.put("status", step.getStepStatus());
                                            stepMap.put("error", this.getEncodedText(step.getStepError()));
                                            stepMap.put("afterStatus", step.getAfterStatus());
                                            stepMap.put("afterError", this.getEncodedText(step.getAfterError()));
                                            stepMap.put("finalStatus", step.getStepFinalStatus());

                                            List<ReportJsonFeature.Embedding> beforeScreenshots = new ArrayList<>();
                                            List<ReportJsonFeature.Embedding> afterScreenshots = new ArrayList<>();
                                            beforeScreenshots = Optional.of(step.getBefore().stream().filter(Objects::nonNull).map(ReportJsonFeature.Before::getEmbeddings).filter(Objects::nonNull).flatMap(Collection::stream).toList()).orElse(new ArrayList<>());
                                            afterScreenshots = Optional.of(step.getAfter().stream().filter(Objects::nonNull).map(ReportJsonFeature.After::getEmbeddings).filter(Objects::nonNull).flatMap(Collection::stream).toList()).orElse(new ArrayList<>());
                                            List<ReportJsonFeature.Embedding> screenshots = Stream.concat(beforeScreenshots.stream(), afterScreenshots.stream()).toList();

                                            if(!screenshots.isEmpty()) {
                                                screenshots.forEach(embedding -> {
                                                    embedding.setName(this.getEncodedText(this.replaceEscapesWithHtml(embedding.getName())));
                                                    if(embedding.getMime_type().equals("text/plain")) {
                                                        byte[] byteArray = Base64.getDecoder().decode(embedding.getData());
                                                        String text = new String(byteArray, StandardCharsets.UTF_8);
                                                        embedding.setData(this.replaceEscapesWithHtml(text));
                                                    }
                                                });
                                            }

                                            stepMap.put("embeddings", screenshots);
                                            stepMapList.add(stepMap);
                                        });
                                scenariosSet.put("id", this.replaceEscapesWithHtml(scenario.getId()));
                                scenariosSet.put("scenarioSeq", scenario.getScenarioSeq());
                                scenariosSet.put("name", this.getEncodedText(scenario.getScenarioSeq() + " : " + this.replaceEscapesWithHtml(scenario.getName())));
                                scenariosSet.put("duration", this.getReadableTime(scenario.getTotalScenarioDuration()));
                                scenariosSet.put("beforeStatus", scenario.getBeforeStatus());
                                scenariosSet.put("beforeError", this.getEncodedText(scenario.getBeforeError()));
                                scenariosSet.put("status", scenario.getScenarioStatus());
                                scenariosSet.put("afterStatus", scenario.getAfterStatus());
                                scenariosSet.put("afterError", this.getEncodedText(scenario.getAfterError()));
                                scenariosSet.put("steps", stepMapList);
                                scenarioMapList.add(scenariosSet);
                            });
                    featuresSet.put("id", this.replaceEscapesWithHtml(feature.getId()));
                    featuresSet.put("featureSeq", feature.getFeatureSeq());
                    featuresSet.put("name", this.getEncodedText(feature.getFeatureSeq() + " : " + this.replaceEscapesWithHtml(feature.getName())));
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

    private String getEncodedText(String value) {
        return Objects.nonNull(value) ? Base64.getEncoder().encodeToString(value.getBytes()) : value;
    }

    private void generateDefectsStats(HtmlDataSet htmlDataSet) throws JsonProcessingException {
        features
                .forEach( feature -> {
                    String featureNameReference = feature.getFeatureSeq() + " : " + feature.getName();
                    List<ReportJsonFeature.Element> scenarios = feature.getElements();

                    scenarios.stream()
                            .filter(scenario -> !Set.of(PASSED.getStatus(), SKIPPED.getStatus()).contains(scenario.getScenarioStatus()))
                            .forEach(failedScenario -> {
                                List<ReportJsonFeature.Step> steps = failedScenario.getSteps();
                                ReportJsonFeature.Step failedStep = steps.stream().filter(step -> !ignorableStatus.contains(step.getStepFinalStatus())).findFirst().orElse(new ReportJsonFeature.Step());
                                this.categorizeFailures(featureNameReference, failedScenario, failedStep);
                            });
                });

        Map<String, List<Cause>> knownFailuresGroupedByTrackId = knownFailures.stream().collect(Collectors.groupingBy(Cause::getTrackingId, Collectors.toList()));
        long trackedKnownDefects = knownFailuresGroupedByTrackId.keySet().stream().filter(id -> !id.equals("No Tracking Id")).count();
        long unTrackedKnownDefects = Optional.ofNullable(knownFailuresGroupedByTrackId.get("No Tracking Id")).orElse(new ArrayList<>())
                .stream().collect(groupingBy(Cause::getLabel, Collectors.toList())).keySet().size();

        long newDefects = newFailures.size();
        long otherDefects = otherFailures.size();
//        knownFailuresGroupedByTrackId.entrySet().forEach(System.out::println);
//        System.out.println(trackedKnownDefects);
//        System.out.println(unTrackedKnownDefects);
//        newFailures.forEach(System.out::println);
//        System.out.println(newDefects);
//        otherFailures.forEach(System.out::println);
//        System.out.println(otherDefects);

        htmlDataSet.getDefectPieChartDataMap().put("trackedKnownDefects", trackedKnownDefects);
        htmlDataSet.getDefectPieChartDataMap().put("unTrackedKnownDefects", unTrackedKnownDefects);
        htmlDataSet.getDefectPieChartDataMap().put("newDefects", newDefects);
        htmlDataSet.getDefectPieChartDataMap().put("otherDefects", otherDefects);

        defectDetails.put("trackedKnownDefectIds", knownFailuresGroupedByTrackId.keySet().stream().filter(id -> !id.equals("No Tracking Id")).toList());
        defectDetails.put("knownFailures", knownFailures);
        defectDetails.put("newFailures", newFailures);
        defectDetails.put("otherFailures", otherFailures);
        htmlDataSet.setDefectDetails(defectDetails);
    }

    Map<String, Object> defectDetails = new HashMap<>();
    List<Cause> knownFailures = new ArrayList<>();
    List<Cause> newFailures = new ArrayList<>();
    List<Cause> otherFailures = new ArrayList<>();
    Set<String> ignorableStatus = Set.of(PASSED.getStatus(), SKIPPED.getStatus());

    private void categorizeFailures(String featureName, ReportJsonFeature.Element failedScenario, ReportJsonFeature.Step failedStep) {

        String scenarioNameReference = failedScenario.getScenarioSeq() + " : " + failedScenario.getName();

        List<ReportJsonFeature.Before> scenarioBeforeList = Optional.ofNullable(failedScenario.getBefore()).orElse(new ArrayList<>());
        if(!scenarioBeforeList.isEmpty()) {
            ReportJsonFeature.Before scenarioBeforeFailure = scenarioBeforeList.stream().filter(before -> !ignorableStatus.contains(before.getResult().getStatus())).findFirst().orElse(null);
            String scenarioBeforeError = Objects.nonNull(scenarioBeforeFailure) ? scenarioBeforeFailure.getResult().getError_message() : StringUtils.EMPTY;
            this.addFailuresIntoCategory(featureName, scenarioNameReference, "scenario before hook", scenarioBeforeError);
        }

        List<ReportJsonFeature.Before> stepBeforeList = Optional.ofNullable(failedStep.getBefore()).orElse(new ArrayList<>());
        if(!stepBeforeList.isEmpty()) {
            String stepNameRef = failedStep.getStepSeq() + " before hook error";
            ReportJsonFeature.Before stepBeforeFailure = stepBeforeList.stream().filter(before -> !ignorableStatus.contains(before.getResult().getStatus())).findFirst().orElse(null);
            String stepBeforeError = Objects.nonNull(stepBeforeFailure) ? stepBeforeFailure.getResult().getError_message() : StringUtils.EMPTY;
            this.addFailuresIntoCategory(featureName, scenarioNameReference, stepNameRef, stepBeforeError);
        }

        String stepError = Optional.ofNullable(failedStep.getResult()).orElse(new ReportJsonFeature.Result()).getError_message();
        String stepNameReference = "";
        if(Objects.nonNull(failedStep.getStepSeq())) {
            stepNameReference = failedStep.getStepSeq() + " : " + failedStep.getName();
        }
        stepError = Objects.nonNull(stepError) ? stepError : StringUtils.EMPTY;
        this.addFailuresIntoCategory(featureName, scenarioNameReference, stepNameReference, stepError);

        List<ReportJsonFeature.After> stepAfterList = Optional.ofNullable(failedStep.getAfter()).orElse(new ArrayList<>());
        if(!stepAfterList.isEmpty()) {
            String stepNameRef = failedStep.getStepSeq() + " after hook error";
            ReportJsonFeature.After stepAfterFailure = stepAfterList.stream().filter(after -> !ignorableStatus.contains(after.getResult().getStatus())).findFirst().orElse(null);
            String stepAfterError = Objects.nonNull(stepAfterFailure) ? stepAfterFailure.getResult().getError_message() : StringUtils.EMPTY;
            this.addFailuresIntoCategory(featureName, scenarioNameReference, stepNameRef, stepAfterError);
        }

        List<ReportJsonFeature.After> scenarioAfterList = Optional.ofNullable(failedScenario.getAfter()).orElse(new ArrayList<>());
        if(!scenarioAfterList.isEmpty()) {
            ReportJsonFeature.After scenarioAfterFailure = scenarioAfterList.stream().filter(after -> !ignorableStatus.contains(after.getResult().getStatus())).findFirst().orElse(null);
            String scenarioAfterError = Objects.nonNull(scenarioAfterFailure) ? scenarioAfterFailure.getResult().getError_message() : StringUtils.EMPTY;
            this.addFailuresIntoCategory(featureName, scenarioNameReference, "scenario after hook", scenarioAfterError);
        }
    }

    private void addFailuresIntoCategory(String featureName, String scenarioName, String stepName, String errorText) {
        try {
            if(errorText.contains("{") && errorText.contains("}")) {
                errorText = errorText.substring(errorText.indexOf("{"), errorText.lastIndexOf("}") + 1);
                ObjectMapper oMapper = new ObjectMapper();
                oMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                Error error = oMapper.readValue(errorText, Error.class);

                if(!error.getKnownFailures().isEmpty()) {
                    error.getKnownFailures().forEach(f -> {
                        f.setFeatureName(this.getEncodedText(featureName));
                        f.setScenarioName(this.getEncodedText(scenarioName));
                        f.setStepName(this.getEncodedText(stepName));
                        f.setFailureMessage(this.getEncodedText(f.getFailureMessage()));
                    });
                    knownFailures.addAll(error.getKnownFailures());
                }

                if(!error.getFailures().isEmpty()){
                    error.getFailures().forEach(f -> {
                        f.setFeatureName(this.getEncodedText(featureName));
                        f.setScenarioName(this.getEncodedText(scenarioName));
                        f.setStepName(this.getEncodedText(stepName));
                        f.setFailureMessage(this.getEncodedText(f.getFailureMessage()));
                    });
                    newFailures.addAll(error.getFailures());
                }
            } else if(!errorText.isBlank()){
                Cause cause = new Cause();
                cause.setFeatureName(this.getEncodedText(featureName));
                cause.setScenarioName(this.getEncodedText(scenarioName));
                cause.setStepName(this.getEncodedText(stepName));
                cause.setLabel("otherFailures");
                cause.setTrackingId("No Tracking Id");
                cause.setFailureMessage(this.getEncodedText(errorText));
                otherFailures.add(cause);
            }
        } catch (JsonProcessingException e) {
            // swallow exception. In case of parse exception, mark it failed
            e.printStackTrace();
        } catch (Exception e) {
            // for other exceptions, throw it
            throw new EasyReportException(e.getMessage());
        }
    }

    @Data
    public static class Error {
        private List<Cause> failures = new ArrayList<>();
        private List<Cause> knownFailures = new ArrayList<>();
    }

    @Data
    public static class Cause {
        private String label;
        private String failureMessage;
        private String trackingId;

        private String featureName;
        private String scenarioName;
        private String stepName;
    }

}
