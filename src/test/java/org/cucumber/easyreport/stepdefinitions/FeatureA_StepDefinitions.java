package org.cucumber.easyreport.stepdefinitions;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.cucumber.easyreport.assertions.Assertions;
import org.utils.datetime.date.DateTimeFormat;
import org.utils.datetime.date.DateTimeUtils;
import org.utils.datetime.date.TimeZoneId;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Locale;

public class FeatureA_StepDefinitions {

    @Given("start1 {string} and {string}")
    public void start(String testData, String data) throws JsonProcessingException {
        System.out.println("Start method");
    }

    @When("process1")
    public void process() {
        System.out.println("Process method");
    }

    @Then("check1")
    public void check() {
        System.out.println("Check method");
    }

    //====

    @Given("start2")
    public void start2() throws JsonProcessingException {
        System.out.println("Start method");
    }

    @When("process2")
    public void process2() {
        System.out.println("Process method");
    }

    @Then("check2")
    public void check2() {
        System.out.println("Check method");
    }

    //=====

    @Given("start3")
    public void start3() throws JsonProcessingException {


        System.out.println("Start method");
        Assertions assertions = new Assertions();
        try {
//            assertions.addAssertions("City", assetion -> assetion.assertThat("Chennai").as("City is not matching.").isEqualTo("Madras"));
//            assertions.addAssertions("Address", assetion -> assetion.assertThat("Chennai").as("City is not matching.").isEqualTo("Madras"));

            assertions.assertEqualsTo("BirthDate",
                    "2000-08-04T13:00:00",
                    "04-08-2000",
                    "Date is not matching.",
                    "Date is matching."
            );
        } finally {
            assertions.assertAll();
        }
    }

    @When("process3")
    public void process3() {
        System.out.println("Process method");
    }

    @Then("check3")
    public void check3() {
        System.out.println("Check method");
    }
}
