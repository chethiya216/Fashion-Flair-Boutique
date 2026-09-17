/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package fashionflairboutique.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
/**
 *
 * @author Chethiya
 */
public class ReportExporterExcel {
    public static void exportToExcel(JTable table, File destinationFile) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Sales Report");
        TableModel model = table.getModel();

        // 1. Create Header Row with styling
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);

        for (int col = 0; col < model.getColumnCount(); col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(model.getColumnName(col));
            cell.setCellStyle(headerStyle);
        }

        // 2. Populate Data Rows
        for (int row = 0; row < model.getRowCount(); row++) {
            Row excelRow = sheet.createRow(row + 1);
            for (int col = 0; col < model.getColumnCount(); col++) {
                Cell cell = excelRow.createCell(col);
                Object val = model.getValueAt(row, col);
                if (val != null) {
                    if (val instanceof Number) {
                        cell.setCellValue(((Number) val).doubleValue());
                    } else {
                        cell.setCellValue(val.toString());
                    }
                }
            }
        }

        // Auto-size columns for neat formatting
        for (int col = 0; col < model.getColumnCount(); col++) {
            sheet.autoSizeColumn(col);
        }

        // 3. Write output to file
        try (FileOutputStream fileOut = new FileOutputStream(destinationFile)) {
            workbook.write(fileOut);
        }
        workbook.close();
    }
}
