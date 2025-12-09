package ui;

import model.DataPoint;
import logic.LeastSquaresCalculator;
import util.FileExporter;
import util.FileImporter;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Главное окно приложения для обработки экспериментальных данных.
 * Предоставляет интерфейс для работы с экспериментальными данными,
 * выполнения линейной интерполяции методом наименьших квадратов
 * и визуализации результатов на графике.
 *
 * Основные функции:
 * 1. Ввод и редактирование экспериментальных данных
 * 2. Расчет коэффициентов линейной регрессии
 * 3. Интерполяция температур в заданные моменты времени
 * 4. Визуализация данных на графике
 * 5. Импорт/экспорт данных в формате Excel
 *
 * @author Петрущенко Александр Андреевич
 * @version 1.0
 * @see DataPoint
 * @see LeastSquaresCalculator
 * @see GraphFrame
 */
public class MainFrame extends JFrame {
    // Экспериментальные данные (время и температура)
    private List<DataPoint> experimentalData;

    // Времена для интерполяции
    private List<Double> interpolationTimes;

    // Коэффициенты линейного уравнения T = a*t + b
    private double a, b;

    // Окно графика
    private GraphFrame graphFrame;

    // Компоненты GUI
    private JTable experimentalTable;
    private JTable interpolationTable;
    private DefaultTableModel experimentalModel;
    private DefaultTableModel interpolationModel;
    private JLabel equationLabel;
    private JLabel statusLabel;

    /**
     * Конструктор главного окна приложения.
     * Инициализирует данные, вычисляет коэффициенты линейной регрессии
     * и настраивает пользовательский интерфейс.
     */
    public MainFrame() {
        initializeData();
        calculateCoefficients();
        setupUI();
        updateInterpolationTemperatures(); // Инициализируем температуры
    }

    /**
     * Инициализирует начальные экспериментальные и интерполяционные данные.
     * Создает список экспериментальных точек и времен для интерполяции.
     */
    private void initializeData() {
        experimentalData = new ArrayList<>(Arrays.asList(
                new DataPoint(8.0, 7.0),
                new DataPoint(10.0, 10.0),
                new DataPoint(13.0, 15.0),
                new DataPoint(14.0, 16.0),
                new DataPoint(17.0, 18.0),
                new DataPoint(20.0, 17.0)
        ));

        interpolationTimes = new ArrayList<>(Arrays.asList(9.0, 12.5, 15.25));
    }

    /**
     * Вычисляет коэффициенты линейной регрессии a и b методом наименьших квадратов.
     * Использует экспериментальные данные для расчета уравнения T = a*t + b.
     * В случае ошибки устанавливает значения по умолчанию.
     */
    private void calculateCoefficients() {
        try {
            double[] coefficients = LeastSquaresCalculator.calculateCoefficients(experimentalData);
            a = coefficients[0]; // Коэффициент наклона
            b = coefficients[1]; // Коэффициент смещения
        } catch (Exception e) {
            /*
             * В случае ошибки вычисления коэффициентов
             * используем значения по умолчанию для
             * обеспечения работоспособности приложения
             */
            showErrorDialog("Ошибка вычислений", e.getMessage());
            a = 0.8904;
            b = 1.6644;
        }
    }

    /**
     * Настраивает пользовательский интерфейс главного окна.
     * Создает меню, панели с таблицами и кнопки управления.
     */
    private void setupUI() {
        setTitle("Обработка экспериментальных данных");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        setupMenuBar();
        setupMainPanel();

        setSize(900, 700);
        setLocationRelativeTo(null); // Центрируем окно на экране
    }

    /**
     * Создает и настраивает строку меню приложения.
     * Меню содержит три раздела: Файл, Редактирование и Справка.
     */
    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Меню "Файл" - операции с файлами
        JMenu fileMenu = new JMenu("Файл");

        JMenuItem importItem = new JMenuItem("Импорт данных");
        importItem.addActionListener(e -> importData());

        JMenuItem exportItem = new JMenuItem("Экспорт данных");
        exportItem.addActionListener(e -> exportData());

        JMenuItem exitItem = new JMenuItem("Выход");
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(importItem);
        fileMenu.add(exportItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Меню "Редактирование" - операции с данными
        JMenu editMenu = new JMenu("Редактирование");

        JMenuItem clearAllItem = new JMenuItem("Очистить все данные");
        clearAllItem.addActionListener(e -> clearAllDataWithConfirmation());

        editMenu.add(clearAllItem);

        // Меню "Справка" - информация о программе и авторе
        JMenu helpMenu = new JMenu("Справка");

        JMenuItem aboutProgramItem = new JMenuItem("О программе");
        aboutProgramItem.addActionListener(e -> AboutProgramDialog.showDialog(this));

        JMenuItem aboutAuthorItem = new JMenuItem("Об авторе");
        aboutAuthorItem.addActionListener(e -> AboutAuthorDialog.showDialog(this));

        helpMenu.add(aboutProgramItem);
        helpMenu.add(aboutAuthorItem);

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    /**
     * Создает и настраивает основную панель интерфейса.
     * Панель содержит:
     * 1. Информационную панель (уравнение и статус)
     * 2. Центральную панель с таблицами данных
     * 3. Нижнюю панель с кнопками управления
     */
    private void setupMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Верхняя панель с информацией
        JPanel infoPanel = createInfoPanel();
        mainPanel.add(infoPanel, BorderLayout.NORTH);

        // Центральная панель с таблицами
        JPanel centerPanel = createCenterPanel();
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Нижняя панель с кнопками
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * Создает информационную панель.
     * Отображает уравнение регрессии и текущий статус работы.
     *
     * @return JPanel информационная панель
     */
    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        equationLabel = new JLabel("Уравнение: T = " + String.format("%.4f", a) + " * t + " + String.format("%.4f", b));
        equationLabel.setFont(new Font("Arial", Font.BOLD, 14));
        equationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        statusLabel = new JLabel("Готово. Всего экспериментальных точек: " + experimentalData.size() +
                ", интерполяционных: " + interpolationTimes.size());
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoPanel.add(equationLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(statusLabel);

        return infoPanel;
    }

    /**
     * Создает центральную панель с таблицами данных.
     * Панель разделена на две части:
     * 1. Экспериментальные данные
     * 2. Данные интерполяции
     *
     * @return JPanel центральная панель
     */
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        centerPanel.add(createExperimentalPanel());
        centerPanel.add(createInterpolationPanel());
        return centerPanel;
    }

    /**
     * Создает нижнюю панель с кнопками управления.
     *
     * @return JPanel нижняя панель
     */
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        JButton showGraphButton = new JButton("Показать график");
        showGraphButton.addActionListener(e -> showGraph());

        JButton clearAllButton = new JButton("Очистить все");
        clearAllButton.setForeground(Color.RED);
        clearAllButton.addActionListener(e -> clearAllDataWithConfirmation());

        controlPanel.add(showGraphButton);
        controlPanel.add(clearAllButton);

        bottomPanel.add(controlPanel, BorderLayout.CENTER);
        return bottomPanel;
    }

    /**
     * Создает панель для отображения и редактирования экспериментальных данных.
     * Таблица позволяет:
     * 1. Вводить время и температуру
     * 2. Добавлять и удалять строки
     * 3. Проверять корректность введенных данных
     *
     * @return JPanel с таблицей экспериментальных данных
     */
    private JPanel createExperimentalPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Экспериментальные данные"));

        // Создаем модель таблицы с кастомным поведением
        experimentalModel = createExperimentalTableModel();

        // Заполняем таблицу начальными данными с форматированием
        for (DataPoint point : experimentalData) {
            experimentalModel.addRow(new Object[]{
                    formatWithDecimal(point.getTime()),
                    formatWithDecimal(point.getTemperature())
            });
        }

        experimentalTable = new JTable(experimentalModel);
        configureExperimentalTable();

        JScrollPane scrollPane = new JScrollPane(experimentalTable);
        scrollPane.setPreferredSize(new Dimension(0, 300));

        JPanel buttonPanel = createExperimentalButtonPanel();
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Создает модель таблицы для экспериментальных данных.
     * Определяет:
     * 1. Столбцы таблицы
     * 2. Правила редактирования ячеек
     * 3. Форматирование значений
     *
     * @return DefaultTableModel модель таблицы
     */
    private DefaultTableModel createExperimentalTableModel() {
        String[] columns = {"Время (час)", "Температура (°C)"};

        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Все ячейки редактируемы
                return true;
            }

            @Override
            public void setValueAt(Object aValue, int row, int column) {
                try {
                    if (aValue != null) {
                        String strValue = aValue.toString().trim();
                        if (!strValue.isEmpty()) {
                            strValue = strValue.replace(',', '.');
                            double value = Double.parseDouble(strValue);

                            // Проверка допустимых диапазонов
                            if (column == 0) {
                                if (value < 0 || value > 24) {
                                    showErrorDialog("Ошибка ввода",
                                            "Время должно быть от 0 до 24 часов. Строка: " + (row + 1));
                                    return;
                                }
                            } else if (column == 1) {
                                if (value < -50 || value > 100) {
                                    showErrorDialog("Ошибка ввода",
                                            "Температура должна быть от -50 до 100°C. Строка: " + (row + 1));
                                    return;
                                }
                            }

                            // ФОРМАТИРУЕМ: целые числа показываем с .0
                            String formattedValue = formatWithDecimal(value);
                            super.setValueAt(formattedValue, row, column);
                        }
                    }
                } catch (NumberFormatException e) {
                    showErrorDialog("Ошибка ввода",
                            "Неверный формат числа. Строка: " + (row + 1) + ". Введите число.");
                }
            }

            // Метод для форматирования при получении значения
            @Override
            public Object getValueAt(int row, int column) {
                Object value = super.getValueAt(row, column);
                if (value != null && (value instanceof Double || value instanceof String)) {
                    try {
                        String strValue = value.toString().replace(',', '.');
                        double numValue = Double.parseDouble(strValue);
                        return formatWithDecimal(numValue);
                    } catch (NumberFormatException e) {
                        return value;
                    }
                }
                return value;
            }
        };
    }

    /**
     * Настраивает таблицу экспериментальных данных.
     * Устанавливает рендереры и редакторы ячеек.
     */
    private void configureExperimentalTable() {
        // Кастомный рендерер для форматирования значений
        experimentalTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);

                if (value != null) {
                    try {
                        String strValue = value.toString().replace(',', '.');
                        double numValue = Double.parseDouble(strValue);
                        setText(formatWithDecimal(numValue));
                    } catch (NumberFormatException e) {
                        setText(value.toString());
                    }
                }

                return c;
            }
        });

        // Кастомный редактор ячеек с валидацией
        experimentalTable.setDefaultEditor(Object.class, new DefaultCellEditor(new JTextField()) {
            @Override
            public boolean stopCellEditing() {
                try {
                    String text = ((JTextField) getComponent()).getText().trim();
                    if (!text.isEmpty()) {
                        text = text.replace(',', '.');
                        Double.parseDouble(text);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(experimentalTable,
                            "Неверный формат числа. Введите число.",
                            "Ошибка ввода",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                return super.stopCellEditing();
            }

            @Override
            public Object getCellEditorValue() {
                String value = super.getCellEditorValue().toString();
                if (value != null && !value.trim().isEmpty()) {
                    try {
                        String cleaned = value.replace(',', '.');
                        double num = Double.parseDouble(cleaned);
                        return formatWithDecimal(num);
                    } catch (NumberFormatException e) {
                        return value;
                    }
                }
                return value;
            }
        });
    }

    /**
     * Создает панель кнопок для работы с экспериментальными данными.
     *
     * @return JPanel панель кнопок
     */
    private JPanel createExperimentalButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        JButton addButton = new JButton("Добавить строку");
        addButton.addActionListener(e -> {
            experimentalModel.addRow(new Object[]{"", ""});
            updateStatus("Добавлена новая строка. Введите данные и нажмите 'Рассчитать'");
        });

        JButton deleteButton = new JButton("Удалить строку");
        deleteButton.addActionListener(e -> {
            int selectedRow = experimentalTable.getSelectedRow();
            if (selectedRow != -1) {
                experimentalModel.removeRow(selectedRow);
                updateStatus("Строка удалена. Нажмите 'Рассчитать' для обновления");
            } else {
                showErrorDialog("Ошибка", "Выберите строку для удаления");
            }
        });

        JButton calculateButton = new JButton("Рассчитать");
        calculateButton.addActionListener(e -> saveExperimentalData());

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(calculateButton);

        return buttonPanel;
    }

    /**
     * Создает панель для отображения и редактирования интерполяционных данных.
     * Таблица позволяет:
     * 1. Вводить время для интерполяции
     * 2. Автоматически рассчитывать температуру по уравнению
     * 3. Добавлять и удалять времена интерполяции
     *
     * @return JPanel с таблицей интерполяционных данных
     */
    private JPanel createInterpolationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Времена для интерполяции"));

        // Создаем модель таблицы
        interpolationModel = createInterpolationTableModel();

        // Заполняем таблицу начальными данными
        for (Double time : interpolationTimes) {
            double temp = a * time + b;
            interpolationModel.addRow(new Object[]{time, String.format(Locale.US, "%.2f", temp)});
        }

        interpolationTable = new JTable(interpolationModel);
        configureInterpolationTable();

        JScrollPane scrollPane = new JScrollPane(interpolationTable);
        scrollPane.setPreferredSize(new Dimension(0, 300));

        JPanel buttonPanel = createInterpolationButtonPanel();
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Создает модель таблицы для интерполяционных данных.
     *
     * @return DefaultTableModel модель таблицы
     */
    private DefaultTableModel createInterpolationTableModel() {
        String[] columns = {"Время (час)", "Температура (°C)"};

        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Только столбец времени редактируем
                return column == 0;
            }

            @Override
            public void setValueAt(Object aValue, int row, int column) {
                if (column == 0) {
                    try {
                        if (aValue != null) {
                            String strValue = aValue.toString().trim();
                            if (!strValue.isEmpty()) {
                                strValue = strValue.replace(',', '.');
                                double value = Double.parseDouble(strValue);

                                if (value < 0 || value > 24) {
                                    showErrorDialog("Ошибка ввода",
                                            "Время должно быть от 0 до 24 часов. Строка: " + (row + 1));
                                    return;
                                }

                                super.setValueAt(strValue, row, column);

                                // Обновляем температуру при изменении времени
                                double temp = a * value + b;
                                super.setValueAt(String.format(Locale.US, "%.2f", temp), row, 1);

                                // Обновляем список времен
                                if (row < interpolationTimes.size()) {
                                    interpolationTimes.set(row, value);
                                } else {
                                    interpolationTimes.add(value);
                                }
                            }
                        }
                    } catch (NumberFormatException e) {
                        showErrorDialog("Ошибка ввода",
                                "Неверный формат числа. Строка: " + (row + 1) + ". Введите число.");
                    }
                }
            }
        };
    }

    /**
     * Настраивает таблицу интерполяционных данных.
     */
    private void configureInterpolationTable() {
        interpolationTable.setDefaultEditor(Object.class, new DefaultCellEditor(new JTextField()) {
            @Override
            public boolean stopCellEditing() {
                try {
                    String text = ((JTextField) getComponent()).getText().trim();
                    if (!text.isEmpty()) {
                        text = text.replace(',', '.');
                        double value = Double.parseDouble(text);
                        if (value < 0 || value > 24) {
                            JOptionPane.showMessageDialog(interpolationTable,
                                    "Время должно быть от 0 до 24 часов",
                                    "Ошибка ввода",
                                    JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(interpolationTable,
                            "Неверный формат числа. Введите число.",
                            "Ошибка ввода",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                return super.stopCellEditing();
            }
        });
    }

    /**
     * Создает панель кнопок для работы с интерполяционными данными.
     *
     * @return JPanel панель кнопок
     */
    private JPanel createInterpolationButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

        JButton addButton = new JButton("Добавить время");
        addButton.addActionListener(e -> addInterpolationTime());

        JButton deleteButton = new JButton("Удалить время");
        deleteButton.addActionListener(e -> {
            int selectedRow = interpolationTable.getSelectedRow();
            if (selectedRow != -1) {
                interpolationModel.removeRow(selectedRow);
                updateInterpolationTimesFromTable();
                updateStatus("Время интерполяции удалено. Всего: " + interpolationTimes.size());

                if (graphFrame != null && graphFrame.isVisible()) {
                    graphFrame.updateGraph(experimentalData, a, b, interpolationTimes);
                }
            } else {
                showErrorDialog("Ошибка", "Выберите строку для удаления");
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        return buttonPanel;
    }

    /**
     * Сохраняет экспериментальные данные из таблицы в список.
     * Проверяет корректность введенных данных:
     * 1. Формат чисел
     * 2. Диапазоны значений
     * 3. Минимальное количество точек
     */
    private void saveExperimentalData() {
        List<DataPoint> newData = new ArrayList<>();
        boolean hasError = false;

        for (int i = 0; i < experimentalModel.getRowCount(); i++) {
            try {
                Object timeObj = experimentalModel.getValueAt(i, 0);
                Object tempObj = experimentalModel.getValueAt(i, 1);

                // Проверка на null
                if (timeObj == null || tempObj == null) {
                    showErrorDialog("Ошибка данных", "Строка " + (i + 1) + ": значения не могут быть пустыми");
                    hasError = true;
                    break;
                }

                String timeStr = timeObj.toString().trim();
                String tempStr = tempObj.toString().trim();

                // Проверка на пустые строки
                if (timeStr.isEmpty() || tempStr.isEmpty()) {
                    showErrorDialog("Ошибка данных", "Строка " + (i + 1) + ": значения не могут быть пустыми");
                    hasError = true;
                    break;
                }

                timeStr = timeStr.replace(',', '.');
                tempStr = tempStr.replace(',', '.');

                double time = Double.parseDouble(timeStr);
                double temperature = Double.parseDouble(tempStr);

                // Проверка диапазонов
                if (time < 0 || time > 24) {
                    showErrorDialog("Ошибка данных",
                            "Строка " + (i + 1) + ": время должно быть от 0 до 24 часов");
                    hasError = true;
                    break;
                }

                if (temperature < -50 || temperature > 100) {
                    showErrorDialog("Ошибка данных",
                            "Строка " + (i + 1) + ": температура должна быть от -50 до 100°C");
                    hasError = true;
                    break;
                }

                // Форматируем значения
                experimentalModel.setValueAt(formatWithDecimal(time), i, 0);
                experimentalModel.setValueAt(formatWithDecimal(temperature), i, 1);

                newData.add(new DataPoint(time, temperature));
            } catch (NumberFormatException e) {
                showErrorDialog("Ошибка данных", "Строка " + (i + 1) + ": неверный формат числа");
                hasError = true;
                break;
            }
        }

        if (hasError) {
            return;
        }

        // Проверка минимального количества точек
        if (newData.isEmpty()) {
            showErrorDialog("Ошибка", "Таблица не должна быть пустой");
            return;
        }

        if (newData.size() < 2) {
            showErrorDialog("Ошибка", "Для расчета нужно как минимум 2 точки данных");
            return;
        }

        // Сохраняем данные и пересчитываем коэффициенты
        experimentalData = newData;
        calculateCoefficients();
        updateEquationLabel();

        // Обновляем таблицу интерполяции с новыми коэффициентами
        updateInterpolationTemperatures();

        updateStatus("Экспериментальные данные сохранены. Всего точек: " + experimentalData.size());

        // Обновляем график если он открыт
        if (graphFrame != null && graphFrame.isVisible()) {
            graphFrame.updateGraph(experimentalData, a, b, interpolationTimes);
        }
    }

    /**
     * Метод для обновления температур интерполяции в таблице.
     * Вызывается после изменения коэффициентов уравнения.
     */
    private void updateInterpolationTemperatures() {
        // Очищаем таблицу
        interpolationModel.setRowCount(0);

        // Заполняем заново с новыми коэффициентами
        for (Double time : interpolationTimes) {
            double temp = a * time + b;
            interpolationModel.addRow(new Object[]{time, String.format(Locale.US, "%.2f", temp)});
        }

        // Если таблица пустая, добавляем стандартные значения
        if (interpolationTimes.isEmpty()) {
            interpolationTimes.addAll(Arrays.asList(9.0, 12.5, 15.25));
            for (Double time : interpolationTimes) {
                double temp = a * time + b;
                interpolationModel.addRow(new Object[]{time, String.format(Locale.US, "%.2f", temp)});
            }
        }
    }

    /**
     * Добавляет новое время для интерполяции через диалоговое окно.
     * Проверяет корректность ввода и добавляет время в таблицу.
     */
    private void addInterpolationTime() {
        String input = JOptionPane.showInputDialog(this,
                "Введите время для интерполяции (час, от 0 до 24):",
                "Добавление времени интерполяции",
                JOptionPane.QUESTION_MESSAGE);

        if (input != null && !input.trim().isEmpty()) {
            try {
                double time = Double.parseDouble(input.replace(',', '.'));

                if (time < 0 || time > 24) {
                    showErrorDialog("Ошибка ввода", "Время должно быть от 0 до 24 часов");
                    return;
                }

                double temp = a * time + b;
                interpolationTimes.add(time);
                interpolationModel.addRow(new Object[]{time, String.format(Locale.US, "%.2f", temp)});
                updateStatus("Добавлено время интерполяции: " + time + " час");

                if (graphFrame != null && graphFrame.isVisible()) {
                    graphFrame.updateGraph(experimentalData, a, b, interpolationTimes);
                }
            } catch (NumberFormatException e) {
                showErrorDialog("Ошибка ввода", "Неверный формат времени");
            }
        }
    }

    /**
     * Обновляет список времен интерполяции из данных таблицы.
     * Извлекает значения из первой колонки таблицы интерполяции.
     */
    private void updateInterpolationTimesFromTable() {
        interpolationTimes.clear();
        for (int i = 0; i < interpolationModel.getRowCount(); i++) {
            try {
                Object timeObj = interpolationModel.getValueAt(i, 0);
                if (timeObj != null && !timeObj.toString().trim().isEmpty()) {
                    String timeStr = timeObj.toString().replace(',', '.');
                    interpolationTimes.add(Double.parseDouble(timeStr));
                }
            } catch (Exception e) {
                // Игнорируем ошибки парсинга
                System.err.println("Ошибка при обновлении времени интерполяции: " + e.getMessage());
            }
        }
    }

    /**
     * Открывает окно с графиком данных.
     * Сохраняет экспериментальные данные перед отображением графика.
     * Если окно графика уже открыто, обновляет его данные.
     */
    private void showGraph() {
        saveExperimentalData();

        if (graphFrame == null || !graphFrame.isVisible()) {
            graphFrame = new GraphFrame(experimentalData, a, b, interpolationTimes, this);
            graphFrame.setVisible(true);
        } else {
            graphFrame.toFront();
            graphFrame.updateGraph(experimentalData, a, b, interpolationTimes);
        }
    }

    /**
     * Импортирует данные из Excel файла.
     * Поддерживает форматы .xlsx и .xls.
     * Позволяет загрузить как экспериментальные, так и интерполяционные данные.
     */
    private void importData() {
        FileImporter.ImportResult result = FileImporter.importFromExcel(this);

        if (result != null && result.hasData()) {
            // Проверка минимального количества экспериментальных точек
            if (result.experimentalData.size() < 2 && !result.experimentalData.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Найдено только " + result.experimentalData.size() + " экспериментальных точек.\n" +
                                "Для расчетов нужно минимум 2 точки.",
                        "Предупреждение",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Формируем сообщение о найденных данных
            StringBuilder message = new StringBuilder();
            message.append("В файле найдено:\n\n");

            if (!result.experimentalData.isEmpty()) {
                message.append("Экспериментальные точки: ").append(result.experimentalData.size()).append("\n");
                for (int i = 0; i < Math.min(3, result.experimentalData.size()); i++) {
                    DataPoint point = result.experimentalData.get(i);
                    message.append(String.format("  %.1f час → %.1f°C\n", point.getTime(), point.getTemperature()));
                }
                if (result.experimentalData.size() > 3) message.append("  ...\n");
                message.append("\n");
            }

            if (!result.interpolationData.isEmpty()) {
                message.append("Интерполяционные точки: ").append(result.interpolationData.size()).append("\n");
                for (int i = 0; i < Math.min(3, result.interpolationData.size()); i++) {
                    DataPoint point = result.interpolationData.get(i);
                    message.append(String.format("  %.1f час → %.1f°C\n", point.getTime(), point.getTemperature()));
                }
                if (result.interpolationData.size() > 3) message.append("  ...\n");
                message.append("\n");
            }

            message.append("Текущие данные будут полностью заменены.\nПродолжить?");

            // Запрос подтверждения у пользователя
            int confirm = JOptionPane.showConfirmDialog(this,
                    message.toString(),
                    "Подтверждение импорта",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                // Очищаем текущие данные
                experimentalData.clear();
                interpolationTimes.clear();

                experimentalModel.setRowCount(0);
                interpolationModel.setRowCount(0);

                boolean dataLoaded = false;

                // Загружаем экспериментальные данные
                if (!result.experimentalData.isEmpty()) {
                    experimentalData = new ArrayList<>(result.experimentalData);
                    updateExperimentalTable();
                    dataLoaded = true;
                }

                // Загружаем интерполяционные данные
                if (!result.interpolationData.isEmpty()) {
                    interpolationTimes.clear();
                    for (DataPoint point : result.interpolationData) {
                        interpolationTimes.add(point.getTime());
                    }
                    updateInterpolationTemperatures();
                    dataLoaded = true;
                }

                if (dataLoaded) {
                    calculateCoefficients();
                    updateEquationLabel();
                    updateInterpolationTemperatures();

                    // Формируем сообщение о результате импорта
                    StringBuilder status = new StringBuilder("Данные импортированы! ");
                    if (!experimentalData.isEmpty()) {
                        status.append("Эксп. точек: ").append(experimentalData.size()).append(". ");
                    }
                    if (!interpolationTimes.isEmpty()) {
                        status.append("Интерп. точек: ").append(interpolationTimes.size());
                    }
                    updateStatus(status.toString());

                    // Обновляем график если он открыт
                    if (graphFrame != null && graphFrame.isVisible()) {
                        graphFrame.updateGraph(experimentalData, a, b, interpolationTimes);
                    }

                    JOptionPane.showMessageDialog(this,
                            "✅ Импорт успешно завершен!\nВсе данные заменены.",
                            "Успех",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }

    /**
     * Метод для форматирования чисел.
     * Целые числа показываются с .0, дробные - с двумя знаками после запятой.
     *
     * @param value число для форматирования
     * @return отформатированная строка
     */
    private String formatWithDecimal(double value) {
        /*
         * Алгоритм форматирования:
         * 1. Если число целое - добавляем .0
         * 2. Если число дробное - показываем 2 знака после запятой
         * 3. Используем локаль US для гарантированного использования точки
         */

        // Проверяем, является ли число целым
        if (value == Math.floor(value) && !Double.isInfinite(value)) {
            // Целое число - добавляем .0
            return String.format(Locale.US, "%.1f", value);
        } else {
            // Нецелое число - показываем 2 знака после запятой
            return String.format(Locale.US, "%.2f", value);
        }
    }

    /**
     * Очищает все данные приложения.
     * Удаляет экспериментальные и интерполяционные данные,
     * сбрасывает коэффициенты уравнения и обновляет интерфейс.
     */
    private void clearAllData() {
        experimentalData.clear();
        interpolationTimes.clear();

        experimentalModel.setRowCount(0);
        interpolationModel.setRowCount(0);

        // Устанавливаем коэффициенты по умолчанию
        a = 0.8904;
        b = 1.6644;

        updateEquationLabel();
        updateStatus("Все данные очищены. Начните с добавления экспериментальных точек.");

        // Обновляем график если он открыт
        if (graphFrame != null && graphFrame.isVisible()) {
            graphFrame.updateGraph(experimentalData, a, b, interpolationTimes);
        }
    }

    /**
     * Очищает все данные с предварительным подтверждением пользователя.
     * Показывает диалоговое окно с предупреждением о необратимости операции.
     */
    private void clearAllDataWithConfirmation() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Вы действительно хотите очистить ВСЕ данные?\n" +
                        "Это действие нельзя отменить.\n\n" +
                        "Будут очищены:\n" +
                        "• Все экспериментальные точки\n" +
                        "• Все времена интерполяции\n" +
                        "• Все расчеты",
                "Очистка всех данных",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            clearAllData();
            JOptionPane.showMessageDialog(this,
                    "Все данные успешно очищены.\nТеперь вы можете начать с чистого листа.",
                    "Очистка завершена",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Обновляет таблицу экспериментальных данных в интерфейсе.
     * Заполняет таблицу текущими значениями из списка experimentalData.
     */
    private void updateExperimentalTable() {
        experimentalModel.setRowCount(0);
        for (DataPoint point : experimentalData) {
            experimentalModel.addRow(new Object[]{
                    formatWithDecimal(point.getTime()),
                    formatWithDecimal(point.getTemperature())
            });
        }
    }

    /**
     * Обновляет метку с уравнением регрессии.
     * Отображает текущие значения коэффициентов a и b.
     */
    private void updateEquationLabel() {
        equationLabel.setText("Уравнение: T = " + String.format("%.4f", a) + " * t + " + String.format("%.4f", b));
    }

    /**
     * Обновляет строку состояния в интерфейсе.
     *
     * @param message сообщение для отображения в статусной строке
     */
    private void updateStatus(String message) {
        statusLabel.setText(message);
    }

    /**
     * Экспортирует данные в Excel файл.
     * Сохраняет экспериментальные, интерполяционные и пользовательские данные,
     * а также коэффициенты уравнения.
     */
    private void exportData() {
        // Сохраняем текущие экспериментальные данные
        saveExperimentalData();

        // Собираем интерполяционные данные из таблицы
        List<DataPoint> interpolatedData = new ArrayList<>();
        for (int i = 0; i < interpolationModel.getRowCount(); i++) {
            try {
                Object timeObj = interpolationModel.getValueAt(i, 0);
                Object tempObj = interpolationModel.getValueAt(i, 1);

                if (timeObj == null || tempObj == null) {
                    continue; // Пропускаем пустые строки
                }

                // Исправляем форматирование чисел с запятыми
                String timeStr = timeObj.toString().trim().replace(',', '.');
                String tempStr = tempObj.toString().trim().replace(',', '.');

                double time = Double.parseDouble(timeStr);
                double temp = Double.parseDouble(tempStr);

                interpolatedData.add(new DataPoint(time, temp));
            } catch (NumberFormatException e) {
                // Пропускаем некорректные строки
                System.err.println("Ошибка парсинга в строке " + i + ": " + e.getMessage());
            }
        }

        // Вызываем экспорт в Excel
        FileExporter.exportToExcel(experimentalData, interpolatedData, a, b, this);
    }

    /**
     * Показывает диалоговое окно с сообщением об ошибке.
     *
     * @param title заголовок окна ошибки
     * @param message текст сообщения об ошибке
     */
    private void showErrorDialog(String title, String message) {
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
    }
}