package org.cucumber.easyreport.stepdefinitions;

import org.cucumber.easyreport.assertions.Assertions;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class FeatureB_StepDefinitions {

    @Given("start4")
    public void start() throws JsonProcessingException {
        System.out.println("Start method");
        Assertions assertions = new Assertions();
        assertions.addKnownFailureLabels("name", "age");
        assertions.assertEqualsTo("name", "raj", "rajkumar", "failed", "passed");
        assertions.assertEqualsTo("age", "30", "31", "failed", "passed");
        assertions.assertAll();
    }

    @When("process4")
    public void process() {
        System.out.println("Process method");
    }

    @Then("check4")
    public void check() {
        System.out.println("Check method");
    }

    //====

    @Given("start5")
    public void start5() {
        System.out.println("Start method");
        Assertions assertions = new Assertions();
        assertions.addKnownFailureLabels("name", "age");
        assertions.assertEqualsTo("name", "raj", "raj", "starts5 is failed - logging failed", "starts5 is passed - logging passed");
        assertions.assertEqualsTo("age", "30", "31", "failed", "passed");
        assertions.assertAll();
    }

    @When("process5")
    public void process5() {
        System.out.println("Process method");
    }

    @Then("check5")
    public void check5() {
        System.out.println("Check method");
    }

    //=====

    @Given("start6")
    public void start6() {
        System.out.println("Start method");
        Assertions assertions = new Assertions();
        assertions.addKnownFailureLabels("name", "age");
        assertions.assertEqualsTo("name", "raj", "rajkumar", "failed", "passed");
        assertions.assertEqualsTo("age", "30", "31", "failed", "passed");
        assertions.assertAll();
    }

    @When("process6")
    public void process6() {
        System.out.println("Process method");
    }

    @Then("check6")
    public void check6() {
        System.out.println("Check method");
    }
}
