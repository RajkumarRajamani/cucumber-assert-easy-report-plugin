package org.cucumber.easyreport.util.dateutils;

import lombok.SneakyThrows;
import org.apache.commons.validator.GenericValidator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

import static org.cucumber.easyreport.util.dateutils.DateFormats.*;

public class DateUtils {

    public static boolean isDateValue(String dateString) {
        if (Objects.nonNull(dateString)) {
            return GenericValidator.isDate(dateString, YYYYMMDD_HYPHEN.get(), false) ||
                    GenericValidator.isDate(dateString, YYYYMMDD_SLASH.get(), false) ||
                    GenericValidator.isDate(dateString, YYYYMMMDD_HYPHEN.get(), false) ||
                    GenericValidator.isDate(dateString, YYYYDDMM_HYPHEN.get(), false) ||
                    GenericValidator.isDate(dateString, YYYYDDMM_SLASH.get(), false) ||
                    GenericValidator.isDate(dateString, MMDDYYYY_HYPHEN.get(), false) ||
                    GenericValidator.isDate(dateString, MMDDYYYY_SLASH.get(), false) ||
                    GenericValidator.isDate(dateString, DDMMYYYY_HYPHEN.get(), false) ||
                    GenericValidator.isDate(dateString, DDMMYYYY_SLASH.get(), false) ||
                    GenericValidator.isDate(dateString, DDMMYYYY.get(), false) ||
                    GenericValidator.isDate(dateString, DDMMMYYYY_HYPHEN.get(), false);
        }
        return false;
    }


    @SneakyThrows
    public static String getDateStringAtFormat(String dateString, DateFormats fromFormat, DateFormats toFormat) {
        String defaultFormat = YYYYMMDD_HYPHEN.get();

        if(isDateValue(dateString)) {
            DateFormat df = new SimpleDateFormat(fromFormat.get());
            Date date = df.parse(dateString);
            if(Objects.nonNull(toFormat))
                return new SimpleDateFormat(toFormat.get()).format(date);
            else
                return new SimpleDateFormat(defaultFormat).format(date);
        }

        return dateString;
    }
}
