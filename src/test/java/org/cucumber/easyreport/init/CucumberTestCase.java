package org.cucumber.easyreport.init;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
        plugin = {
                "pretty",
                "html:test-output/cucumber/cucumber-report.html",
                "json:test-output/cucumber/cucumber-report.json",
                "org.cucumber.easyreport.core.EasyReportJsonFormatter:test-output/cucumber/easy-cucumber-report.html"
        },
        features = "src/test/resources/features",
        glue = {"org.cucumber.easyreport"
        },
        tags = "@test"
)
public class CucumberTestCase extends AbstractTestNGCucumberTests {

        @Override
        @DataProvider(parallel = true)
        public Object[][] scenarios() {
                return super.scenarios();
        }
}