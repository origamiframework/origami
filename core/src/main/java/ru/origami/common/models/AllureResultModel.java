package ru.origami.common.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class AllureResultModel {

    private UUID uuid;

    private String historyId;

    private String testCaseId;

    private String testCaseName;

    private String fullName;

    private String name;

    private String status;

    private String stage;

    private String description;

    private Long start;

    private Long stop;

    @JsonIgnore
    public String getDuration() {
        return Objects.nonNull(this.getStart()) && Objects.nonNull(this.getStop())
                ? String.valueOf(this.getStop() - this.getStart())
                : "";
    }
}
