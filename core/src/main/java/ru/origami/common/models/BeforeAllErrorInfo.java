package ru.origami.common.models;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder(builderMethodName = "Builder", setterPrefix = "set")
@ToString
public class BeforeAllErrorInfo {

    private Throwable throwable;

    private String testClassName;

    private String methodName;
}
