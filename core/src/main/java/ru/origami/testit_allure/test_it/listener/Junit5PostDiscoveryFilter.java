package ru.origami.testit_allure.test_it.listener;

import org.junit.platform.engine.FilterResult;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.PostDiscoveryFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.origami.testit_allure.test_it.client.invoker.ApiException;
import ru.origami.testit_allure.test_it.testit.services.Adapter;
import ru.origami.testit_allure.test_it.testit.services.AdapterManager;
import ru.origami.testit_allure.test_it.testit.services.Utils;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Junit5PostDiscoveryFilter implements PostDiscoveryFilter {
    private List<String> testsForRun;

    private boolean isFilteredMode = false;

    private final AdapterManager adapterManager;

    private static final Logger LOGGER = LoggerFactory.getLogger(Junit5PostDiscoveryFilter.class);

    public Junit5PostDiscoveryFilter() throws ApiException, NoSuchAlgorithmException, KeyManagementException {
        adapterManager = Adapter.getAdapterManager();

        if (adapterManager != null) {
            isFilteredMode = adapterManager.isFilteredMode();

            if (isFilteredMode) {
                testsForRun = adapterManager.getTestFromTestRun();
            }
        }
    }

    @Override
    public FilterResult apply(TestDescriptor object) {
        if (!isFilteredMode) {
            return FilterResult.included("Adapter mode isn't filtered");
        }

        if (!object.getChildren().isEmpty()) {
            return FilterResult.included("filter only applied for tests");
        }

        final MethodSource source = (MethodSource) object.getSource().get();
        String externalId = Utils.extractExternalID(source.getJavaMethod(), null);

        if (externalId.matches("\\{.*}")) {
            return filterTestWithParameters(externalId);
        }

        return filterSimpleTest(externalId);
    }

    private FilterResult filterSimpleTest(String externalId) {
        if (adapterManager == null) {
            return FilterResult.includedIf(true);
        }

        if (testsForRun.contains(externalId)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Test {} include for run", externalId);
            }

            return FilterResult.includedIf(true);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Test {} exclude for run", externalId);
        }

        return FilterResult.excluded("test excluded");
    }

    private FilterResult filterTestWithParameters(String externalId) {
        if (adapterManager == null) {
            return FilterResult.includedIf(true);
        }

        Pattern pattern = Pattern.compile(externalId.replaceAll("\\{.*}", ".*"));

        for (String test : testsForRun) {
            Matcher matcher = pattern.matcher(test);
            if (matcher.find()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Test {} include for run", externalId);
                }
                return FilterResult.includedIf(true);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Test {} exclude for run", externalId);
        }

        return FilterResult.excluded("test excluded");
    }
}
