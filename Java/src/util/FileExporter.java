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

/**
 * Класс для экспорта данных в формат Excel.
 * Использует библиотеку Apache POI для работы с Excel файлами.
 * Экспортирует экспериментальные, интерполяционные и пользовательские точки.
 *
 * @author Petrushchenko A.A.
 * @version 1.0
 */
public class FileExporter {

    /**
     * Экспортирует данные в Excel файл.
     * Создает файл с одним листом "Все точки", содержащим все типы данных.
     *
     * @param experimentalData список экспериментальных точек
     * @param interpolatedData список интерполяционных точек (уже с рассчитанной температурой)
     * @param a коэффициент наклона прямой
     * @param b коэффициент смещения прямой
     * @param parentFrame родительское окно для диалогов
     */
    public static void exportToExcel(List<DataPoint> experimentalData,
                                     List<DataPoint> interpolatedData,
                                     double a, double b,
                                     JFrame parentFrame) {

        // Создаем диалоговое окно выбора файла
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Сохранить данные в Excel");

        // Предлагаем имя файла по умолчанию с текущей датой и временем
        String defaultFileName = "данные_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xlsx";
        fileChooser.setSelectedFile(new File(defaultFileName));

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
            if (!filePath.toLowerCase().endsWith(".xlsx") &&
                    !filePath.toLowerCase().endsWith(".xls")) {
                fileToSave = new File(filePath + ".xlsx");
            }

            try (Workbook workbook = new XSSFWorkbook()) {
                // Создаем стили для ячеек
                CellStyle headerStyle = createHeaderStyle(workbook);
                CellStyle dataStyle = createDataStyle(workbook);
                CellStyle infoStyle = createInfoStyle(workbook);

                // Создаем лист "Все точки" и заполняем его данными
                Sheet allPointsSheet = workbook.createSheet("Все точки");
                createSimpleTable(allPointsSheet, experimentalData,
                        interpolatedData, a, b,
                        headerStyle, dataStyle, infoStyle);

                // Сохраняем файл на диск
                try (FileOutputStream fileOut = new FileOutputStream(fileToSave)) {
                    workbook.write(fileOut);
                }

                // Показываем сообщение об успешном экспорте
                JOptionPane.showMessageDialog(parentFrame,
                        "Данные успешно сохранены в Excel файл:\n" + fileToSave.getAbsolutePath(),
                        "Экспорт завершен",
                        JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException e) {
                // Обрабатываем ошибки ввода-вывода
                JOptionPane.showMessageDialog(parentFrame,
                        "Ошибка при сохранении Excel файла:\n" + e.getMessage(),
                        "Ошибка экспорта",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Создает простую таблицу на листе Excel.
     * Таблица содержит все типы точек: экспериментальные, интерполяционные и пользовательские.
     *
     * @param sheet лист Excel для заполнения
     * @param experimentalData список экспериментальных точек
     * @param interpolatedData список интерполяционных точек
     * @param a коэффициент наклона прямой
     * @param b коэффициент смещения прямой
     * @param headerStyle стиль для заголовков таблицы
     * @param dataStyle стиль для данных таблицы
     * @param infoStyle стиль для информационной строки
     */
    private static void createSimpleTable(Sheet sheet,
                                          List<DataPoint> experimentalData,
                                          List<DataPoint> interpolatedData,
                                          double a, double b,
                                          CellStyle headerStyle,
                                          CellStyle dataStyle,
                                          CellStyle infoStyle) {

        int rowIndex = 0;

        // Строка с уравнением регрессии
        Row infoRow = sheet.createRow(rowIndex++);
        infoRow.createCell(0).setCellValue("Уравнение: T = " +
                String.format("%.4f", a) + " * t + " + String.format("%.4f", b));
        infoRow.getCell(0).setCellStyle(infoStyle);

        // Пустая строка для разделения
        rowIndex++;

        // Заголовки таблицы
        Row headerRow = sheet.createRow(rowIndex++);
        String[] headers = {"Тип точки", "Время (час)", "Температура (°C)"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Экспериментальные точки
        for (DataPoint point : experimentalData) {
            Row dataRow = sheet.createRow(rowIndex++);
            dataRow.createCell(0).setCellValue("Экспериментальная");
            dataRow.createCell(1).setCellValue(point.getTime());
            dataRow.createCell(2).setCellValue(point.getTemperature());

            for (int i = 0; i < 3; i++) {
                dataRow.getCell(i).setCellStyle(dataStyle);
            }
        }

        // Интерполяционные точки (сохраняем рассчитанную температуру)
        for (DataPoint point : interpolatedData) {
            Row dataRow = sheet.createRow(rowIndex++);
            dataRow.createCell(0).setCellValue("Интерполяция");
            dataRow.createCell(1).setCellValue(point.getTime());
            dataRow.createCell(2).setCellValue(point.getTemperature());

            for (int i = 0; i < 3; i++) {
                dataRow.getCell(i).setCellStyle(dataStyle);
            }
        }

        // Автоматически настраиваем ширину колонок
        for (int i = 0; i < 3; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Создает стиль для заголовков таблицы Excel.
     * Заголовки выделены жирным шрифтом и имеют границы.
     *
     * @param workbook книга Excel
     * @return стиль для заголовков
     */
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true); // Жирный шрифт для заголовков
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    /**
     * Создает стиль для данных таблицы Excel.
     * Данные центрированы и имеют тонкие границы.
     *
     * @param workbook книга Excel
     * @return стиль для данных
     */
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

    /**
     * Создает стиль для информационной строки Excel.
     * Информация отображается курсивным шрифтом.
     *
     * @param workbook книга Excel
     * @return стиль для информационной строки
     */
    private static CellStyle createInfoStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setItalic(true); // Курсивный шрифт для информации
        style.setFont(font);
        return style;
    }
}