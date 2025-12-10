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

/**
 * Класс для импорта данных из Excel файлов.
 * Поддерживает форматы .xlsx и .xls. Ищет данные в формате,
 * соответствующем экспорту из FileExporter.
 *
 * @author Petrushchenko A.A.
 * @version 1.0
 */
public class FileImporter {

    /**
     * Результат импорта данных из Excel файла.
     * Содержит отдельные списки для экспериментальных и интерполяционных данных.
     */
    public static class ImportResult {
        /**
         * Список экспериментальных точек данных.
         */
        public List<DataPoint> experimentalData;

        /**
         * Список интерполяционных точек данных.
         */
        public List<DataPoint> interpolationData;

        /**
         * Сообщение об ошибке, если импорт не удался.
         */
        public String errorMessage;

        /**
         * Создает новый пустой результат импорта.
         */
        public ImportResult() {
            experimentalData = new ArrayList<>();
            interpolationData = new ArrayList<>();
        }

        /**
         * Проверяет, содержит ли результат какие-либо данные.
         *
         * @return true если есть хотя бы один тип данных, false если оба списка пусты
         */
        public boolean hasData() {
            return !experimentalData.isEmpty() || !interpolationData.isEmpty();
        }
    }

    /**
     * Импортирует данные из Excel файла через диалоговое окно выбора файла.
     * Поддерживает форматы .xlsx и .xls.
     *
     * @param parentFrame родительское окно для диалогов
     * @return результат импорта или null если пользователь отменил операцию
     */
    public static ImportResult importFromExcel(JFrame parentFrame) {
        // Создаем диалоговое окно выбора файла
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

                // Загружаем данные из файла
                ImportResult result = loadSimpleTable(fileToLoad);

                if (!result.hasData()) {
                    // Показываем предупреждение если данных не найдено
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
                // Обрабатываем ошибки чтения файла
                JOptionPane.showMessageDialog(parentFrame,
                        "Ошибка при чтении Excel файла:\n" + e.getMessage(),
                        "Ошибка загрузки",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } catch (Exception e) {
                // Обрабатываем другие ошибки
                JOptionPane.showMessageDialog(parentFrame,
                        "Ошибка обработки данных:\n" + e.getMessage(),
                        "Ошибка загрузки",
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }

        return null; // Пользователь отменил операцию
    }

    /**
     * Загружает данные из Excel файла в простом табличном формате.
     * Ищет лист "Все точки" с определенной структурой.
     *
     * @param file файл Excel для загрузки
     * @return результат импорта с данными
     * @throws IOException если возникает ошибка чтения файла
     */
    private static ImportResult loadSimpleTable(File file) throws IOException {
        ImportResult result = new ImportResult();

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = createWorkbook(file, fis)) {

            // Ищем лист с названием "Все точки"
            Sheet allPointsSheet = workbook.getSheet("Все точки");
            if (allPointsSheet == null) {
                result.errorMessage = "В файле отсутствует лист 'Все точки'";
                return result;
            }

            System.out.println("Найден лист 'Все точки'");
            System.out.println("Всего строк: " + (allPointsSheet.getLastRowNum() + 1));

            // Находим строку с заголовками таблицы
            int headerRowIndex = findHeaderRow(allPointsSheet);
            if (headerRowIndex == -1) {
                result.errorMessage = "Не найдена строка с заголовками таблицы";
                return result;
            }

            System.out.println("Заголовки найдены в строке: " + headerRowIndex);

            // Определяем индексы колонок
            Row headerRow = allPointsSheet.getRow(headerRowIndex);
            int typeCol = findColumnIndex(headerRow, "тип");
            int timeCol = findColumnIndex(headerRow, "время", "час");
            int tempCol = findColumnIndex(headerRow, "температура", "°c");

            // Проверяем что нашли все необходимые колонки
            if (typeCol == -1 || timeCol == -1 || tempCol == -1) {
                result.errorMessage = "В таблице не найдены все необходимые колонки";
                return result;
            }

            System.out.println("Колонки: тип=" + typeCol + ", время=" + timeCol + ", темп=" + tempCol);

            // Читаем данные начиная со следующей строки после заголовков
            int dataStartRow = headerRowIndex + 1;
            System.out.println("Чтение данных начиная со строки: " + dataStartRow);

            for (int row = dataStartRow; row <= allPointsSheet.getLastRowNum(); row++) {
                Row dataRow = allPointsSheet.getRow(row);
                if (dataRow == null || isRowEmpty(dataRow)) {
                    continue; // Пропускаем пустые строки
                }

                Cell typeCell = dataRow.getCell(typeCol);
                Cell timeCell = dataRow.getCell(timeCol);
                Cell tempCell = dataRow.getCell(tempCol);

                if (typeCell == null || timeCell == null || tempCell == null) {
                    continue; // Пропускаем строки с отсутствующими данными
                }

                try {
                    String type = getCellValueAsString(typeCell).toLowerCase().trim();
                    double time = getNumericValue(timeCell);
                    double temperature = getNumericValue(tempCell);

                    // Проверяем корректность значений
                    if (time < 0 || time > 24 || temperature < -100 || temperature > 100) {
                        System.out.println("Пропускаем строку " + row + ": некорректные данные");
                        continue;
                    }

                    // Распределяем точки по типам
                    if (type.contains("эксперимент") || type.contains("исход")) {
                        result.experimentalData.add(new DataPoint(time, temperature));
                        System.out.println("Экспериментальная: " + time + " час, " + temperature + "°C");
                    }
                    else if (type.contains("интерполяция") || type.contains("расчет")) {
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

    /**
     * Находит индекс колонки по ключевым словам в заголовке.
     *
     * @param headerRow строка с заголовками
     * @param keywords ключевые слова для поиска
     * @return индекс колонки или -1 если не найдено
     */
    private static int findColumnIndex(Row headerRow, String... keywords) {
        for (int col = 0; col < headerRow.getLastCellNum(); col++) {
            Cell cell = headerRow.getCell(col);
            if (cell != null) {
                String value = getCellValueAsString(cell).toLowerCase().trim();
                for (String keyword : keywords) {
                    if (value.contains(keyword)) {
                        return col;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * Находит строку с заголовками таблицы.
     * Игнорирует возможную строку с уравнением в начале.
     *
     * @param sheet лист Excel
     * @return индекс строки с заголовками или -1 если не найдено
     */
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

    /**
     * Проверяет, является ли строка пустой.
     *
     * @param row строка Excel
     * @return true если строка пустая, false если содержит данные
     */
    private static boolean isRowEmpty(Row row) {
        if (row == null) return true;

        for (int col = 0; col < row.getLastCellNum(); col++) {
            Cell cell = row.getCell(col);
            if (cell != null) {
                String value = getCellValueAsString(cell).trim();
                if (!value.isEmpty()) {
                    return false; // Нашли непустую ячейку
                }
            }
        }

        return true; // Все ячейки пустые
    }

    /**
     * Получает числовое значение из ячейки Excel.
     * Поддерживает различные типы ячеек: числовые, строковые, формулы.
     *
     * @param cell ячейка Excel
     * @return числовое значение
     * @throws IllegalArgumentException если ячейка null или содержит нечисловые данные
     */
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
                str = str.replace(',', '.'); // Заменяем запятую на точку
                str = str.replaceAll("[^0-9.-]", ""); // Удаляем нечисловые символы
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

    /**
     * Получает строковое значение из ячейки Excel.
     * Конвертирует различные типы ячеек в строку.
     *
     * @param cell ячейка Excel
     * @return строковое представление значения ячейки
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) return "";

        try {
            if (cell.getCellType() == CellType.STRING) {
                return cell.getStringCellValue();
            } else if (cell.getCellType() == CellType.NUMERIC) {
                double num = cell.getNumericCellValue();
                // Форматируем число, убирая лишние нули
                if (num == Math.floor(num)) {
                    return String.format("%.0f", num); // Целое число
                } else {
                    return String.format("%.2f", num); // Дробное число с двумя знаками
                }
            } else if (cell.getCellType() == CellType.BOOLEAN) {
                return cell.getBooleanCellValue() ? "true" : "false";
            } else {
                return cell.toString();
            }
        } catch (Exception e) {
            return ""; // Возвращаем пустую строку при ошибке
        }
    }

    /**
     * Создает объект Workbook в зависимости от формата файла.
     *
     * @param file файл Excel
     * @param fis поток ввода файла
     * @return объект Workbook для работы с Excel
     * @throws IOException если формат файла не поддерживается
     */
    private static Workbook createWorkbook(File file, FileInputStream fis) throws IOException {
        String fileName = file.getName().toLowerCase();

        if (fileName.endsWith(".xlsx")) {
            return new XSSFWorkbook(fis); // Excel 2007+
        } else if (fileName.endsWith(".xls")) {
            return new HSSFWorkbook(fis); // Excel 97-2003
        } else {
            throw new IOException("Неподдерживаемый формат: " + fileName);
        }
    }
}