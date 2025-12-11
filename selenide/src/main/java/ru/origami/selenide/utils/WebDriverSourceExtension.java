package ru.origami.selenide.utils;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import ru.origami.common.environment.Environment;
import ru.origami.selenide.attachment.SelenideAttachment;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.codeborne.selenide.FileDownloadMode.FOLDER;
import static org.junit.jupiter.api.Assertions.fail;
import static ru.origami.common.OrigamiHelper.waitInMillis;
import static ru.origami.common.environment.Environment.getSysEnvPropertyOrDefault;
import static ru.origami.common.environment.Language.getLangValue;

public class WebDriverSourceExtension implements BeforeEachCallback, AfterEachCallback, AfterTestExecutionCallback {

    private static final AtomicBoolean IS_FIRST_EXECUTE = new AtomicBoolean(true);

    private static final String BROWSER_NAME_PROP = "web.browser.name";
    private static final String CI_BROWSER_NAME_PROP = "WEB_BROWSER_NAME";
    private static final String BROWSER_NAME = getSysEnvPropertyOrDefault(BROWSER_NAME_PROP, CI_BROWSER_NAME_PROP, "chrome");

    @Override
    public void beforeEach(ExtensionContext context) {
        if (IS_FIRST_EXECUTE.getAndSet(false)) {
            String baseUrl = Environment.getWithNullValue("web.site.url");

            if (Objects.isNull(baseUrl)) {
                fail(getLangValue("selenide.web.site.url.is.empty"));
            }

            Configuration.baseUrl = baseUrl;
//        Configuration.browserSize = "1920x1080";
            Configuration.downloadsFolder = "target/selenide/downloads";
            Configuration.reportsFolder = "target/selenide/reports";
//        Configuration.proxyEnabled = true;
            Configuration.fileDownload = FOLDER;
            Configuration.screenshots = true;
            Configuration.savePageSource = true;

            String timeout = Environment.getWithNullValue("web.timeout");
            Configuration.timeout = Objects.nonNull(timeout) ? Long.parseLong(timeout) : 5000;

            String pageLoadTimeout = Environment.getWithNullValue("web.page.load.timeout");
            Configuration.pageLoadTimeout = Objects.nonNull(pageLoadTimeout) ? Long.parseLong(pageLoadTimeout) : 10000;

            Configuration.browser = BROWSER_NAME;

            Capabilities.setUp();

//        SelenideLogger.addListener("AllureSelenide", new AllureSelenide().screenshots(true).savePageSource(true));

//        switch (Configuration.browser) {
//            case "firefox" -> WebDriverManager.firefoxdriver().setup();
//            case "opera" -> WebDriverManager.operadriver().setup();
//            case "edge" -> WebDriverManager.edgedriver().setup();
//            case "chrome" -> WebDriverManager.chromedriver().setup();
//            default -> {
//                Configuration.browser = "chrome";
//                WebDriverManager.chromedriver().setup();
//            }
//        }
        }
    }

    @Override
    public void afterEach(ExtensionContext context) {
        if (WebDriverRunner.hasWebDriverStarted()) {
            Selenide.closeWebDriver();
            waitInMillis(500);
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        if (WebDriverRunner.hasWebDriverStarted() && context.getExecutionException().isPresent()) {
            SelenideAttachment.screenshot();
            SelenideAttachment.source();
        }
    }
}
