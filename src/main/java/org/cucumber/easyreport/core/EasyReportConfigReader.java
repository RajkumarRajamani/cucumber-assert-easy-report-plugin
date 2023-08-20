package org.cucumber.easyreport.core;

import org.cucumber.easyreport.exception.EasyReportException;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Stream;

public class EasyReportConfigReader {

    private static Properties properties;
    private static final String defaultReportDirectory = System.getProperty("user.dir") + System.getProperty("file.separator") + "easy-report" + System.getProperty("file.separator");

    public EasyReportConfigReader() {
        this.loadProperties();
    }

    @SneakyThrows
    private void loadProperties() {
        if(Objects.isNull(properties)) {
            String fileNameToFind = "cucumber.properties";
            String rootDirectoryPath = System.getProperty("user.dir") + System.getProperty("file.separator") + "src" + System.getProperty("file.separator");
            File rootDirectory = new File(rootDirectoryPath);
            File cucumberPropertyFile = null;

            try(Stream<Path> walkStream = Files.walk(rootDirectory.toPath())) {
                cucumberPropertyFile = walkStream.filter(p -> p.toFile().isFile())
                        .filter(f -> f.toString().endsWith(fileNameToFind))
                        .findFirst().orElseThrow(() -> new EasyReportException("'cucumber.properties' file is not found in project folder")).toFile();
            }

            properties = new Properties();
            properties.load(new FileInputStream(cucumberPropertyFile));
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getJsonReportPath() {
        String path = properties.getProperty("easyReport.format.json.conventional");
        return path == null ? defaultReportDirectory + "easy-cucumber-report.json" : path;
    }

    public String getCustomizedJsonReportPath() {
        String path = properties.getProperty("easyReport.format.json.customized");
        return path == null ? defaultReportDirectory + "easy-cucumber-html-data-set-report.json" : path;
    }

    public String getHtmlReportPath() {
        String path = properties.getProperty("easyReport.format.html.customized");
        return path == null ? defaultReportDirectory + "easy-cucumber-html-report.html" : path;
    }

    public String getEnvironment() {
        String environment = properties.getProperty("easyReport.project.info.environment");
        return environment == null ? "Default Test Environment" : environment;
    }

    public String getBrowser() {
        String browser = properties.getProperty("easyReport.project.info.browser");
        return browser == null ? "Default Browser" : browser;
    }

    public String getApplicationName() {
        String appName = properties.getProperty("easyReport.project.info.appName");
        return appName == null ? "Default Application Name" : appName;
    }

    public String getApplicationUrl() {
        String appUrl = properties.getProperty("easyReport.project.info.url");
        return appUrl == null ? "Default Application URI" : appUrl;
    }

    public String getProjectDescription() {
        String projectDescription = properties.getProperty("easyReport.project.info.description");
        return projectDescription == null ? "Default Description" : projectDescription;
    }

    public String getOs() {
        return System.getProperty("os.name");
    }

    public static void main(String[] args) {
        EasyReportConfigReader config = new EasyReportConfigReader();
        System.out.println(config.getProperty("easyReport.format.html.customized"));
        System.out.println(config.getProperty("easyReport.format.html.customizeds"));
    }

}
