package ru.origami.common.models;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public interface IDateFormat {

    String getPattern();

    default DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormatter.ofPattern(getPattern());
    }

    default String format(Date date) {
        return new SimpleDateFormat(getPattern()).format(date);
    }

    default String format(LocalDate date) {
        return date.format(getDateTimeFormatter());
    }

    default String format(LocalTime time) {
        return time.format(getDateTimeFormatter());
    }

    default String format(LocalDateTime dateTime) {
        return dateTime.format(getDateTimeFormatter());
    }

    default String format(ZonedDateTime dateTime) {
        return dateTime.format(getDateTimeFormatter());
    }
}
