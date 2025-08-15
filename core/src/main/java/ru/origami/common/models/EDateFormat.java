package ru.origami.common.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Date;

@Getter
@ToString
@AllArgsConstructor
public enum EDateFormat implements IDateFormat {

    // TODO PATTERN_17 пока нет. Был дублем PATTERN_2. Пока что 17 просто удален без перенумерации последующих,
    // чтобы не было путаницы и не приходилось сверять паттерны

    PATTERN_1("yyyy-MM-dd HH:mm:ss"),
    PATTERN_2("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),
    PATTERN_3("yyyy-MM-dd HH:mm:ss:SSS Z"),
    PATTERN_4("yyyy-MM-dd HH:mm:ss:SSS"),
    PATTERN_5("yyyy-MM-dd"),
    PATTERN_6("MM.yyyy"),
    PATTERN_7("dd/MM/yyyy"),
    PATTERN_8("yyyy-MM-dd HH:mm:ss:SSSZ"),
    PATTERN_9("dd.MM.yyyy"),
    PATTERN_10("ddMMyyyy"),
    PATTERN_11("yyyy-MM-dd'T'HH:mm:ssXXX"),
    PATTERN_12("dd.MM.yyyy HH:mm:ss"),
    PATTERN_13("MMyy"),
    PATTERN_14("dd-MM-yyyy"),
    PATTERN_15("dd.MM.yyyy HH:mm"),
    PATTERN_16("yyyy-MM-dd'T'HH:mm:ssZ"),
    PATTERN_18("yyyyMMdd-HH:mm:ss.SSS"),
    PATTERN_19("ddMMyyyy_HH.mm"),
    PATTERN_20("yyyy-MM-dd'Z'"),
    PATTERN_21("yyyyMMdd"),
    PATTERN_22("yyyy-MM-dd'T'HH:mm:ss.SSS"),
    PATTERN_23("yyyy-MM-dd'T'HH:mm:ss.SSSZ"),
    PATTERN_24("yyyy-MM-dd'T'HH:mm:ss[.SSS][XXX][X]"),
    PATTERN_25("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"),
    PATTERN_26("yyyy-MM-dd'T'HH:mm:ss.SSSxxx"),
    PATTERN_27("HH:mm dd/MM/yyyy");

    private final String pattern;
}

