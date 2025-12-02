package ru.origami.selenide.utils;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import ru.origami.common.environment.Environment;
import ru.origami.selenide.attachment.SelenideAttachment;

import java.util.Objects;

import static com.codeborne.selenide.FileDownloadMode.FOLDER;
import static org.junit.jupiter.api.Assertions.fail;
import static ru.origami.common.environment.Environment.isLocal;
import static ru.origami.common.environment.Language.getLangValue;

public class WebDriverSourceExtension implements BeforeEachCallback, AfterEachCallback, AfterTestExecutionCallback {

    private static String DEFAULT_BROWSER_NAME = "chrome";

    @Override
    public void beforeEach(ExtensionContext context) {
        String baseUrl = Environment.getWithNullValue("web.site.url");

        if (Objects.isNull(baseUrl)) {
            fail(getLangValue("selenide.web.site.url.is.empty"));
        }

        Configuration.baseUrl = baseUrl;
        Configuration.browserSize = "1920x1080";
        Configuration.downloadsFolder = "target/selenide/downloads";
        Configuration.reportsFolder = "target/selenide/reports";
//        Configuration.proxyEnabled = true;
        Configuration.fileDownload = FOLDER;

        String timeout = Environment.getWithNullValue("web.timeout");
        Configuration.timeout = Objects.nonNull(timeout) ? Long.parseLong(timeout) : 5000;

        String pageLoadTimeout = Environment.getWithNullValue("web.page.load.timeout");
        Configuration.pageLoadTimeout = Objects.nonNull(pageLoadTimeout) ? Long.parseLong(pageLoadTimeout) : 10000;

        String browser = Environment.getWithNullValue("web.browser.name");
        Configuration.browser = Objects.nonNull(browser) ? browser : DEFAULT_BROWSER_NAME;

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

        if (!isLocal()) {
            Configuration.headless = true;
        }
    }

    @Override
    public void afterEach(ExtensionContext context) {
        Selenide.closeWebDriver();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        if (context.getExecutionException().isPresent()) {
            SelenideAttachment.screenshot();
            SelenideAttachment.source();
        }
    }
}
