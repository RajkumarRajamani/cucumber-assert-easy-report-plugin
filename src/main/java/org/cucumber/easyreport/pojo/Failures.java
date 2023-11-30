package org.cucumber.easyreport.pojo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Failures {
    private List<KnownFailures> knownFailures = new ArrayList<>();
}
