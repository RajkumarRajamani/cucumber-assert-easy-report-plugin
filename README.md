# Easy Cucumber Report

[![Maven Central](https://img.shields.io/maven-central/v/io.github.seleniumbrain/easy-cucumber-report.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.seleniumbrain/easy-cucumber-report)
[![GitHub](https://img.shields.io/badge/source-GitHub-blue?logo=github)](https://github.com/seleniumbrain/cucumber-assert-easy-report-plugin)

## About

EasyCucumberReport is a Java-based Cucumber plugin that generates HTML reports with a modern design. It includes an assertion object to manage known failures.

For unresolved or deferred issues, you can add them to the known failures list. This prevents the report from showing a harsh red failure status, instead displaying results in a more user-friendly amber color.

## Tools & Technologies

| Tools/Technologies                                                                                                                                 |                Name                |       Version       |
|:---------------------------------------------------------------------------------------------------------------------------------------------------|:----------------------------------:|:-------------------:|
| <img src="https://user-images.githubusercontent.com/25181517/117201156-9a724800-adec-11eb-9a9d-3cd0f67da4bc.png" alt="JDK" width="40" />           |              **JDK**               |        `17+`        |
| <img src="https://user-images.githubusercontent.com/25181517/117207242-07d5a700-adf4-11eb-975e-be04e62b984b.png" alt="Maven" width="40" />         |             **Maven**              |       `3.8.9`       |
| <img src="https://user-images.githubusercontent.com/25181517/190229463-87fa862f-ccf0-48da-8023-940d287df610.png" alt="Lombok" width="40" />        |             **Lombok**             |      `Latest`       |
| <img src="https://user-images.githubusercontent.com/25181517/184117353-4b437677-c4bb-4f4c-b448-af4920576732.png" alt="Cucumber" width="40" />      |            **Cucumber**            |      `7.17.0`       |
| <img src="https://user-images.githubusercontent.com/25181517/192108890-200809d1-439c-4e23-90d3-b090cf9a4eea.png" alt="IntelliJ Idea" width="40" /> | **Recommended IDE I   - IntelliJ** | `Community Edition` |
| <img src="https://user-images.githubusercontent.com/25181517/192108891-d86b6220-e232-423a-bf5f-90903e6887c3.png" alt="VS Code" width="40" />       |  **Recommended IDE II - VS Code**  | `Community Edition` |
| <img src="https://user-images.githubusercontent.com/25181517/192108892-6e9b5cdf-4e35-4a70-ad9a-801a93a07c1c.png" alt="Eclipse" width="40" />       | **Recommended IDE III - Eclipse**  | `Community Edition` |

---

## Easy Cucumber Report Usage

Add below maven dependency to your existing pom.xml file

    <dependency>
        <groupId>io.github.seleniumbrain</groupId>
        <artifactId>easy-cucumber-report</artifactId>
        <version>1.0.0</version>
    </dependency>

Include this plugin to your cucumber runner file
`org.cucumber.easyreport.core.EasyReportJsonFormatter:<location of an HTML report to be stored>`

```java

@CucumberOptions(
        plugin = {
                "pretty",
                "html:test-output/cucumber/cucumber-report.html",
                "json:test-output/cucumber/cucumber-report.json",
                "org.cucumber.easyreport.core.EasyReportJsonFormatter:test-output/easy-cucumber-report/easy-cucumber-report.html"
        },
        features = {
                "src/test/resources/features"
        },
        glue = {
                "org.cucumber.easyreport"
        },
        tags = "@tag"
)
public class CucumberTestCase extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
```

> [!IMPORTANT]
> Ensure that you have added `cucumber.properties` file under `src/` folder with the below content

```properties
# Below are the configurations for Easy Cucumber Report

# this is the location where the regular cucumber JSON report will be stored
easyReport.format.json.conventional=test-output/easy-cucumber-report/easy-cucumber-report.json
# this is the location where the customized cucumber JSON report will be stored
easyReport.format.json.customized=test-output/easy-cucumber-report/easy-cucumber-html-data-report.json
# this is the location where the regular cucumber HTML report will be stored
easyReport.format.html.customized=test-output/easy-cucumber-report/easy-cucumber-report.html

# Maintain below input while carrying out the execution. These will not impact any execution but will be displayed in the report
easyReport.project.info.environment=Development Region
easyReport.project.info.browser=Microsoft Edge
easyReport.project.info.appName=Google
easyReport.project.info.descriptionOrReleaseNotes=Any note that you would like to see on an HTML report, pls type here

easyReport.project.info.project-manager=John, Peter
easyReport.project.info.dq-manager=Dwayne, Johnson
easyReport.project.info.dq-lead=Jonny, Dep

easyReport.project.info.release.name=March 2025 Release
easyReport.project.info.release.date=21-March-2025
easyReport.project.info.release.sprint=Sprint 22
```
---

## Assertions usage

Create an instance for `Assertions` class and use its methods to add assertions and known failures to the report.

```java
import io.cucumber.java.en.Given;
import org.cucumber.easyreport.core.Assertions;

public class StepDefinitions {

    @Given("I have a step")
    public void iHaveAStep() {
        Assertions assertions = new Assertions();
        try {

            // your definitions here...

            // to add known failures
            assertions.addKnownFailure(
                    "Device Name Field Validation", // it is the name of the assertion and can be any text. This is just for our reference to identify the assertion
                    "JIRA-1234" // tracking id of the known failure [basically any text]
            );

            // to add assertions
            assertions.assertEqualsTo(
                    "Device Name Field Validation", // it is the name of the assertion and can be any text. This is just for our reference to identify the assertion
                    "Macbook Pro", // actual value to compare
                    "Macbook Air", // expected value to compare
                    "Device Name is not matching. Please check...", // failure message to be displayed in the report
                    "Device Name is matching." // pass message to be displayed in the report
            );
            
            // Note: if you programmatically add known failures, then you should add it before adding assertions with the respective label name
            // In the above example, the known-failure for the label "Device Name Field Validation" is added before adding the assertion for the same label
            
        } finally {
            assertions.assertAll();
        }
    }

}
```

> [!NOTE]
> You can also maintain list of known failures in a YAML file and keep it anywhere under `src/` location. Name the file as `known-failures.yml` and add list of known failures in below format

`known-failures.yml`
```yaml
knownFailures:
- label: UserNameField Validation
  trackingId: XLCTCD-1234
  description: any description

- label: LoginButton Validation
  trackingId: XLCTCD-5678
  description: any description

- label: SubmissionCount Validation
  trackingId: XLCTCD-9810
  description: any description
```

### [Click here to see a sample report](https://raw.githubusercontent.com/seleniumbrain/cucumber-assert-easy-report-plugin/master/sample-output/easy-cucumber-report.html)
### [Click here to download and view a sample report](../cucumber-assert-easy-report/sample-output/easy-cucumber-report.html)

---
## License

This project is licensed under the MIT License—see the LICENSE file for details.

## Contact

For any questions or suggestions, please contact the project maintainers `rajoviyaa.s@gmail.com`

---



