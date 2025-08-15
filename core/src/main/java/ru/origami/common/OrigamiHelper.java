package ru.origami.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.xml.bind.*;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.w3c.dom.Node;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.InputSource;
import ru.origami.common.models.IDateFormat;
import ru.origami.testit_allure.annotations.Step;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.fail;
import static ru.origami.common.environment.Language.getLangValue;

@UtilityClass
public class OrigamiHelper {

    /**
     * Данный класс содержит вспомогательные методы.
     * <p>Методы разбиты на блоки:
     *     <li> {@link #waitInMillis(long) Common Helper}
     *     <li> {@link #getObjectFromXml(String, Class) XML Helper}
     *     <li> {@link #getObjectFromJson(String, Class) JSON Helper}
     *     <li> {@link #getRandomFromList(List) Collection Helper}
     *     <li> {@link #getTestDataFile(String) File Helper}
     *     <li> {@link #getUUID() Identifier Helper}
     *     <li> {@link #getRandomBigDecimalFromRange(int, int, int) Number Helper}
     *     <li> {@link #getRandomLatinString(int) String Helper}
     *     <li> {@link #changeValueInXmlField(String, String, String) XML Helper}
     *     <li> {@link #setFieldNull(String, Object) Reflection Helper}
     *     <li> {@link #getDateTimeFromString(String, IDateFormat) DateTime Helper}
     */


    // Common Helper

    @Step("getLangValue:origami.helper.step.wait")
    public static void waitInMillis(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void waitInSeconds(long seconds) {
        waitInMillis(seconds * 1000);
    }

    /**
     * @param value строка, которая будет в дальнейшем преобразована в объект
     * @param clazz тип возвращаемого объекта
     * @return сериализованный из value объект
     */
    public static <T> T getObjectFromString(String value, Class<T> clazz) {
        if (Objects.isNull(value)) {
            return null;
        }

        T parsedValue = getObjectFromJson(value, clazz, false);

        if (Objects.isNull(parsedValue)) {
            parsedValue = getObjectFromXml(value, clazz, false);
        }

        return parsedValue;
    }

    /**
     * Метод, реализующий преобразование неформатированных строк в нужный формат
     *
     * @param unformattedStr необработанная строка
     * @return строка в нужном формате (XML, JSON)
     */
    public static String prettyFormat(String unformattedStr) {
        if (unformattedStr == null) {
            return null;
        }

        unformattedStr = unformattedStr.trim();
        Pattern xmlPattern = Pattern.compile("^(<\\?xml[\\s\\w\\d=\".-]+\\?>)?(<.*>)", Pattern.DOTALL);
        Matcher matcher = xmlPattern.matcher(unformattedStr);

        if (matcher.find()) {
            return prettyXmlFormat(unformattedStr);
        } else {
            return prettyJsonFormat(unformattedStr);
        }
    }

    /**
     * @param value строка, которая будет в дальнейшем преобразована в объект
     * @param clazz тип Enum возвращаемого объекта
     * @return сериализованный из value объект
     */
    public static <T extends Enum<T>> T getEnumFromString(String value, Class<T> clazz) {
        if (Objects.isNull(value)) {
            return null;
        }

        return EnumSet.allOf(clazz)
                .stream()
                .filter(e -> e.name().equals(value))
                .findFirst()
                .orElse(null);
    }


    // XML Helper

    /**
     * @param xml   строка, которая будет в дальнейшем преобразована в объект
     * @param clazz тип возвращаемого объекта
     * @return сериализованный из xml объект
     */
    public static <T> T getObjectFromXml(String xml, Class<T> clazz) {
        return getObjectFromXml(xml, clazz, true);
    }

    /**
     * @param xml      строка, которая будет в дальнейшем преобразована в объект
     * @param clazz    тип возвращаемого объекта
     * @param withFail зафейлить тест при ошибке парсинга
     * @return сериализованный из xml объект
     */
    public static <T> T getObjectFromXml(String xml, Class<T> clazz, boolean withFail) {
        if (xml == null) {
            return null;
        }

//        String unescapedXml = StringEscapeUtils.unescapeXml(xml.trim());
        String unescapedXml = xml.trim();
        Pattern pattern = Pattern.compile("^(<\\?xml[\\s\\w\\d=\".-]+\\?>)?(<.*>)", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(unescapedXml);
        String cleanXml = null;

        if (matcher.find()) {
            cleanXml = matcher.group(2);
        } else {
            if (withFail) {
                fail(getLangValue("xml.string.not.parsed").formatted(unescapedXml));
            }
        }

        try {
            return JAXB.unmarshal(new StringReader(cleanXml), clazz);
        } catch (Exception e) {
            if (withFail) {
                fail(getLangValue("common.string.not.parsed").formatted(e.getMessage(), cleanXml));
            }
        }

        return null;
    }

    /**
     * @param value объект, который будет преобразован в xml строку
     * @return строка в формате xml
     */
    public static <T> String getObjectAsXmlString(T value) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(value.getClass());
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
            XmlRootElement xmlRootElement = value.getClass().getAnnotation(XmlRootElement.class);
            final String defaultValue = "##default";

            if (Objects.isNull(xmlRootElement)) {
                fail(getLangValue("xml.annotation.not.found"));
            }

            QName qName = new QName(defaultValue.equals(xmlRootElement.namespace()) ? null : xmlRootElement.namespace(),
                    defaultValue.equals(xmlRootElement.name()) ? null : xmlRootElement.name());
            JAXBElement<T> root = new JAXBElement<T>(qName, (Class<T>) value.getClass(), value);

            StringWriter sw = new StringWriter();
            sw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            jaxbMarshaller.marshal(root, sw);

            return sw.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
            fail(getLangValue("xml.object.not.parsed").formatted(e.getMessage()));
        }

        return null;
    }

    public static String prettyXmlFormat(String unformattedXml) {
        try {
            final InputSource src = new InputSource(new StringReader(unformattedXml));
            final Node document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(src).getDocumentElement();
            final Boolean keepDeclaration = unformattedXml.startsWith("<?xml");

            final DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
            final DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("LS");
            final LSSerializer writer = impl.createLSSerializer();

            Writer stringWriter = new StringWriter();
            LSOutput lsOutput = impl.createLSOutput();
            lsOutput.setEncoding("UTF-8");
            lsOutput.setCharacterStream(stringWriter);

            writer.getDomConfig().setParameter("format-pretty-print", Boolean.TRUE);
            writer.getDomConfig().setParameter("xml-declaration", keepDeclaration);
            writer.write(document, lsOutput);
            String formattedXml = stringWriter.toString().trim();

            return keepDeclaration
                    ? new StringBuilder(formattedXml)
                    .insert(formattedXml.indexOf('>') + 1, "\n")
                    .toString()
                    : formattedXml;
        } catch (Exception e) {
            return unformattedXml;
        }
    }


    // JSON Helper

    /**
     * @param json  строка, которая будет в дальнейшем преобразована в объект
     * @param clazz
     * @param <T>
     * @return сериализованный из Json объект
     */
    public static <T> T getObjectFromJson(String json, Class<T> clazz) {
        return getObjectFromJson(json, clazz, true);
    }

    /**
     * @param json     строка, которая будет в дальнейшем преобразована в объект
     * @param clazz
     * @param withFail зафейлить тест при ошибке парсинга
     * @param <T>
     * @return сериализованный из JSON объект
     */
    public static <T> T getObjectFromJson(String json, Class<T> clazz, boolean withFail) {
        if (json == null) {
            return null;
        }

        try {
            return new ObjectMapper().registerModule(new JavaTimeModule())
                    .registerModule(new JodaModule())
                    .readValue(json.trim(), clazz);
        } catch (IOException e) {
            if (withFail) {
                fail(getLangValue("common.string.not.parsed").formatted(e.getMessage(), json.trim()));
            }
        }

        return null;
    }

    /**
     * @param value объект, который будет преобразован в json строку с форматированием
     * @return строка в формате json
     */
    public static String getObjectAsJsonString(Object value) {
        if (Objects.isNull(value)) {
            return null;
        }

        try {
            return new ObjectMapper().registerModule(new JavaTimeModule())
                    .registerModule(new JodaModule())
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(value);
        } catch (Exception e) {
            fail(getLangValue("json.object.not.parsed").formatted(e.getMessage()));
        }

        return null;
    }

    /**
     * @param value объект, который будет преобразован в json строку без форматирования
     * @return строка в формате json
     */
    public static String getObjectAsJsonStringWithoutPrettify(Object value) {
        if (Objects.isNull(value)) {
            return null;
        }

        try {
            return new ObjectMapper().registerModule(new JavaTimeModule())
                    .registerModule(new JodaModule())
                    .writeValueAsString(value);
        } catch (Exception e) {
            fail(getLangValue("json.object.not.parsed").formatted(e.getMessage()));
        }

        return null;
    }

    public static String prettyJsonFormat(String unformattedJson) {
        try {
            return (new JSONObject(unformattedJson.replaceAll(":\"\\{\\\\\"", ": {\"")
                    .replaceAll("}\"}", "}}")
                    .replaceAll("\\\\\"", "\"")))
                    .toString(4);
        } catch (Exception ex) {
            return unformattedJson;
        }
    }


    // Collection Helper

    public static <T> T getRandomFromList(List<T> list) {
        return (T) list.get(new Random().nextInt(list.size()));
    }

    public static <T> T getRandomFromList(T[] list) {
        return (T) list[new Random().nextInt(list.length)];
    }


    // File Helper

    public static File getTestDataFile(String fileName) {
        return new File(format("./src/main/resources/test_data/%s", fileName));
    }

    public static List<List<String>> readCsvFile(String fileName, String splitter, int linesToSkip) {
        List<List<String>> records = new ArrayList<>();
        List<String> fileLines = skipLinesInFile(readFromFile(getTestDataFile(fileName)), linesToSkip);
        fileLines.forEach(line -> records.add(Arrays.asList(line.split(splitter, -1))));

        return records;
    }

    public static void changeSourceIdInFile(String fileName, String sourceId) {
        File file = getTestDataFile(fileName);
        List<String> fileLines = readFromFile(file);
        fileLines.set(0, fileLines.get(0).replaceAll(";.+;", format(";%s;", sourceId)));
        writeInFile(file, fileLines);
    }

    public static List<String> skipLinesInFile(List<String> fileLines, int linesToSkip) {
        return fileLines.subList(linesToSkip, fileLines.size());
    }

    public static String getLineFromFile(String fileName, int linesNumber) {
        return readFromFile(getTestDataFile(fileName)).get(linesNumber - 1);
    }

    public static void addLineToFile(String fileName, int lineNumber, String lineToAdd) {
        File file = getTestDataFile(fileName);
        List<String> fileLines = readFromFile(file);
        fileLines.add(lineNumber - 1, lineToAdd);
        writeInFile(file, fileLines);
    }

    public static void setLineInFile(String fileName, int lineNumber, String lineToSet) {
        File file = getTestDataFile(fileName);
        List<String> fileLines = readFromFile(file);
        fileLines.set(lineNumber - 1, lineToSet);
        writeInFile(file, fileLines);
    }

    public static void deleteLineFromFile(String fileName, int lineNumber) {
        File file = getTestDataFile(fileName);
        List<String> fileLines = readFromFile(file);
        fileLines.remove(lineNumber - 1);
        writeInFile(file, fileLines);
    }

    public static void copyFile(String fileFrom, String fileTo) {
        try {
            Files.copy(getTestDataFile(fileFrom).toPath(), getTestDataFile(fileTo).toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            exception.printStackTrace();
            fail(getLangValue("file.copy.error").formatted(fileFrom, fileTo, exception.getMessage()));
        }
    }

    public static void cleanFile(String fileName) {
        try {
            Files.newBufferedWriter(getTestDataFile(fileName).toPath(), StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException exception) {
            exception.printStackTrace();
            fail(getLangValue("file.clear.error").formatted(fileName, exception.getMessage()));
        }
    }

    public static List<String> readFromFile(File file) {
        List<String> lines = new ArrayList<>();

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine());
            }
        } catch (FileNotFoundException exception) {
            exception.printStackTrace();
            fail(getLangValue("file.not.found.error").formatted(file.getName(), exception.getMessage()));
        }

        lines.removeAll(Collections.singleton(""));

        return lines;
    }

    public static void writeInFile(File file, List<String> fileLines) {
        try (FileWriter writer = new FileWriter(file)) {
            for (String line : fileLines) {
                writer.write(line + "\n");
            }
        } catch (IOException exception) {
            exception.printStackTrace();
            fail(getLangValue("file.write.error").formatted(file.getName(), exception.getMessage()));
        }
    }


    // Identifier Helper

    public static UUID getUUID() {
        return UUID.randomUUID();
    }

    public static String getStringUUID() {
        return getUUID().toString().toUpperCase();
    }


    // Number Helper

    public static BigDecimal getRandomBigDecimalFromRange(int minValue, int maxValue, int scale) {
        BigDecimal min = BigDecimal.valueOf(minValue);
        BigDecimal max = BigDecimal.valueOf(maxValue);
        BigDecimal randomBigDecimal = min.add(BigDecimal.valueOf(Math.random()).multiply(max.subtract(min)));

        return randomBigDecimal.setScale(scale, RoundingMode.HALF_UP);
    }

    public static int getRandomIntFromRange(int min, int max) {
        return new Random().nextInt(max + 1 - min) + min;
    }

    public static int getRandomInt(int max) {
        return new Random().nextInt(max + 1);
    }

    public static BigDecimal stripZeros(BigDecimal number) {
        return new BigDecimal(number.stripTrailingZeros().toPlainString());
    }


    // String Helper

    public static String getRandomLatinString(int length) {
        return RandomStringUtils.randomAlphabetic(length).toUpperCase();
    }

    public static String getRandomLatinString(int minLength, int maxLength) {
        return getRandomLatinString(new Random().nextInt(maxLength - minLength + 1) + minLength);
    }

    public static String getRandomCyrillicString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < length; i++) {
            char randomChar = (char) (0x0410 + random.nextInt(0x042F - 0x0410 + 1));
            sb.append(randomChar);
        }

        return sb.toString();
    }

    public static String getRandomCyrillicString(int minLength, int maxLength) {
        return getRandomCyrillicString(new Random().nextInt(maxLength - minLength + 1) + minLength);
    }

    public static String getRandomNumberAsString(int length) {
        return RandomStringUtils.randomNumeric(length);
    }

    public static String getRandomNumberAsString(int startValue, int endValue) {
        return String.valueOf(getRandomIntFromRange(startValue, endValue));
    }

    public static String getRandomCaseString(String value) {
        String outResult;

        do {
            char[] inputValue = value.toCharArray();
            char[] result = new char[value.length()];

            for (int i = 0; i < value.length(); i++) {
                result[i] = getRandomInt(1) == 0 ? Character.toUpperCase(inputValue[i]) : Character.toLowerCase(inputValue[i]);
            }

            outResult = String.valueOf(result);
        } while (outResult.equals(value));

        return outResult;
    }

    public static String removeTrailingZeroes(String s) {
        return s.replaceAll("(?!^)0+$", "");
    }


    // XML Helper

    public static String changeValueInXmlField(String xml, String field, String value) {
        return xml.replaceAll("<%s>.*</%s>".formatted(field, field), "<%s>%s</%s>".formatted(field, value, field));
    }


    // Reflection Helper

    /**
     * Метод обнуления полей класса описывающего XML или JSON-структуры
     * Класс Person:
     * Person:
     *        name
     *        surname
     *        age
     *        taskList:
     *                task:
     *                     field1
     *                     field2
     *                     ...
     *                task:
     *                     field1
     *                     field2
     *                     ...
     *  Примеры classFieldPath:
     *  Доступ к полям подструктур осуществляется через ".", имена подклассов которые находятся в списочном поле в параметре не указываются,
     *  в любом списочном поле выбирается первый элемент.
     *      Для обнуления полей корневой структуры : "name", "surname" ...
     *      Для обнуления поля field1 в структуре 'task': "taskList.field1"
     *      (названия классов которые находятся в списочном поле не описываются, поле удаляется в первой сущности в списке)
     *
     *
     * @param classFieldPath - строка содержащая путь к обнуляемому полю (примеры выше)
     * @param request - объект описывающий структуру данных запроса (JSON, XML)
     */
    public static void setFieldNull(String classFieldPath, Object request) {
        setValueToField(classFieldPath, request, null);
    }

    public static void setValueToField(String classFieldPath, Object request, Object value) {
        try {
            List<String> fieldNames = List.of(classFieldPath.split("\\."));
            Object parentObject = request;
            Field objectField = request.getClass().getDeclaredField(fieldNames.get(0));
            objectField.setAccessible(true);

            if (fieldNames.size() > 1) {
                parentObject = objectField.get(request);

                for (int i = 1; i < fieldNames.size(); i++) {
                    if (parentObject instanceof Iterable) {
                        parentObject = ((List<?>) parentObject).get(0);
                    }

                    objectField = parentObject.getClass().getDeclaredField(fieldNames.get(i));
                    objectField.setAccessible(true);

                    if (i < fieldNames.size() - 1) {
                        parentObject = objectField.get(parentObject);
                    }
                }
            }

            objectField.set(parentObject, value);
        } catch (Exception exception) {
            exception.printStackTrace();
            fail(getLangValue("reflection.change.field.error").formatted(classFieldPath, exception.getMessage()));
        }
    }


    // DateTime Helper

    public static LocalDateTime getDateTimeFromString(String value, IDateFormat format) {
        return LocalDateTime.parse(value, format.getDateTimeFormatter());
    }

    public static ZonedDateTime getZonedDateTimeFromString(String value, IDateFormat format) {
        return ZonedDateTime.parse(value, format.getDateTimeFormatter());
    }

    public static LocalDate getDateFromString(String value, IDateFormat format) {
        return LocalDate.parse(value, format.getDateTimeFormatter());
    }

    public static LocalTime getTimeFromString(String value, IDateFormat format) {
        return LocalTime.parse(value, format.getDateTimeFormatter());
    }

    public static LocalDateTime getCurrentLocalDateTimeWithoutWeekend() {
        LocalDateTime now = LocalDateTime.now();

        while (isWeekendDay(now.getDayOfWeek())) {
            now = now.plusDays(1);
        }

        return now;
    }

    public static String getDateAsString(IDateFormat pattern, LocalDate date) {
        if (Objects.isNull(date)) {
            return null;
        }

        return date.format(DateTimeFormatter.ofPattern(pattern.getPattern()));
    }

    public static String getDateTimeAsString(IDateFormat pattern, LocalDateTime dateTime) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern.getPattern());

        if (Objects.nonNull(dateTime)) {
            return dateTime.format(dtf);
        }

        return null;
    }

    public static String getZonedDateTimeAsString(IDateFormat pattern, ZonedDateTime dateTime) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern.getPattern());

        if (Objects.nonNull(dateTime)) {
            return dateTime.format(dtf);
        }

        return null;
    }

    public static boolean isWeekendDay(DayOfWeek d) {
        return (d == DayOfWeek.SATURDAY || d == DayOfWeek.SUNDAY);
    }

    public static LocalDateTime getLocalDateTimeWithoutTime(LocalDateTime time) {
        if (Objects.isNull(time)) {
            return null;
        }

        return time.toLocalDate().atStartOfDay();
    }

    public static LocalDateTime getDateTimeForRequest(LocalDateTime dateTime) {
        if (Objects.isNull(dateTime)) {
            return null;
        }

        return dateTime.with(LocalTime.MIN)
                .atZone(ZoneId.of("Europe/Moscow"))
                .withZoneSameInstant(ZoneOffset.UTC)
                .toLocalDateTime();
    }

    public static LocalDateTime setDateDifferentFromCurrent(Long daysDifferenceOn) {
        return LocalDateTime.now().withNano(0).plusDays(daysDifferenceOn);
    }

    public static ZonedDateTime getDateDifferentFromCurrent(long daysDifferenceOn) {
        return ZonedDateTime.now().withNano(0).plusDays(daysDifferenceOn);
    }

    public static ZonedDateTime getCurrentDateWithZeroTime() {
        return ZonedDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0).minusHours(3).withFixedOffsetZone();
    }

    public static String getTimeAsString(IDateFormat pattern, LocalTime time) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern.getPattern());

        if (time != null) {
            return time.format(dtf);
        }

        return null;
    }

    public static String getOffsetDateTimeAsString(IDateFormat pattern, OffsetDateTime time) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern.getPattern());

        if (time != null) {
            return time.format(dtf);
        }

        return null;
    }
}