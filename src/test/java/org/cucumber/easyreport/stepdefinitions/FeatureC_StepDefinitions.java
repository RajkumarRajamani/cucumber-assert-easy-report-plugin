package org.cucumber.easyreport.stepdefinitions;

import org.cucumber.easyreport.assertions.Assertions;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class FeatureC_StepDefinitions {

    @Given("start7")
    public void start() throws JsonProcessingException {
        System.out.println("Start method");
    }

    @When("process7")
    public void process() {
        System.out.println("Process method");
    }

    @Then("check7")
    public void check() {
        System.out.println("Check method");
        Assertions assertions = new Assertions();
        assertions.addKnownFailureLabels("name");
        assertions.assertEqualsTo("name", "raj", "rajkumar", "failed", "passed");
        assertions.assertEqualsTo("dob", "30", "31", "failed", "passed");
        assertions.assertAll();
    }

    //====

    @Given("start8")
    public void start5() throws JsonProcessingException {
        System.out.println("Start method");
    }

    @When("process8")
    public void process5() {
        System.out.println("Process method");
    }

    @Then("check8")
    public void check5() {
        System.out.println("Check method");
    }

    //=====

    @Given("start9")
    public void start6() throws JsonProcessingException {
        System.out.println("Start method");
    }

    @When("process9")
    public void process6() {
        System.out.println("Process method");
    }

    @Then("check9")
    public void check6() {
        System.out.println("Check method");
    }
}
