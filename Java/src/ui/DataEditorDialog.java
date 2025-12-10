package ui;

import model.DataPoint;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Диалоговое окно для редактирования экспериментальных данных.
 * Позволяет добавлять, удалять и изменять точки данных.
 *
 * @author Petrushchenko A.A.
 * @version 1.0
 */
public class DataEditorDialog extends JDialog {
    private JTable dataTable;
    private DefaultTableModel tableModel;
    private List<DataPoint> originalData;
    private List<DataPoint> updatedData;
    private boolean dataChanged = false;

    /**
     * Конструктор диалогового окна редактора данных.
     *
     * @param parent родительское окно
     * @param data список исходных точек данных для редактирования
     */
    public DataEditorDialog(JFrame parent, List<DataPoint> data) {
        super(parent, "Редактирование данных", true);
        this.originalData = data;

        setLayout(new BorderLayout());
        setSize(400, 300);
        setLocationRelativeTo(getParent());

        // Создаем таблицу
        String[] columns = {"Время", "Температура"};
        tableModel = new DefaultTableModel(columns, 0);
        dataTable = new JTable(tableModel);

        // Заполняем таблицу данными
        for (DataPoint point : originalData) {
            tableModel.addRow(new Object[]{point.getTime(), point.getTemperature()});
        }

        JScrollPane scrollPane = new JScrollPane(dataTable);
        add(scrollPane, BorderLayout.CENTER);

        // Панель кнопок
        JPanel buttonPanel = new JPanel();

        JButton addButton = new JButton("+");
        addButton.addActionListener(e -> tableModel.addRow(new Object[]{0.0, 0.0}));

        JButton deleteButton = new JButton("-");
        deleteButton.addActionListener(e -> {
            int selectedRow = dataTable.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.removeRow(selectedRow);
            }
        });

        JButton saveButton = new JButton("Сохранить");
        saveButton.addActionListener(e -> saveData());

        JButton cancelButton = new JButton("Отмена");
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Сохраняет отредактированные данные из таблицы.
     * Проверяет корректность введенных значений.
     */
    private void saveData() {
        updatedData = new java.util.ArrayList<>();

        // Собираем данные из таблицы
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            try {
                double time = Double.parseDouble(tableModel.getValueAt(i, 0).toString());
                double temperature = Double.parseDouble(tableModel.getValueAt(i, 1).toString());
                updatedData.add(new DataPoint(time, temperature));
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                        "Ошибка в строке " + (i + 1),
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (updatedData.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Таблица пуста",
                    "Ошибка",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        dataChanged = true;
        dispose();
    }

    /**
     * Проверяет, были ли изменены данные.
     *
     * @return true если данные были изменены, иначе false
     */
    public boolean isDataChanged() {
        return dataChanged;
    }

    /**
     * Возвращает обновленные данные.
     *
     * @return список отредактированных точек данных
     */
    public List<DataPoint> getUpdatedData() {
        return updatedData;
    }
}