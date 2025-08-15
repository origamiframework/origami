package ru.origami.hibernate.models;

import jakarta.persistence.TemporalType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.query.BindableType;

import java.time.Instant;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

@Getter
@ToString
@Builder(builderMethodName = "Builder", setterPrefix = "set")
public class QueryParameter {

    private String name;

    private Object value;

    private BindableType bindableType;

    private Class classType;

    private Instant instantArgument;

    private TemporalType temporalType;

    private Calendar calendarArgument;

    private Date dateArgument;

    private Collection<?> collectionValues;

    private Object[] massiveValues;
}
