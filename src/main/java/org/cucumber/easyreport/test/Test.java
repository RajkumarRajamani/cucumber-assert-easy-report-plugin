package org.cucumber.easyreport.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Test {
    public static void main(String[] args) throws IOException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        String jsonArray = objectMapper.writeValueAsString(List.of("Rajkumar", "Rajamani"));
//        System.out.println(jsonArray);

        System.out.println(Paths.get("test-output/cucumber1/easy-cucumber-report.html").getParent());
        Files.createDirectories(Paths.get("test-output/cucumber1/easy-cucumber-report.html").getParent());
    }
}
