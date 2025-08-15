package ru.origami.selenide;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import groovy.util.logging.Slf4j;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.Dimension;
import ru.origami.testit_allure.annotations.Step;

import java.util.Set;

import static com.codeborne.selenide.Selenide.localStorage;
import static org.junit.jupiter.api.Assertions.fail;
import static ru.origami.common.environment.Language.getLangValue;

@Slf4j
public class WebSteps {

    @Step("getLangValue:selenide.step.open.url")
    public void openUrl(String url) {
        Selenide.open(url);
    }

    @Step("getLangValue:selenide.step.current.url")
    public String getCurrentUrl() {
        return WebDriverRunner.getWebDriver().getCurrentUrl();
    }

    @Step("getLangValue:selenide.step.refresh.page")
    public void refreshPage() {
        Selenide.refresh();
    }

    @Step("getLangValue:selenide.step.close.driver")
    public void closeDriver() {
        Selenide.closeWebDriver();
    }

    @Step("getLangValue:selenide.step.close.window")
    public void closeWindow() {
        Selenide.closeWindow();
    }

    @Step("getLangValue:selenide.step.browser.size")
    public void setBrowserSize(int width, int height) {
        WebDriverRunner.getWebDriver().manage().window().setSize(new Dimension(width, height));
    }

    @Step("getLangValue:selenide.step.back")
    public void back() {
        Selenide.back();
    }

    @Step("getLangValue:selenide.step.forward")
    public void forward() {
        Selenide.forward();
    }

    @Step("getLangValue:selenide.step.switch.to.window")
    public void switchToWindow(int windowNumber) {
        Selenide.switchTo().window(windowNumber);
    }

    @Step("getLangValue:selenide.step.get.local.storage")
    public String getLocalStorage(String key) {
        return localStorage().getItem(key);
    }

    @Step("getLangValue:selenide.step.set.item.in.local.storage")
    public void setItemInLocalStorage(String item, String value) {
        Selenide.executeJavaScript(String.format("window.localStorage.setItem('%s','%s');", item, value));
    }

    @Step("getLangValue:selenide.step.get.cookies")
    public Set<Cookie> getCookies() {
        return WebDriverRunner.getWebDriver().manage().getCookies();
    }

    @Step("getLangValue:selenide.step.get.cookie.by.name")
    public Cookie getCookieByName(String cookieName) {
        try {
            WebDriverRunner.getWebDriver().manage().getCookieNamed(cookieName);
        } catch (NullPointerException ignore) {
            fail(getLangValue("selenide.cookie.not.found.error").formatted(cookieName));
        }

        return null;
    }

    @Step("getLangValue:selenide.step.set.cookie")
    public void setCookie(String cookieName, String cookieValue) {
        if (WebDriverRunner.getWebDriver().manage().getCookieNamed(cookieName) != null) {
            WebDriverRunner.getWebDriver().manage().deleteCookieNamed(cookieName);
        }

        WebDriverRunner.getWebDriver().manage().addCookie(new Cookie(cookieName, cookieValue));
    }

    @Step("getLangValue:selenide.step.clear.browser.local.storage")
    public void clearBrowserLocalStorage() {
        Selenide.clearBrowserLocalStorage();
    }

    @Step("getLangValue:selenide.step.clear.browser.cookies")
    public void clearBrowserCookies() {
        Selenide.clearBrowserCookies();
    }

    @Step("getLangValue:selenide.step.clear.browser.cookies.and.local.storage")
    public void clearBrowserCookiesAndLocalStorage() {
        Selenide.clearBrowserCookies();
        Selenide.clearBrowserLocalStorage();
    }

    @Step("getLangValue:selenide.step.execute.java.script")
    public void executeJavaScript(String jsCode) {
        Selenide.executeJavaScript(jsCode);
    }
    
    @Step("getLangValue:selenide.step.execute.java.script")
    public void executeJavaScript(String jsCode, String... arguments) {
        Selenide.executeJavaScript(jsCode, (Object[]) arguments);
    }
    
    @Step("getLangValue:selenide.step.execute.async.java.script")
    public void executeAsyncJavaScript(String jsCode) {
        Selenide.executeAsyncJavaScript(jsCode);
    }
    
    @Step("getLangValue:selenide.step.execute.async.java.script")
    public void executeAsyncJavaScript(String jsCode, String... arguments) {
        Selenide.executeAsyncJavaScript(jsCode, (Object[]) arguments);
    }
}
