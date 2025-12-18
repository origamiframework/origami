package ru.origami.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ExecMavenCheck {

    public static void main(String[] args) {
        List<String> executionIds = List.of("allure-excel");
        System.err.printf("\u001B[31mНе передан или передан некорректный execution-id! Используйте 'mvn exec:java@your-id'.\nСписок допустимых id: %s\u001B[0m",
                String.join(", ", executionIds));
        System.exit(1);
    }
}
