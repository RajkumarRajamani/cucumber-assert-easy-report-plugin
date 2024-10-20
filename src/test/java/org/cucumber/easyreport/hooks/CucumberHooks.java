package org.cucumber.easyreport.hooks;

import io.cucumber.core.backend.TestCaseState;
import io.cucumber.java.*;
import io.cucumber.plugin.event.PickleStepTestStep;
import io.cucumber.plugin.event.TestCase;
import lombok.SneakyThrows;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.lang.reflect.Field;
import java.util.List;

public class CucumberHooks {

    private static WebDriver driver;

    private int counter = 0;

    @BeforeAll
    public static void beforeAllCucumberHook() {
        driver = new ChromeDriver();
        Capabilities cap = ((RemoteWebDriver) driver).getCapabilities();
        System.out.println(cap.getBrowserVersion());

//        driver.manage().getCookies().forEach(ck -> ck.);
    }

    @Before
    public void easyReportInitScenario(Scenario scenario) {
        driver.get("https://www.google.com");
    }

    @Before
    public void secondBeforeScenarioMethod() {
        System.out.println("second before scenario method");
//        throw new EasyReportException("ForceFailCheck");
//        Assertions assertions = new Assertions();
//        assertions.addKnownFailureLabels("name", "XLCTCD-1001")
//                .addKnownFailureLabels("age", "XLCTCD-1002");
//        assertions.assertEqualsTo("name", "raj", "rajkumar", "failed", "passed");
//        assertions.assertEqualsTo("age", "30", "31", "failed", "passed");
//        assertions.assertEqualsTo("dob", "30-2-2023", "20-2-2023", "failed", "passed");
//        assertions.assertAll();
    }
//
    @BeforeStep
    public void easyReportInitStep(Scenario scenario) {
//        throw new EasyReportException("ForceFailCheck");
    }

    @AfterStep
    public void easyReportEndStep(Scenario scenario) {

//        final byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
//        scenario.attach(screenshot, "image/png", "screenshot for " + getStepName(scenario));
//        StringBuilder sb = new StringBuilder();
//        sb.append("Total Number of cases = 5").append("\n")
//                .append("C:\\abc\\bdhb\\dkkj.xml").append("\n")
//                .append("Total Number of cases = 5").append("\n")
//                .append("Total Number of cases = 5").append("\n");
//        scenario.attach(sb.toString(), "text/plain", "screenshot for " + getStepName(scenario));

//        if(getStepName(scenario).contains("check3") || getStepName(scenario).contains("process12"))
//            throw new EasyReportException("ForceFailCheck");

        counter++;
//        throw new EasyReportException("ForceFailCheck");
    }

    @After
    public void easyReportEndScenario(Scenario scenario) {
//        throw new EasyReportException();
    }
//
    @AfterAll
    public static void afterAllCucumberHook() {
        driver.quit();
    }
//
    @SneakyThrows
    private String getStepName(Scenario scenario) {
        Field f = scenario.getClass().getDeclaredField("delegate");
        f.setAccessible(true);
        TestCaseState tcs = (TestCaseState) f.get(scenario);

        Field f2 = tcs.getClass().getDeclaredField("testCase");
        f2.setAccessible(true);
        TestCase r = (TestCase) f2.get(tcs);

        List<PickleStepTestStep> stepDefs = r.getTestSteps().stream()
                .filter(x -> x instanceof PickleStepTestStep)
                .map(x -> (PickleStepTestStep) x)
                .toList();
        PickleStepTestStep currentStepDef = stepDefs.get(counter);
        return currentStepDef.getStep().getText();

    }
}
