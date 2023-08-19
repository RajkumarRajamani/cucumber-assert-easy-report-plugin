package org.cucumber.easyreport.pojo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Data
public class ReportJsonFeature {

//    private List<Feature> features;
//
//    @Data
//    public static class Feature {
//        private int line;
//        private ArrayList<Element> elements;
//        private String name;
//        private String description;
//        private String id;
//        private String keyword;
//        private String uri;
//        private ArrayList<Tag> tags;
//    }

    private int line;
    private ArrayList<Element> elements;
    private String name;
    private String description;
    private String id;
    private String keyword;
    private String uri;
    private ArrayList<Tag> tags;
    private String status;
    private long totalFeatureDuration;

    @Data
    public static class Element {
        private Date start_timestamp;
        private ArrayList<Before> before;
        private int line;
        private String name;
        private String description;
        private String id;
        private ArrayList<After> after;
        private String type;
        private String keyword;
        private ArrayList<Step> steps;
        private ArrayList<Tag> tags;
        private String status;
        private long totalScenarioDuration;
    }

    @Data
    public static class Tag {
        private String name;
        private String type;
        private Location location;
    }

//    @Data
//    public static class ScenarioTag {
//        private String name;
//    }

    @Data
    public static class Embedding {
        private String data;
        private String mime_type;
        private String name;
    }

    @Data
    public static class After{
        private LinkedList<Embedding> embeddings;
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
        private LinkedList<Embedding> embeddings;
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
        private ArrayList<Argument> arguments;
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
        private ArrayList<Before> before;
        private int line;
        private String name;
        private Match match;
        private ArrayList<After> after;
        private String keyword;
        private String status;
        private long totalStepDuration;
    }

}
