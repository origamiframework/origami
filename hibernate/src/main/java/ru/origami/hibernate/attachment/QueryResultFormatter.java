package ru.origami.hibernate.attachment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.annotations.Formula;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static ru.origami.common.environment.Language.getLangValue;

public class QueryResultFormatter {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");

    public static String getResultOfUpdate(String query, int resultRows) {
        int currentResultRows = resultRows;

        if (currentResultRows > 20) {
            String currentRows = String.valueOf(currentResultRows);
            currentResultRows = Integer.parseInt(currentRows.substring(currentRows.length() - 1));
        }

        String queryResultRows = currentResultRows == 1
                ? getLangValue("hibernate.one.row")
                : currentResultRows > 1 && currentResultRows < 5
                    ? getLangValue("hibernate.two.rows")
                    : getLangValue("hibernate.many.rows");

        StringBuilder resultInfo = new StringBuilder();

        if (query.startsWith("update")) {
            resultInfo.append("%s ".formatted(currentResultRows == 1
                    ? getLangValue("hibernate.updated.word")
                    : getLangValue("hibernate.updated.words")));
        } else if (query.startsWith("delete")) {
            resultInfo.append("%s ".formatted(currentResultRows == 1
                    ? getLangValue("hibernate.deleted.word")
                    : getLangValue("hibernate.deleted.words")));
        } else if (query.startsWith("insert")) {
            resultInfo.append("%s ".formatted(currentResultRows == 1
                    ? getLangValue("hibernate.inserted.word")
                    : getLangValue("hibernate.inserted.words")));
        }

        return resultInfo.append(resultRows).append(" ").append(queryResultRows).append("\n").toString();
    }

    public static byte[] getResult(List<Object> result) {
        if (CollectionUtils.isEmpty(result)) {
            return null;
        }

        Class resultClass = result.get(0).getClass();
        LinkedList<Field> declaredFields = getCommonDeclaredFields(result.get(0), resultClass);

        if (CollectionUtils.isEmpty(declaredFields)) {
            declaredFields = getCustomDeclaredFields(resultClass);
        }

        if (CollectionUtils.isEmpty(declaredFields)) {
            declaredFields = getDeclaredFields(resultClass);
        }

        Class superClass = resultClass.getSuperclass();

        while (superClass != Object.class && !(result.get(0) instanceof Number) && result.get(0) != String.class) {
            declaredFields.addAll(getDeclaredFields(superClass));
            superClass = superClass.getSuperclass();
        }

        Map<Integer, Integer> cellWidths = new HashMap<>();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("SQL Result");

        setResultHeaders(declaredFields, workbook, sheet, cellWidths);
        setResultValues(result, declaredFields, workbook, sheet, cellWidths);
        setColumnsWidth(sheet, cellWidths);

        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
            workbook.write(byteOut);
            workbook.close();

            return byteOut.toByteArray();
        } catch (IOException ex) {
            ex.printStackTrace();

            return null;
        }
    }

    private static LinkedList<Field> getDeclaredFields(Class resultClass) {
        return Arrays.stream(resultClass.getDeclaredFields())
                .filter(f -> Modifier.isPrivate(f.getModifiers()))
                .filter(f -> {
                    List<Class<? extends Annotation>> currAnnotations = Arrays.stream(f.getDeclaredAnnotations())
                            .map(Annotation::annotationType)
                            .collect(Collectors.toList());

                    for (Class<? extends Annotation> annotation : currAnnotations) {
                        if (annotation == Column.class || annotation == Formula.class) {
                            return true;
                        }
                    }

                    return false;
                }).collect(Collectors.toCollection(LinkedList::new));
    }

    private static LinkedList<Field> getCommonDeclaredFields(Object result, Class resultClass) {
        return Arrays.stream(resultClass.getDeclaredFields())
                .filter(f -> {
                    if (result instanceof Number || result instanceof String) {
                        return "value".equals(f.getName());
                    }

                    return false;
                }).collect(Collectors.toCollection(LinkedList::new));
    }

    private static LinkedList<Field> getCustomDeclaredFields(Class resultClass) {
        if (resultClass.getDeclaredAnnotation(Entity.class) == null) {
            return Arrays.stream(resultClass.getDeclaredFields()).collect(Collectors.toCollection(LinkedList::new));
        }

        return null;
    }

    private static void setResultHeaders(LinkedList<Field> declaredFields, Workbook workbook, Sheet sheet,
                                         Map<Integer, Integer> cellWidths) {
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 14);
        font.setBold(true);
        headerStyle.setFont(font);

        Row header = sheet.createRow(0);
        Cell headerCell;

        for (int num = 0; num < declaredFields.size(); num++) {
            headerCell = header.createCell(num);
            headerCell.setCellValue(getFieldName(declaredFields.get(num)));
            headerCell.setCellStyle(headerStyle);
            updateColumnWidth(cellWidths, num, declaredFields.get(num).getName().length());
        }
    }

    private static String getFieldName(Field field) {
        Column column = field.getDeclaredAnnotation(Column.class);

        if (column != null) {
            return column.name();
        } else {
            return field.getName();
        }
    }

    private static void setResultValues(List<Object> resultList, LinkedList<Field> declaredFields, Workbook workbook,
                                        Sheet sheet, Map<Integer, Integer> cellWidths) {
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        XSSFFont cellFont = ((XSSFWorkbook) workbook).createFont();
        cellFont.setFontName("Arial");
        cellFont.setFontHeightInPoints((short) 14);
        style.setFont(cellFont);

        int rowNum = 1;

        for (Object result : resultList) {
            Row row = sheet.createRow(rowNum++);

            for (int cellNum = 0; cellNum < declaredFields.size(); cellNum++) {
                Cell cell = row.createCell(cellNum);

                try {
                    Field field = declaredFields.get(cellNum);
                    field.setAccessible(true);
                    Object value = field.get(result);

                    cell.setCellValue(getCeilValue(value));
                    updateColumnWidth(cellWidths, cellNum, getCeilValue(value).length());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    cell.setCellValue(getLangValue("hibernate.excel.result.value.error").formatted(ex.getMessage()));
                }

                cell.setCellStyle(style);
            }
        }
    }

    private static void setColumnsWidth(Sheet sheet, Map<Integer, Integer> cellWidths) {
        for (Map.Entry<Integer, Integer> cellWidth : cellWidths.entrySet()) {
            sheet.setColumnWidth(cellWidth.getKey(), cellWidth.getValue());
        }
    }

    private static void updateColumnWidth(Map<Integer, Integer> cellWidths, int index, int width) {
        int maxWidth = 20000;
        width = (int) Math.ceil((width * 430D) / 1100) * 1100;

        if (cellWidths.containsKey(index)) {
            if (cellWidths.get(index) < width) {
                cellWidths.put(index, Math.min(width, maxWidth));
            }
        } else {
            cellWidths.put(index, Math.min(width, maxWidth));
        }
    }

    private static String getCeilValue(Object value) {
        if (value == null) {
            return "[null]";
        }

        if (value instanceof Date) {
            return DATE_FORMAT.format(value);
        } else if (value instanceof Calendar) {
            return DATE_FORMAT.format(((Calendar)value).getTime());
        } else if (value instanceof byte[]) {
            return new String((byte[]) value);
        } else {
            return value.toString().length() > 32767 ? getLangValue("hibernate.excel.big.value") : value.toString();
        }
    }
}
