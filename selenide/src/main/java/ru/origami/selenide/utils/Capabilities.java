package ru.origami.selenide.utils;

import com.codeborne.selenide.Configuration;
import lombok.experimental.UtilityClass;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import ru.origami.common.environment.Environment;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static ru.origami.common.environment.Environment.isLocal;

@UtilityClass
public class Capabilities {

    private static final List<String> COMMON_ARGS = Arrays.asList(
            "--headless",                // без GUI
            "--no-sandbox",              // для Docker/CI
            "--disable-gpu",             // реже нужен, но безопасно
            "--disable-dev-shm-usage"    // для /dev/shm в Docker
    );

    public static void setUp() {
        if (!isLocal()) {
            MutableCapabilities options =
                    switch (Configuration.browser.toLowerCase()) {
                        case "chrome" -> getChromeCapabilities();
                        case "firefox" -> getFirefoxCapabilities();
                        case "yandex" -> getYandexCapabilities();
                        default -> null;
                    };

            if (Objects.nonNull(options)) {
                Configuration.browserCapabilities = options;
            }

            Configuration.headless = true;
        }
    }

    private static ChromeOptions getChromeCapabilities() {
        ChromeOptions options = new ChromeOptions();

        COMMON_ARGS.forEach(options::addArguments);

        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});

        return options;
    }

    private static FirefoxOptions getFirefoxCapabilities() {
        FirefoxOptions options = new FirefoxOptions();

        COMMON_ARGS.forEach(options::addArguments);

        options.addPreference("dom.webdriver.enabled", true);
        options.addPreference("useAutomationExtension", false);
        options.addPreference("browser.tabs.warnOnClose", false);

        return options;
    }

    private static ChromeOptions getYandexCapabilities() {
        ChromeOptions options = new ChromeOptions();

        COMMON_ARGS.forEach(options::addArguments);

        options.setBinary(Environment.get("web.browser.yandex.binary"));
        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});

        return options;
    }
}
