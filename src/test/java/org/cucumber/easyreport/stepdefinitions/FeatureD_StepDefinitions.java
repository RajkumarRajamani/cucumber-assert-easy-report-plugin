package org.cucumber.easyreport.stepdefinitions;

import org.cucumber.easyreport.assertions.Assertions;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class FeatureD_StepDefinitions {

    @Given("start10")
    public void start() throws JsonProcessingException {
        System.out.println("Start method");
//        throw new IllegalArgumentException("Forced Exception");
    }

    @When("process10")
    public void process() {
        System.out.println("Process method");
    }

    @Then("check10")
    public void check() {
        System.out.println("Check method");
    }

    //====

    @Given("start11")
    public void start5() throws JsonProcessingException {
        System.out.println("Start method");
        Assertions assertions = new Assertions();
        assertions.addKnownFailureLabels("name", "age1");
        assertions.assertEqualsTo("name", "raj", "rajkumar", "failed", "passed");
        assertions.assertEqualsTo("age", "30", "31", "failed", "passed");
        assertions.assertAll();
    }

    @When("process11")
    public void process5() {
        System.out.println("Process method");
    }

    @Then("check11")
    public void check5() {
        System.out.println("Check method");
    }

    //=====

    @Given("start12")
    public void start6() throws JsonProcessingException {
        System.out.println("Start method");
    }

    @When("process12")
    public void process6() {
        System.out.println("Process method");
    }

    @Then("check12")
    public void check6() {
        System.out.println("Check method");
    }
}
