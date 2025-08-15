package ru.origami.common.asserts;

import ru.origami.testit_allure.annotations.Step;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.fail;
import static ru.origami.common.environment.Language.getLangValue;

public class XmlAsserts {

    /**
     *
     * @param xmlData строка в формате XML
     * @param xsdPath XSD-схема
     */
    @Step("getLangValue:asserts.step.validate.xml.against.xsd.schema")
    public static void validateXmlAgainstXsdSchema(String xmlData, String xsdPath) {
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(xsdPath));
            Validator validator = schema.newValidator();
            StringReader reader = new StringReader(xmlData);
            validator.validate(new StreamSource(reader));
        } catch (Exception exception) {
            exception.printStackTrace();
            fail(getLangValue("xsd.validation.error").formatted(exception.getMessage()));
        }
    }
}
