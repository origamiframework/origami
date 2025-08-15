package ru.origami.selenide;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;

import java.util.Objects;

public class Page {

    protected final String WEB_ELEMENT_ATTRIBUTE_INNER_TEXT = "innerText";

    protected final String WEB_ELEMENT_ATTRIBUTE_INNER_HTML = "innerHTML";

    protected final String WEB_ELEMENT_ATTRIBUTE_CLASS_NAME = "className";

    protected final String WEB_ELEMENT_ATTRIBUTE_HREF = "href";

    protected final String WEB_ELEMENT_ATTRIBUTE_SRC = "src";

    protected final String WEB_ELEMENT_ATTRIBUTE_PLACEHOLDER = "placeholder";

    protected final String WEB_ELEMENT_ATTRIBUTE_TYPE = "type";

    protected void clearField(SelenideElement element) {
        if (Objects.nonNull(element.getValue())) {
            for (int i = 0; i < element.getValue().length(); i++) {
                element.sendKeys(Keys.BACK_SPACE);
            }
        }
    }
}
