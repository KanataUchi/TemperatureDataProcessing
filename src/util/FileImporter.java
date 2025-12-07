package util;

import model.DataPoint;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileImporter {

    public static class ImportResult {
        public List<DataPoint> experimentalData;
        public List<DataPoint> interpolationData;
        public String errorMessage;

        public ImportResult() {
            experimentalData = new ArrayList<>();
            interpolationData = new ArrayList<>();
        }

        public boolean hasData() {
            return !experimentalData.isEmpty() || !interpolationData.isEmpty();
        }
    }

    public static ImportResult importFromExcel(JFrame parentFrame) {
        // Создаем диалог выбора файла
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Загрузить данные из Excel файла");

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

        int userSelection = fileChooser.showOpenDialog(parentFrame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();

            try {
                System.out.println("=== ИМПОРТ ДАННЫХ ===");
                System.out.println("Файл: " + fileToLoad.getName());

                ImportResult result = loadSimpleTable(fileToLoad);

                if (!result.hasData()) {
                    JOptionPane.showMessageDialog(parentFrame,
                            "В файле не найдены данные в нужном формате.\n" +
                                    "Файл должен содержать лист 'Все точки' с таблицей:\n" +
                                    "1. Тип точки\n2. Время (час)\n3. Температура (°C)\n\n" +
                                    "Первая строка может содержать уравнение.",
                            "Ошибка загрузки",
                            JOptionPane.WARNING_MESSAGE);
                    return null;
                }

                return result;

            } catch (IOException e) {
                JOptionPane.showMessageDialog(parentFrame,
                        "Ошибка при чтении Excel файла:\n" + e.getMessage(),
                        "Ошибка загрузки",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(parentFrame,
                        "Ошибка обработки данных:\n" + e.getMessage(),
                        "Ошибка загрузки",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }

        return null;
    }

    private static ImportResult loadSimpleTable(File file) throws IOException {
        ImportResult result = new ImportResult();

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = createWorkbook(file, fis)) {

            // ИЩЕМ ЛИСТ "ВСЕ ТОЧКИ"
            Sheet allPointsSheet = workbook.getSheet("Все точки");
            if (allPointsSheet == null) {
                result.errorMessage = "В файле отсутствует лист 'Все точки'";
                return result;
            }

            System.out.println("Найден лист 'Все точки'");
            System.out.println("Всего строк: " + (allPointsSheet.getLastRowNum() + 1));

            // ИЩЕМ СТРОКУ С ЗАГОЛОВКАМИ ТАБЛИЦЫ
            // В новом формате первая строка может быть с уравнением
            int headerRowIndex = findHeaderRow(allPointsSheet);
            if (headerRowIndex == -1) {
                result.errorMessage = "Не найдена строка с заголовками таблицы";
                return result;
            }

            System.out.println("Заголовки найдены в строке: " + headerRowIndex);

            // ОПРЕДЕЛЯЕМ КОЛОНКИ
            int typeCol = -1, timeCol = -1, tempCol = -1;
            Row headerRow = allPointsSheet.getRow(headerRowIndex);

            for (int col = 0; col < headerRow.getLastCellNum(); col++) {
                Cell cell = headerRow.getCell(col);
                if (cell != null) {
                    String value = getCellValueAsString(cell).toLowerCase().trim();

                    if (value.contains("тип")) {
                        typeCol = col;
                        System.out.println("Колонка 'Тип': " + col);
                    } else if (value.contains("время") || value.contains("час")) {
                        timeCol = col;
                        System.out.println("Колонка 'Время': " + col);
                    } else if (value.contains("температура") || value.contains("°c")) {
                        tempCol = col;
                        System.out.println("Колонка 'Температура': " + col);
                    }
                }
            }

            // ПРОВЕРЯЕМ ЧТО НАШЛИ ВСЕ КОЛОНКИ
            if (typeCol == -1 || timeCol == -1 || tempCol == -1) {
                System.out.println("Не все колонки найдены: тип=" + typeCol +
                        ", время=" + timeCol + ", темп=" + tempCol);
                result.errorMessage = "В таблице не найдены все необходимые колонки";
                return result;
            }

            // ЧИТАЕМ ДАННЫЕ НАЧИНАЯ СО СЛЕДУЮЩЕЙ СТРОКИ ПОСЛЕ ЗАГОЛОВКОВ
            int dataStartRow = headerRowIndex + 1;
            System.out.println("Чтение данных начиная со строки: " + dataStartRow);

            for (int row = dataStartRow; row <= allPointsSheet.getLastRowNum(); row++) {
                Row dataRow = allPointsSheet.getRow(row);
                if (dataRow == null || isRowEmpty(dataRow)) {
                    continue;
                }

                Cell typeCell = dataRow.getCell(typeCol);
                Cell timeCell = dataRow.getCell(timeCol);
                Cell tempCell = dataRow.getCell(tempCol);

                if (typeCell == null || timeCell == null || tempCell == null) {
                    continue;
                }

                try {
                    String type = getCellValueAsString(typeCell).toLowerCase().trim();
                    double time = getNumericValue(timeCell);
                    double temperature = getNumericValue(tempCell);

                    // Пропускаем некорректные значения
                    if (time < 0 || time > 24 || temperature < -100 || temperature > 100) {
                        System.out.println("Пропускаем строку " + row + ": некорректные данные");
                        continue;
                    }

                    // ДОБАВЛЯЕМ ВСЕ ТОЧКИ С ТЕМПЕРАТУРОЙ
                    if (type.contains("эксперимент") || type.contains("исход")) {
                        result.experimentalData.add(new DataPoint(time, temperature));
                        System.out.println("Экспериментальная: " + time + " час, " + temperature + "°C");
                    }
                    else if (type.contains("интерполяция") || type.contains("расчет")) {
                        // СОХРАНЯЕМ ТЕМПЕРАТУРУ ИЗ ФАЙЛА, а не пересчитываем
                        result.interpolationData.add(new DataPoint(time, temperature));
                        System.out.println("Интерполяция (импорт): " + time + " час, " + temperature + "°C");
                    }

                } catch (Exception e) {
                    System.out.println("Ошибка в строке " + row + ": " + e.getMessage());
                }
            }

            System.out.println("\nИтого:");
            System.out.println("Экспериментальных точек: " + result.experimentalData.size());
            System.out.println("Интерполяционных точек: " + result.interpolationData.size());

        } catch (Exception e) {
            result.errorMessage = "Ошибка: " + e.getMessage();
            e.printStackTrace();
        }

        return result;
    }

    // НАХОДИМ СТРОКУ С ЗАГОЛОВКАМИ (игнорируем строку с уравнением)
    private static int findHeaderRow(Sheet sheet) {
        // Ищем строку, которая содержит все три заголовка
        for (int row = 0; row <= Math.min(10, sheet.getLastRowNum()); row++) {
            Row currentRow = sheet.getRow(row);
            if (currentRow == null) continue;

            boolean hasType = false, hasTime = false, hasTemp = false;

            for (int col = 0; col < currentRow.getLastCellNum(); col++) {
                Cell cell = currentRow.getCell(col);
                if (cell != null) {
                    String value = getCellValueAsString(cell).toLowerCase().trim();

                    if (value.contains("тип")) hasType = true;
                    if (value.contains("время") || value.contains("час")) hasTime = true;
                    if (value.contains("температура") || value.contains("°c")) hasTemp = true;
                }
            }

            // Если нашли все три заголовка в одной строке - это наша строка
            if (hasType && hasTime && hasTemp) {
                return row;
            }
        }

        return -1; // Не нашли
    }

    // ПРОВЕРКА ПУСТОТЫ СТРОКИ
    private static boolean isRowEmpty(Row row) {
        if (row == null) return true;

        for (int col = 0; col < row.getLastCellNum(); col++) {
            Cell cell = row.getCell(col);
            if (cell != null) {
                String value = getCellValueAsString(cell).trim();
                if (!value.isEmpty()) {
                    return false;
                }
            }
        }

        return true;
    }

    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
    private static double getNumericValue(Cell cell) {
        if (cell == null) throw new IllegalArgumentException("Ячейка null");

        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return cell.getNumericCellValue();
            } else if (cell.getCellType() == CellType.STRING) {
                String str = cell.getStringCellValue().trim();
                if (str.isEmpty()) {
                    throw new IllegalArgumentException("Пустая строка");
                }
                str = str.replace(',', '.');
                // Удаляем все кроме цифр, точки и минуса
                str = str.replaceAll("[^0-9.-]", "");
                if (str.isEmpty()) {
                    throw new IllegalArgumentException("Нечисловое значение: " + cell.getStringCellValue());
                }
                return Double.parseDouble(str);
            } else if (cell.getCellType() == CellType.FORMULA) {
                return cell.getNumericCellValue();
            } else {
                throw new IllegalArgumentException("Нечисловая ячейка: " + cell.getCellType());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Ошибка преобразования: " + e.getMessage());
        }
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";

        try {
            if (cell.getCellType() == CellType.STRING) {
                return cell.getStringCellValue();
            } else if (cell.getCellType() == CellType.NUMERIC) {
                double num = cell.getNumericCellValue();
                // Убираем лишние нули
                if (num == Math.floor(num)) {
                    return String.format("%.0f", num);
                } else {
                    return String.format("%.2f", num);
                }
            } else if (cell.getCellType() == CellType.BOOLEAN) {
                return cell.getBooleanCellValue() ? "true" : "false";
            } else {
                return cell.toString();
            }
        } catch (Exception e) {
            return "";
        }
    }

    private static Workbook createWorkbook(File file, FileInputStream fis) throws IOException {
        String fileName = file.getName().toLowerCase();

        if (fileName.endsWith(".xlsx")) {
            return new XSSFWorkbook(fis);
        } else if (fileName.endsWith(".xls")) {
            return new HSSFWorkbook(fis);
        } else {
            throw new IOException("Неподдерживаемый формат: " + fileName);
        }
    }
}