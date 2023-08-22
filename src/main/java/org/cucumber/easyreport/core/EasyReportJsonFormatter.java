package org.cucumber.easyreport.core;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import org.cucumber.easyreport.assertions.Assertions;
import org.cucumber.easyreport.exception.EasyReportException;
import org.cucumber.easyreport.pojo.ReportJsonFeature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import io.cucumber.core.exception.ExceptionUtils;
import io.cucumber.messages.types.Background;
import io.cucumber.messages.types.Feature;
import io.cucumber.messages.types.Scenario;
import io.cucumber.messages.types.Step;
import io.cucumber.plugin.EventListener;
import io.cucumber.plugin.event.Argument;
import io.cucumber.plugin.event.DataTableArgument;
import io.cucumber.plugin.event.DocStringArgument;
import io.cucumber.plugin.event.EmbedEvent;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.HookTestStep;
import io.cucumber.plugin.event.HookType;
import io.cucumber.plugin.event.PickleStepTestStep;
import io.cucumber.plugin.event.Result;
import io.cucumber.plugin.event.Status;
import io.cucumber.plugin.event.StepArgument;
import io.cucumber.plugin.event.TestCase;
import io.cucumber.plugin.event.TestCaseStarted;
import io.cucumber.plugin.event.TestRunFinished;
import io.cucumber.plugin.event.TestSourceRead;
import io.cucumber.plugin.event.TestStep;
import io.cucumber.plugin.event.TestStepFinished;
import io.cucumber.plugin.event.TestStepStarted;
import io.cucumber.plugin.event.WriteEvent;
import lombok.SneakyThrows;

import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public final class EasyReportJsonFormatter implements EventListener {
    private static final String before = "before";
    private static final String after = "after";
    private final List<Map<String, Object>> featureMaps = new ArrayList<>();
    private final Map<String, Object> currentBeforeStepHookList = new HashMap<>();
    private final Writer jsonReportwriter;
    private final Writer htmlJsonDatawriter;
    private final OutputStream htmlReportOutputStream;
    private final EasyReportTestSourcesModel testSources = new EasyReportTestSourcesModel();
    private URI currentFeatureFile;
    private List<Map<String, Object>> currentElementsList;
    private Map<String, Object> currentElementMap;
    private Map<String, Object> currentTestCaseMap;
    private List<Map<String, Object>> currentStepsList;
    private Map<String, Object> currentStepOrHookMap;

    public EasyReportJsonFormatter(String htmlReportFilePath) {
        try {
            EasyReportConfigReader configReader = new EasyReportConfigReader();
            String jsonReporterFilePath = configReader.getJsonReportPath();
            String htmlJsonDataFilePath = configReader.getCustomizedJsonReportPath();

            // create folders of destinations if not exist
            Files.createDirectories(Paths.get(jsonReporterFilePath).getParent());
            Files.createDirectories(Paths.get(htmlJsonDataFilePath).getParent());
            Files.createDirectories(Paths.get(htmlReportFilePath).getParent());

            this.jsonReportwriter = new EasyReportUTF8OutputStreamWriter(new FileOutputStream(jsonReporterFilePath));
            this.htmlJsonDatawriter = new EasyReportUTF8OutputStreamWriter(new FileOutputStream(htmlJsonDataFilePath));
            this.htmlReportOutputStream = new FileOutputStream(htmlReportFilePath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestSourceRead.class, this::handleTestSourceRead);
        publisher.registerHandlerFor(TestCaseStarted.class, this::handleTestCaseStarted);
        publisher.registerHandlerFor(TestStepStarted.class, this::handleTestStepStarted);
        publisher.registerHandlerFor(TestStepFinished.class, this::handleTestStepFinished);
        publisher.registerHandlerFor(WriteEvent.class, this::handleWrite);
        publisher.registerHandlerFor(EmbedEvent.class, this::handleEmbed);
        publisher.registerHandlerFor(TestRunFinished.class, this::finishReport);
    }

    private void handleTestSourceRead(TestSourceRead event) {
        this.testSources.addTestSourceReadEvent(event.getUri(), event);
    }

    private void handleTestCaseStarted(TestCaseStarted event) {
        if (this.currentFeatureFile == null || !this.currentFeatureFile.equals(event.getTestCase().getUri())) {
            this.currentFeatureFile = event.getTestCase().getUri();
            Map<String, Object> currentFeatureMap = this.createFeatureMap(event.getTestCase());
            this.featureMaps.add(currentFeatureMap);
            this.currentElementsList = (List)currentFeatureMap.get("elements");
        }

        this.currentTestCaseMap = this.createTestCase(event);
        if (this.testSources.hasBackground(this.currentFeatureFile, event.getTestCase().getLocation().getLine())) {
            this.currentElementMap = this.createBackground(event.getTestCase());
            this.currentElementsList.add(this.currentElementMap);
        } else {
            this.currentElementMap = this.currentTestCaseMap;
        }

        this.currentElementsList.add(this.currentTestCaseMap);
        this.currentStepsList = (List)this.currentElementMap.get("steps");
    }

    private void handleTestStepStarted(TestStepStarted event) {
        if (event.getTestStep() instanceof PickleStepTestStep) {
            PickleStepTestStep testStep = (PickleStepTestStep)event.getTestStep();
            if (this.isFirstStepAfterBackground(testStep)) {
                this.currentElementMap = this.currentTestCaseMap;
                this.currentStepsList = (List)this.currentElementMap.get("steps");
            }

            this.currentStepOrHookMap = this.createTestStep(testStep);
            if (this.currentBeforeStepHookList.containsKey("before")) {
                this.currentStepOrHookMap.put("before", this.currentBeforeStepHookList.get("before"));
                this.currentBeforeStepHookList.clear();
            }

            this.currentStepsList.add(this.currentStepOrHookMap);
        } else {
            if (!(event.getTestStep() instanceof HookTestStep)) {
                throw new IllegalStateException();
            }

            HookTestStep hookTestStep = (HookTestStep)event.getTestStep();
            this.currentStepOrHookMap = this.createHookStep(hookTestStep);
            this.addHookStepToTestCaseMap(this.currentStepOrHookMap, hookTestStep.getHookType());
        }

    }

    @SneakyThrows
    private void handleTestStepFinished(TestStepFinished event) {
        this.currentStepOrHookMap.put("match", this.createMatchMap(event.getTestStep(), event.getResult()));
        this.currentStepOrHookMap.put("result", this.createResultMap(event.getResult(), event));
    }

    private void handleWrite(WriteEvent event) {
        this.addOutputToHookMap(event.getText());
    }

    private void handleEmbed(EmbedEvent event) {
        this.addEmbeddingToHookMap(event.getData(), event.getMediaType(), event.getName());
    }

    private void finishReport(TestRunFinished event) {
        Throwable exception = event.getResult().getError();
        if (exception != null) {
            this.featureMaps.add(this.createDummyFeatureForFailure(event));
        }

        try {
            // customized here
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            String node = mapper.writerWithDefaultPrettyPrinter() .writeValueAsString(this.featureMaps);
            List<ReportJsonFeature> reportJson = mapper.readValue(node, TypeFactory.defaultInstance().constructCollectionType(List.class, ReportJsonFeature.class));
            HtmlDataSet htmlJsonData = new HtmlDataGenerator(reportJson).getHtmlDataSet();
            mapper.writeValue(this.htmlJsonDatawriter, htmlJsonData);

            String reportJsonData = mapper.writerWithDefaultPrettyPrinter() .writeValueAsString(htmlJsonData);
            String sourceHtml = "/files/report.html";
            InputStream resource = EasyReportJsonFormatter.class.getResourceAsStream(sourceHtml);
            Objects.requireNonNull(resource, sourceHtml + " could not be loaded");
            this.writeHtmlReport(resource, reportJsonData);

            // customized here
            EasyReportJackson.OBJECT_MAPPER.writeValue(this.jsonReportwriter, this.featureMaps);
            this.jsonReportwriter.close();
        } catch (IOException var4) {
            throw new RuntimeException(var4);
        }
    }

    private void writeHtmlReport(InputStream resource, String replaceWith) throws IOException {

        try(BufferedReader in = new BufferedReader(new InputStreamReader(resource));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(htmlReportOutputStream))) {
            String line;
            String stringToReplace = "<<json-input>>";

            while((line=in.readLine())!=null)  {
                if (line.contains(stringToReplace))
                    line = line.replace(stringToReplace, replaceWith);
                out.write(line);
                out.newLine();
            }
        }
    }

    private Map<String, Object> createFeatureMap(TestCase testCase) {
        Map<String, Object> featureMap = new HashMap<>();
        featureMap.put("uri", EasyReportTestSourcesModel.relativize(testCase.getUri()));
        featureMap.put("elements", new ArrayList<>());
        Feature feature = this.testSources.getFeature(testCase.getUri());
        if (feature != null) {
            featureMap.put("keyword", feature.getKeyword());
            featureMap.put("name", feature.getName());
            featureMap.put("description", feature.getDescription() != null ? feature.getDescription() : "");
            featureMap.put("line", feature.getLocation().getLine());
            featureMap.put("id", EasyReportTestSourcesModel.convertToId(feature.getName()));
            featureMap.put("tags", feature.getTags().stream().map((tag) -> {
                Map<String, Object> json = new LinkedHashMap<>();
                json.put("name", tag.getName());
                json.put("type", "Tag");
                Map<String, Object> location = new LinkedHashMap<>();
                location.put("line", tag.getLocation().getLine());
                location.put("column", tag.getLocation().getColumn());
                json.put("location", location);
                return json;
            }).collect(Collectors.toList()));
        }

        return featureMap;
    }

    private Map<String, Object> createTestCase(TestCaseStarted event) {
        Map<String, Object> testCaseMap = new HashMap<>();
        testCaseMap.put("start_timestamp", this.getDateTimeFromTimeStamp(event.getInstant()));
        TestCase testCase = event.getTestCase();
        testCaseMap.put("name", testCase.getName());
        testCaseMap.put("line", testCase.getLocation().getLine());
        testCaseMap.put("type", "scenario");
        EasyReportTestSourcesModel.AstNode astNode = this.testSources.getAstNode(this.currentFeatureFile, testCase.getLocation().getLine());
        if (astNode != null) {
            testCaseMap.put("id", EasyReportTestSourcesModel.calculateId(astNode));
            Scenario scenarioDefinition = EasyReportTestSourcesModel.getScenarioDefinition(astNode);
            testCaseMap.put("keyword", scenarioDefinition.getKeyword());
            testCaseMap.put("description", scenarioDefinition.getDescription() != null ? scenarioDefinition.getDescription() : "");
        }

        testCaseMap.put("steps", new ArrayList<>());
        if (!testCase.getTags().isEmpty()) {
            List<Map<String, Object>> tagList = new ArrayList<>();
            Iterator<String> var6 = testCase.getTags().iterator();

            while(var6.hasNext()) {
                String tag = (String)var6.next();
                Map<String, Object> tagMap = new HashMap<>();
                tagMap.put("name", tag);
                tagList.add(tagMap);
            }

            testCaseMap.put("tags", tagList);
        }

        return testCaseMap;
    }

    private Map<String, Object> createBackground(TestCase testCase) {
        EasyReportTestSourcesModel.AstNode astNode = this.testSources.getAstNode(this.currentFeatureFile, testCase.getLocation().getLine());
        if (astNode != null) {
            Background background = (Background) EasyReportTestSourcesModel.getBackgroundForTestCase(astNode).get();
            Map<String, Object> testCaseMap = new HashMap();
            testCaseMap.put("name", background.getName());
            testCaseMap.put("line", background.getLocation().getLine());
            testCaseMap.put("type", "background");
            testCaseMap.put("keyword", background.getKeyword());
            testCaseMap.put("description", background.getDescription() != null ? background.getDescription() : "");
            testCaseMap.put("steps", new ArrayList());
            return testCaseMap;
        } else {
            return null;
        }
    }

    private boolean isFirstStepAfterBackground(PickleStepTestStep testStep) {
        EasyReportTestSourcesModel.AstNode astNode = this.testSources.getAstNode(this.currentFeatureFile, testStep.getStepLine());
        if (astNode == null) {
            return false;
        } else {
            return this.currentElementMap != this.currentTestCaseMap && !EasyReportTestSourcesModel.isBackgroundStep(astNode);
        }
    }

    private Map<String, Object> createTestStep(PickleStepTestStep testStep) {
        Map<String, Object> stepMap = new HashMap<>();
        stepMap.put("name", testStep.getStep().getText());
        stepMap.put("line", testStep.getStep().getLine());
        EasyReportTestSourcesModel.AstNode astNode = this.testSources.getAstNode(this.currentFeatureFile, testStep.getStepLine());
        StepArgument argument = testStep.getStep().getArgument();
        if (argument != null) {
            if (argument instanceof DocStringArgument) {
                DocStringArgument docStringArgument = (DocStringArgument)argument;
                stepMap.put("doc_string", this.createDocStringMap(docStringArgument));
            } else if (argument instanceof DataTableArgument) {
                DataTableArgument dataTableArgument = (DataTableArgument)argument;
                stepMap.put("rows", this.createDataTableList(dataTableArgument));
            }
        }

        if (astNode != null) {
            Step step = (Step)astNode.node;
            stepMap.put("keyword", step.getKeyword());
        }

        return stepMap;
    }

    private Map<String, Object> createHookStep(HookTestStep hookTestStep) {
        return new HashMap();
    }

    private void addHookStepToTestCaseMap(Map<String, Object> currentStepOrHookMap, HookType hookType) {
        String hookName;
        if (hookType != HookType.AFTER && hookType != HookType.AFTER_STEP) {
            hookName = "before";
        } else {
            hookName = "after";
        }

        Map mapToAddTo;
        switch (hookType) {
            case BEFORE:
                mapToAddTo = this.currentTestCaseMap;
                break;
            case AFTER:
                mapToAddTo = this.currentTestCaseMap;
                break;
            case BEFORE_STEP:
                mapToAddTo = this.currentBeforeStepHookList;
                break;
            case AFTER_STEP:
                mapToAddTo = (Map)this.currentStepsList.get(this.currentStepsList.size() - 1);
                break;
            default:
                mapToAddTo = this.currentTestCaseMap;
        }

        if (!mapToAddTo.containsKey(hookName)) {
            mapToAddTo.put(hookName, new ArrayList<>());
        }

        ((List)mapToAddTo.get(hookName)).add(currentStepOrHookMap);
    }

    private Map<String, Object> createMatchMap(TestStep step, Result result) {
        Map<String, Object> matchMap = new HashMap<>();
        if (step instanceof PickleStepTestStep) {
            PickleStepTestStep testStep = (PickleStepTestStep)step;
            if (!testStep.getDefinitionArgument().isEmpty()) {
                List<Map<String, Object>> argumentList = new ArrayList<>();

                HashMap argumentMap;
                for(Iterator<Argument> var6 = testStep.getDefinitionArgument().iterator(); var6.hasNext(); argumentList.add(argumentMap)) {
                    Argument argument = (Argument)var6.next();
                    argumentMap = new HashMap<>();
                    if (argument.getValue() != null) {
                        argumentMap.put("val", argument.getValue());
                        argumentMap.put("offset", argument.getStart());
                    }
                }

                matchMap.put("arguments", argumentList);
            }
        }

        if (!result.getStatus().is(Status.UNDEFINED)) {
            matchMap.put("location", step.getCodeLocation());
        }

        return matchMap;
    }

    private Map<String, Object> createResultMap(Result result, TestStepFinished event) throws JsonProcessingException {
        Map<String, Object> resultMap = new HashMap<>();

        // customized her
        String status = result.getStatus().name().toLowerCase(Locale.ROOT);

        if(status.equals(EasyReportStatus.FAILED.getStatus())) {

            String errorContent = ExceptionUtils.printStackTrace(result.getError());

            Map<String, String> errorMap = new HashMap<>();
            try {
                if(errorContent.contains("{") || errorContent.contains("}")) {
                    errorContent = errorContent.substring(errorContent.indexOf("{"), errorContent.lastIndexOf("}") + 1);
                    errorMap = new ObjectMapper().readValue(errorContent, Map.class);
                }
            } catch (JsonProcessingException e) {
                // swallow exception. In case of parse exception, mark it failed
            } catch (Exception e) {
                // for other exceptions, throw it
                throw new EasyReportException(e.getMessage());
            }
            boolean allKnownFailures = !errorMap.keySet().isEmpty() && errorMap.containsKey("knownFailures") && !errorMap.containsKey("failures");
            if(allKnownFailures) {
                resultMap.put("status", EasyReportStatus.KNOWN_FAILURES.getStatus());
            } else {
                resultMap.put("status", EasyReportStatus.FAILED.getStatus());
            }


        } else {
            resultMap.put("status", status);
        }

        // customized her


        if (result.getError() != null) {
            resultMap.put("error_message", ExceptionUtils.printStackTrace(result.getError()));
        }

        if (!result.getDuration().isZero()) {
            resultMap.put("duration", result.getDuration().toNanos());
        }

        return resultMap;
    }

    private void addOutputToHookMap(String text) {
        if (!this.currentStepOrHookMap.containsKey("output")) {
            this.currentStepOrHookMap.put("output", new ArrayList<>());
        }

        ((List)this.currentStepOrHookMap.get("output")).add(text);
    }

    private void addEmbeddingToHookMap(byte[] data, String mediaType, String name) {
        if (!this.currentStepOrHookMap.containsKey("embeddings")) {
            this.currentStepOrHookMap.put("embeddings", new ArrayList<>());
        }

        Map<String, Object> embedMap = this.createEmbeddingMap(data, mediaType, name);
        ((List)this.currentStepOrHookMap.get("embeddings")).add(embedMap);
    }

    private Map<String, Object> createDummyFeatureForFailure(TestRunFinished event) {
        Throwable exception = event.getResult().getError();
        Map<String, Object> feature = new LinkedHashMap<>();
        feature.put("line", 1);
        Map<String, Object> scenario = new LinkedHashMap<>();
        feature.put("elements", Collections.singletonList(scenario));
        scenario.put("start_timestamp", this.getDateTimeFromTimeStamp(event.getInstant()));
        scenario.put("line", 2);
        scenario.put("name", "Failure while executing Cucumber");
        scenario.put("description", "");
        scenario.put("id", "failure;failure-while-executing-cucumber");
        scenario.put("type", "scenario");
        scenario.put("keyword", "Scenario");
        Map<String, Object> when = new LinkedHashMap<>();
        Map<String, Object> then = new LinkedHashMap<>();
        scenario.put("steps", Arrays.asList(when, then));
        Map<String, Object> whenMatch = new LinkedHashMap<>();
        when.put("result", whenMatch);
        whenMatch.put("duration", 0);
        whenMatch.put("status", "passed");
        when.put("line", 3);
        when.put("name", "Cucumber failed while executing");
        whenMatch = new LinkedHashMap<>();
        when.put("match", whenMatch);
        whenMatch.put("arguments", new ArrayList<>());
        whenMatch.put("location", "io.cucumber.core.Failure.failure_while_executing_cucumber()");
        when.put("keyword", "When ");
        Map<String, Object> thenMatch = new LinkedHashMap<>();
        then.put("result", thenMatch);
        thenMatch.put("duration", 0);
        thenMatch.put("error_message", ExceptionUtils.printStackTrace(exception));
        thenMatch.put("status", "failed");
        then.put("line", 4);
        then.put("name", "Cucumber will report this error:");
        thenMatch = new LinkedHashMap<>();
        then.put("match", thenMatch);
        thenMatch.put("arguments", new ArrayList<>());
        thenMatch.put("location", "io.cucumber.core.Failure.cucumber_reports_this_error()");
        then.put("keyword", "Then ");
        feature.put("name", "Test run failed");
        feature.put("description", "There were errors during the execution");
        feature.put("id", "failure");
        feature.put("keyword", "Feature");
        feature.put("uri", "classpath:io/cucumber/core/failure.feature");
        feature.put("tags", new ArrayList<>());
        return feature;
    }

    private String getDateTimeFromTimeStamp(Instant instant) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").withZone(ZoneOffset.UTC);
        return formatter.format(instant);
    }

    private Map<String, Object> createDocStringMap(DocStringArgument docString) {
        Map<String, Object> docStringMap = new HashMap<>();
        docStringMap.put("value", docString.getContent());
        docStringMap.put("line", docString.getLine());
        docStringMap.put("content_type", docString.getMediaType());
        return docStringMap;
    }

    private List<Map<String, List<String>>> createDataTableList(DataTableArgument argument) {
        List<Map<String, List<String>>> rowList = new ArrayList<>();
        Iterator var3 = argument.cells().iterator();

        while(var3.hasNext()) {
            List<String> row = (List)var3.next();
            Map<String, List<String>> rowMap = new HashMap<>();
            rowMap.put("cells", new ArrayList<>(row));
            rowList.add(rowMap);
        }

        return rowList;
    }

    private Map<String, Object> createEmbeddingMap(byte[] data, String mediaType, String name) {
        Map<String, Object> embedMap = new HashMap<>();
        embedMap.put("mime_type", mediaType);
        embedMap.put("data", Base64.getEncoder().encodeToString(data));
        if (name != null) {
            embedMap.put("name", name);
        }

        return embedMap;
    }
}
