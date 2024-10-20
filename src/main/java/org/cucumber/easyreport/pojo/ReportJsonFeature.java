package org.cucumber.easyreport.pojo;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;

@Data
public class ReportJsonFeature {

    private int line;
    private ArrayList<Element> elements = new ArrayList<>();
    private String name;
    private String description;
    private String id;
    private String keyword;
    private String uri;
    private ArrayList<Tag> tags = new ArrayList<>();
    private String status;
    private long totalFeatureDuration;
    private String featureSeq;

    @Data
    public static class Element {
        private Date start_timestamp;
        private ArrayList<Before> before = new ArrayList<>();
        private int line;
        private String name;
        private String description;
        private String id;
        private ArrayList<After> after = new ArrayList<>();
        private String type;
        private String keyword;
        private ArrayList<Step> steps = new ArrayList<>();
        private ArrayList<Tag> tags = new ArrayList<>();

        /*
         * Below are not cucumber json attributes.
         * Added these for EasyReport
         */
        private String scenarioSeq;
        private long totalScenarioDuration;
        private String beforeStatus = StringUtils.EMPTY;
        private String beforeError = StringUtils.EMPTY;
        private String scenarioStatus = StringUtils.EMPTY;
        private String afterStatus = StringUtils.EMPTY;
        private String afterError = StringUtils.EMPTY;
    }

    @Data
    public static class Tag {
        private String name;
        private String type;
        private Location location;
    }

    @Data
    public static class Embedding {
        private String data;
        private String mime_type;
        private String name;
    }

    @Data
    public static class After{
        private LinkedList<Embedding> embeddings = new LinkedList<>();
        private Result result;
        private Match match;
    }

    @Data
    public static class Argument {
        private String val;
        private int offset;
    }

    @Data
    public static class Before {
        private LinkedList<Embedding> embeddings = new LinkedList<>();
        private Result result;
        private Match match;
    }

    @Data
    public static class Location {
        private int line;
        private Column column;
    }

    @Data
    public static class Column {
        private boolean empty;
        private boolean present;
    }
    @Data
    public static class Match {
        private String location;
        private ArrayList<Argument> arguments = new ArrayList<>();
    }

    @Data
    public static class Result {
        private long duration;
        private String status;
        private String error_message;
    }

    @Data
    public static class Step {
        private Result result;
        private ArrayList<Before> before = new ArrayList<>();
        private int line;
        private String name;
        private Match match;
        private ArrayList<After> after = new ArrayList<>();
        private LinkedList<Row> rows = new LinkedList<>(); // changed for data table
        private String keyword;

        /*
         * Below are not cucumber json attributes.
         * Added these for EasyReport
         */
        private String stepSeq;
        private long totalStepDuration;
        private String beforeStatus = StringUtils.EMPTY;
        private String beforeError = StringUtils.EMPTY;
        private String stepStatus = StringUtils.EMPTY;
        private String stepError = StringUtils.EMPTY;
        private String afterStatus = StringUtils.EMPTY;
        private String afterError = StringUtils.EMPTY;
        private String stepFinalStatus = StringUtils.EMPTY;
    }

    @Data
    public static class Row {
        private LinkedList<String> cells = new LinkedList<>();
    }

}
