package ru.origami.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.origami.common.models.AllureResultModel;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static ru.origami.common.environment.Language.getLangValue;

@Slf4j
public class AllureToExcelConverter {

    public static void main(String[] args) {
        try {
            String allureResultsDir = "./target/allure-results";
            String outputExcelPath = "./target/allure-report_%s.xlsx".formatted(LocalDate.now().format(DateTimeFormatter.ofPattern("dd_MM_yyyy")));

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Allure Results");

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 11);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            Font standartFont = workbook.createFont();
            standartFont.setFontHeightInPoints((short) 10);
            CellStyle centerCellStyle = workbook.createCellStyle();
            centerCellStyle.setAlignment(HorizontalAlignment.CENTER);
            centerCellStyle.setFont(standartFont);

            CellStyle leftCellStyle = workbook.createCellStyle();
            leftCellStyle.setAlignment(HorizontalAlignment.LEFT);
            leftCellStyle.setFont(standartFont);

            Font redStatusFont = workbook.createFont();
            redStatusFont.setColor(IndexedColors.RED.getIndex());
            redStatusFont.setFontHeightInPoints((short) 10);
            CellStyle redStatusCellStyle = workbook.createCellStyle();
            redStatusCellStyle.setAlignment(HorizontalAlignment.CENTER);
            redStatusCellStyle.setFont(redStatusFont);

            Font greenStatusFont = workbook.createFont();
            greenStatusFont.setColor(IndexedColors.GREEN.getIndex());
            greenStatusFont.setFontHeightInPoints((short) 10);
            CellStyle greenStatusCellStyle = workbook.createCellStyle();
            greenStatusCellStyle.setAlignment(HorizontalAlignment.CENTER);
            greenStatusCellStyle.setFont(greenStatusFont);

            String[] headers = {"№", getLangValue("allure.excel.field.name"),
                    getLangValue("allure.excel.field.result"), getLangValue("allure.excel.field.duration")};
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            sheet.createFreezePane(0, 1);
            sheet.setAutoFilter(CellRangeAddress.valueOf("B1:D1"));

            File resultsDir = new File(allureResultsDir);
            File[] jsonFiles = resultsDir.listFiles((dir, name) -> name.endsWith("result.json"));

            if (jsonFiles == null || jsonFiles.length == 0) {
                throw new RuntimeException(getLangValue("allure.excel.json.error").formatted(allureResultsDir));
            }

            ObjectMapper mapper = new ObjectMapper();
            int rowNum = 1;
            int caseNum = 1;

            List<AllureResultModel> allureResults = new ArrayList<>();

            for (File jsonFile : jsonFiles) {
                String json = new String(Files.readAllBytes(Paths.get(jsonFile.getPath())));
                allureResults.add(mapper.readValue(json, AllureResultModel.class));
            }

            allureResults.sort(Comparator.comparing(AllureResultModel::getFullName));

            for (AllureResultModel result : allureResults) {
                Row row = sheet.createRow(rowNum++);
                Cell numCell = row.createCell(0);
                numCell.setCellValue(caseNum++);
                numCell.setCellStyle(centerCellStyle);

                Cell nameCell = row.createCell(1);
                nameCell.setCellValue(result.getName().replaceAll("\n", ""));
                nameCell.setCellStyle(leftCellStyle);

                Cell statusCell = row.createCell(2);
                statusCell.setCellValue(result.getStatus());

                if ("passed".equals(result.getStatus())) {
                    statusCell.setCellStyle(greenStatusCellStyle);
                } else if ("failed".equals(result.getStatus())) {
                    statusCell.setCellStyle(redStatusCellStyle);
                } else {
                    statusCell.setCellStyle(centerCellStyle);
                }

                // Время выполнения (если есть)
                Cell durationCell = row.createCell(3);
                durationCell.setCellValue(result.getDuration());
                durationCell.setCellStyle(centerCellStyle);
            }

            sheet.setColumnWidth(0, 1100 + (Math.max(String.valueOf(caseNum).length() - 3, 0) * 420));
            sheet.autoSizeColumn(1);
            sheet.setColumnWidth(2, 3200);
            sheet.setColumnWidth(3, 7000);

            try (FileOutputStream outputStream = new FileOutputStream(outputExcelPath)) {
                workbook.write(outputStream);
            }

            workbook.close();
        } catch (Exception e) {
            log.error(getLangValue("allure.excel.error"));
            e.printStackTrace();
        }
    }
}
