package ru.origami.testit_allure.test_it.testit.clients;

import ru.origami.testit_allure.test_it.client.invoker.ApiException;
import ru.origami.testit_allure.test_it.client.model.*;

import java.util.List;

public interface ApiClient {
    TestRunV2GetModel createTestRun() throws ApiException;
    TestRunV2GetModel getTestRun(String uuid) throws ApiException;
    void completeTestRun(String uuid) throws ApiException;
    void updateAutoTest(AutoTestPutModel model) throws ApiException;
    String createAutoTest(AutoTestPostModel model) throws ApiException;
    AutoTestModel getAutoTestByExternalId(String externalId) throws ApiException;
    void linkAutoTestToWorkItem(String id, String workItemId) throws ApiException;
    void sendTestResults(String testRunUuid, List<AutoTestResultsForTestRunModel> models) throws ApiException;
    String addAttachment(String path) throws ApiException;
    List<String> getTestFromTestRun(String testRunUuid, String configurationId) throws ApiException;
}
