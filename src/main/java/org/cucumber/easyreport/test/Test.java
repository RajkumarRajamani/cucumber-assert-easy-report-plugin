package org.cucumber.easyreport.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class Test {
    public static void main(String[] args) throws IOException {

        List<Date> dates = new ArrayList<>();
        Date date1 = new GregorianCalendar(2020, 4, 1).getTime();
        Date date2 = new GregorianCalendar(2019, 4, 1).getTime();
        Date date3 = new GregorianCalendar(2018, 4, 1).getTime();
        dates.add(date1);
        dates.add(date2);
        dates.add(date3);
        Collections.sort(dates);
        Date lastDate = dates.get(0);
        System.out.println(lastDate);

        Duration gap = Duration.ofSeconds(3600);
        LocalDateTime start = LocalDateTime.now().plus(gap);
        System.out.println(start);


    }

    static List<String> getList(String... labels) {
        return List.of(labels);
    }
}
