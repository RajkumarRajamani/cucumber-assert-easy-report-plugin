package org.cucumber.easyreport.stepdefinitions;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

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
