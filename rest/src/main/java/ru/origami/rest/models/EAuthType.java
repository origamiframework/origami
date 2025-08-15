package ru.origami.rest.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EAuthType {

    BEARER("Bearer");

    private String type;

    @Override
    public String toString() {
        return String.format("type: %s", type);
    }
}
