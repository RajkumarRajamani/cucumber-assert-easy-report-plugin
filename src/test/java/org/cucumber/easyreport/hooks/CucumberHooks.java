package org.cucumber.easyreport.hooks;

import io.cucumber.java.*;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class CucumberHooks {

    private static WebDriver driver;

    @BeforeAll
    public static void beforeAllCucumberHook() {
        driver = WebDriverManager.chromedriver().create();
    }

    @Before
    public void easyReportInitScenario(Scenario scenario) {
        driver.get("https://www.google.com");
    }

    @BeforeStep
    public void easyReportInitStep(Scenario scenario) {
    }

    @AfterStep
    public void easyReportEndStep(Scenario scenario) {
        final byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
//        final byte[] screenshot = "Screenshot Image".getBytes();
        scenario.attach(screenshot, "image/png", "screenshot");
//        scenario.attach(screenshot, "image/png", "screenshot");
//        scenario.attach("test screenshot".getBytes(), "image/png", "screenshot");

        StringBuilder sb = new StringBuilder();
        sb.append("Total Number of cases = 5").append("\n")
                .append("Total Number of cases = 5").append("\n")
                .append("Total Number of cases = 5").append("\n")
                .append("Total Number of cases = 5").append("\n");
        scenario.attach(sb.toString(), "text/plain", "screenshot");
    }

    @After
    public void easyReportEndScenario(Scenario scenario) {
    }

    @AfterAll
    public static void afterAllCucumberHook() {
        driver.quit();
    }
}
