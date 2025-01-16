package org.cucumber.easyreport.core;

import org.cucumber.easyreport.exception.EasyReportException;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Stream;

public class EasyReportConfigReader {

    private static Properties properties;
    private static final String defaultReportDirectory = String.join(File.separator, System.getProperty("user.dir"), "output", "easy-report", "");

    public EasyReportConfigReader() {
        this.loadProperties();
    }

    /**
     * Loads the properties from the `cucumber.properties` file located in the project directory (anywhere inside src/).
     * If the properties are already loaded, it does nothing.
     *
     * @throws EasyReportException if the `cucumber.properties` file is not found or cannot be loaded.
     */
    @SneakyThrows
    private void loadProperties() {
        if (Objects.isNull(properties)) {
            String fileNameToFind = "cucumber.properties";
            String rootDirectoryPath = System.getProperty("user.dir") + File.separator + "src" + File.separator;
            File rootDirectory = new File(rootDirectoryPath);
            File cucumberPropertyFile;

            try (Stream<Path> walkStream = Files.walk(rootDirectory.toPath())) {
                cucumberPropertyFile = walkStream.filter(p -> p.toFile().isFile())
                        .filter(f -> f.toString().endsWith(fileNameToFind))
                        .findFirst()
                        .orElseThrow(() -> new EasyReportException("'cucumber.properties' file is not found in project folder"))
                        .toFile();
            } catch (IOException e) {
                throw new EasyReportException("Error while searching for 'cucumber.properties' file", e);
            }

            properties = new Properties();
            try (FileInputStream fis = new FileInputStream(cucumberPropertyFile)) {
                properties.load(fis);
            } catch (IOException e) {
                throw new EasyReportException("Error while loading 'cucumber.properties' file", e);
            }
        }
    }

    /**
     * Retrieves the property value associated with the given key.
     *
     * @param key the property key to look up
     * @return the property value associated with the key, or null if the key is not found or an error occurs
     */
    public String getProperty(String key) {
        try {
            return properties.getProperty(key);
        } catch (Exception e) {
            return null;
        }
    }

    public String getJsonReportPath() {
        return properties.getProperty("easyReport.format.json.conventional", defaultReportDirectory + "easy-cucumber-report.json");
    }

    public String getCustomizedJsonReportPath() {
        return properties.getProperty("easyReport.format.json.customized", defaultReportDirectory + "easy-cucumber-html-data-set-report.json");
    }

    public String getHtmlReportPath() {
        return properties.getProperty("easyReport.format.html.customized", defaultReportDirectory + "easy-cucumber-html-report.html");
    }

    public String getEnvironment() {
        return properties.getProperty("easyReport.project.info.environment", "Default Test Environment");
    }

    public String getBrowser() {
        return properties.getProperty("easyReport.project.info.browser", "Default Browser");
    }

    public String getApplicationName() {
        return properties.getProperty("easyReport.project.info.appName", "Default Application Name");
    }

    public String getProjectDescription() {
        return properties.getProperty("easyReport.project.info.descriptionOrReleaseNotes", "Default Description");
    }

    public String getOs() {
        return System.getProperty("os.name");
    }

    public String getProjectManger() {
        return properties.getProperty("easyReport.project.info.project-manager", "un-defined");
    }

    public String getDeliveryQualityManager() {
        return properties.getProperty("easyReport.project.info.dq-manager", "un-defined");
    }

    public String getDeliveryQualityLead() {
        return properties.getProperty("easyReport.project.info.dq-lead", "un-defined");
    }

    public String getProdReleaseName() {
        return properties.getProperty("easyReport.project.info.release.name", "un-defined");
    }

    public String getProdReleaseDate() {
        return properties.getProperty("easyReport.project.info.release.date", "un-defined");
    }

    public String getSprintName() {
        return properties.getProperty("easyReport.project.info.release.sprint", "un-defined");
    }

    public static void main(String[] args) {
        EasyReportConfigReader config = new EasyReportConfigReader();
        System.out.println(config.getProperty("easyReport.format.html.customized"));
        System.out.println(config.getProperty("easyReport.format.html.customized"));
        System.out.println(config.getHtmlReportPath());
        System.out.println(System.getProperty("user.dir"));
    }

}
