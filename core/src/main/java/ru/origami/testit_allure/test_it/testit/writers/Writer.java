package ru.origami.testit_allure.test_it.testit.writers;

import ru.origami.testit_allure.test_it.testit.models.ClassContainer;
import ru.origami.testit_allure.test_it.testit.models.MainContainer;
import ru.origami.testit_allure.test_it.testit.models.TestResult;

public interface Writer {
    void writeTest(TestResult testResult);

    void writeClass(ClassContainer container);

    void writeTests(MainContainer container);

    String writeAttachment(String path);
}
