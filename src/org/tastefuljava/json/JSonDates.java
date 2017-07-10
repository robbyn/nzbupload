package org.tastefuljava.json;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JSonDates {
    private static final Logger LOG
            = Logger.getLogger(JSonDates.class.getName());

    public static final String ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
    public static final TimeZone GMT = TimeZone.getTimeZone("GMT");

    private JSonDates() {
    }

    public static Date parse(String s, String pattern, TimeZone tz) {
        try {
            return dateFormat(pattern,tz).parse(s);
        } catch (ParseException ex) {
            LOG.log(Level.SEVERE, null, ex);
            throw new IllegalArgumentException(ex.getMessage());
        }
    }

    public static Date parse(String s, String pattern) {
        return parse(s, pattern, GMT);
    }

    public static Date parse(String s, TimeZone tz) {
        return parse(s, ISO8601, tz);
    }

    public static Date parse(String s) {
        return parse(s, ISO8601, GMT);
    }

    public static String format(Date date, String pattern, TimeZone tz) {
        return dateFormat(pattern,tz).format(date);
    }

    public static String format(Date date, String pattern) {
        return format(date, pattern, GMT);
    }

    public static String format(Date date, TimeZone tz) {
        return format(date, ISO8601, tz);
    }

    public static String format(Date date) {
        return format(date, ISO8601, GMT);
    }

    public static DateFormat dateFormat(String pattern, TimeZone tz) {
        DateFormat format = new SimpleDateFormat(pattern);
        format.setTimeZone(tz);
        return format;
    }
}
