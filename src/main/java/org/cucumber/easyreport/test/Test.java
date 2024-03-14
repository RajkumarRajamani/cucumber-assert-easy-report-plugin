package org.cucumber.easyreport.test;

import java.io.IOException;
import java.util.List;

public class Test {
    public static void main(String[] args) throws IOException {

//        List<Date> dates = new ArrayList<>();
//        Date date1 = new GregorianCalendar(2020, 4, 1).getTime();
//        Date date2 = new GregorianCalendar(2019, 4, 1).getTime();
//        Date date3 = new GregorianCalendar(2018, 4, 1).getTime();
//        dates.add(date1);
//        dates.add(date2);
//        dates.add(date3);
//        Collections.sort(dates);
//        Date lastDate = dates.get(0);
//        System.out.println(lastDate);
//
//        Duration gap = Duration.ofSeconds(3600);
//        LocalDateTime start = LocalDateTime.now().plus(gap);
//        System.out.println(start);
//
//        String s = "bG9nIHN0YXRlbWVudCB0ZXh0IGF0dGFjaG1lbnQ=";
//        byte[] b = Base64.getDecoder().decode(s);
//        String ss = new String(b, StandardCharsets.UTF_8);
//        System.out.println(ss);
//
//        String path = "test-output/cucumber/easy-cucumber-report.html";
//        String parent = Paths.get(path).getParent().toString();
//        System.out.println(parent);
//
//        BufferedImage bImage = ImageIO.read(new File("src/main/resources/easy-cucumber-report-logo.png"));
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        ImageIO.write(bImage, "png", bos );
//        byte [] data = bos.toByteArray();
//        System.out.println(data);
//
//        File fi = new File("src/main/resources/easy-cucumber-report-logo.png");
//        byte[] fileContent = Files.readAllBytes(fi.toPath());
//        System.out.println(fileContent);

    }

    static List<String> getList(String... labels) {
        return List.of(labels);
    }
}
