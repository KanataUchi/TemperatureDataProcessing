package util;

import model.DataPoint;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FileExporter {

    public static void exportToExcel(List<DataPoint> experimentalData,
                                     List<DataPoint> interpolatedData,  // УЖЕ содержит рассчитанные температуры!
                                     List<DataPoint> userData,
                                     double a, double b,
                                     JFrame parentFrame) {

        // Создаем диалог выбора файла
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Сохранить данные в Excel");
        fileChooser.setSelectedFile(new File("данные_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx"));

        // Устанавливаем фильтр для Excel файлов
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() ||
                        f.getName().toLowerCase().endsWith(".xlsx") ||
                        f.getName().toLowerCase().endsWith(".xls");
            }

            @Override
            public String getDescription() {
                return "Excel файлы (*.xlsx, *.xls)";
            }
        });

        int userSelection = fileChooser.showSaveDialog(parentFrame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // Добавляем расширение .xlsx если его нет
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".xlsx") && !filePath.toLowerCase().endsWith(".xls")) {
                fileToSave = new File(filePath + ".xlsx");
            }

            try (Workbook workbook = new XSSFWorkbook()) {
                // Стили для ячеек
                CellStyle headerStyle = createHeaderStyle(workbook);
                CellStyle dataStyle = createDataStyle(workbook);
                CellStyle infoStyle = createInfoStyle(workbook);

                // СОЗДАЕМ ТОЛЬКО ОДИН ЛИСТ "Все точки"
                Sheet allPointsSheet = workbook.createSheet("Все точки");
                createSimpleTable(allPointsSheet, experimentalData,
                        interpolatedData, userData, a, b, headerStyle, dataStyle, infoStyle);

                // Сохраняем файл
                try (FileOutputStream fileOut = new FileOutputStream(fileToSave)) {
                    workbook.write(fileOut);
                }

                JOptionPane.showMessageDialog(parentFrame,
                        "Данные успешно сохранены в Excel файл:\n" + fileToSave.getAbsolutePath(),
                        "Экспорт завершен",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                JOptionPane.showMessageDialog(parentFrame,
                        "Ошибка при сохранении Excel файла:\n" + e.getMessage(),
                        "Ошибка экспорта",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ПРОСТАЯ ТАБЛИЦА С СОХРАНЕНИЕМ ТЕМПЕРАТУР
    private static void createSimpleTable(Sheet sheet,
                                          List<DataPoint> experimentalData,
                                          List<DataPoint> interpolatedData,  // УЖЕ содержит температуры!
                                          List<DataPoint> userData,
                                          double a, double b,
                                          CellStyle headerStyle, CellStyle dataStyle, CellStyle infoStyle) {

        int rowIndex = 0;

        // ИНФОРМАЦИЯ ОБ УРАВНЕНИИ (опционально)
        Row infoRow = sheet.createRow(rowIndex++);
        infoRow.createCell(0).setCellValue("Уравнение: T = " +
                String.format("%.4f", a) + " * t + " + String.format("%.4f", b));
        infoRow.getCell(0).setCellStyle(infoStyle);

        // ПУСТАЯ СТРОКА
        rowIndex++;

        // ЗАГОЛОВКИ ТАБЛИЦЫ
        Row headerRow = sheet.createRow(rowIndex++);
        String[] headers = {"Тип точки", "Время (час)", "Температура (°C)"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 1. ЭКСПЕРИМЕНТАЛЬНЫЕ ТОЧКИ
        for (DataPoint point : experimentalData) {
            Row dataRow = sheet.createRow(rowIndex++);
            dataRow.createCell(0).setCellValue("Экспериментальная");
            dataRow.createCell(1).setCellValue(point.getTime());
            dataRow.createCell(2).setCellValue(point.getTemperature());

            for (int i = 0; i < 3; i++) {
                dataRow.getCell(i).setCellStyle(dataStyle);
            }
        }

        // 2. ИНТЕРПОЛЯЦИОННЫЕ ТОЧКИ (С СОХРАНЕННОЙ ТЕМПЕРАТУРОЙ!)
        for (DataPoint point : interpolatedData) {
            Row dataRow = sheet.createRow(rowIndex++);
            dataRow.createCell(0).setCellValue("Интерполяция");
            dataRow.createCell(1).setCellValue(point.getTime());
            dataRow.createCell(2).setCellValue(point.getTemperature());  // СОХРАНЯЕМ ТЕМПЕРАТУРУ!

            for (int i = 0; i < 3; i++) {
                dataRow.getCell(i).setCellStyle(dataStyle);
            }
        }

        // 3. ПОЛЬЗОВАТЕЛЬСКИЕ ТОЧКИ
        for (DataPoint point : userData) {
            Row dataRow = sheet.createRow(rowIndex++);
            dataRow.createCell(0).setCellValue("Пользовательская");
            dataRow.createCell(1).setCellValue(point.getTime());
            dataRow.createCell(2).setCellValue(point.getTemperature());

            for (int i = 0; i < 3; i++) {
                dataRow.getCell(i).setCellStyle(dataStyle);
            }
        }

        // АВТОНАСТРОЙКА ШИРИНЫ КОЛОНОК
        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // ПРОСТЫЕ СТИЛИ
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private static CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private static CellStyle createInfoStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setItalic(true);
        style.setFont(font);
        return style;
    }
}