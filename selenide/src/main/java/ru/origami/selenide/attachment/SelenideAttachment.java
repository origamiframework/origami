package ru.origami.selenide.attachment;

import com.codeborne.selenide.Selenide;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.OutputType;
import ru.origami.testit_allure.allure.java_commons.Attachment;

@Slf4j
public class SelenideAttachment {

    @Attachment(type = "image/png")
    public static byte[] screenshot() {
        return Selenide.screenshot(OutputType.BYTES);
    }

    @Attachment(type = "text/html")
    public static byte[] source() {
        return Selenide.webdriver().driver().source().getBytes();
    }
}
