package ui;

import model.DataPoint;
import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Окно для отображения графика экспериментальных данных.
 * Показывает экспериментальные точки, аппроксимирующую прямую
 * и интерполяционные точки на графике.
 *
 * @author Петрущенко Александр Андреевич
 * @version 1.0
 */
public class GraphFrame extends JFrame {
    /**
     * Панель для рисования графика.
     */
    private GraphPanel graphPanel;

    /**
     * Создает новое окно графика с заданными данными.
     *
     * @param experimentalData список экспериментальных точек
     * @param a коэффициент наклона прямой
     * @param b коэффициент смещения прямой
     * @param interpolationTimes список времен для интерполяции
     * @param userPoints список пользовательских точек
     * @param mainFrame ссылка на главное окно приложения
     */
    public GraphFrame(List<DataPoint> experimentalData, double a, double b,
                      List<Double> interpolationTimes, List<DataPoint> userPoints,
                      MainFrame mainFrame) {

        setTitle("График экспериментальных данных");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        setupMenuBar();
        setupUI(experimentalData, a, b, interpolationTimes, userPoints);

        setSize(800, 600);
        setLocationRelativeTo(null); // Центрируем окно на экране
    }

    /**
     * Создает и настраивает строку меню окна графика.
     * Содержит только меню "Файл" с опцией закрытия окна.
     */
    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Меню "Файл" для управления окном
        JMenu fileMenu = new JMenu("Файл");

        // Пункт меню для закрытия окна графика
        JMenuItem closeItem = new JMenuItem("Закрыть");
        closeItem.addActionListener(e -> dispose());

        fileMenu.add(closeItem);
        menuBar.add(fileMenu);

        setJMenuBar(menuBar);
    }

    /**
     * Настраивает пользовательский интерфейс окна графика.
     * Создает панель графика и добавляет пользовательские точки.
     *
     * @param experimentalData список экспериментальных точек
     * @param a коэффициент наклона прямой
     * @param b коэффициент смещения прямой
     * @param interpolationTimes список времен для интерполяции
     * @param userPoints список пользовательских точек
     */
    private void setupUI(List<DataPoint> experimentalData, double a, double b,
                         List<Double> interpolationTimes, List<DataPoint> userPoints) {
        // Создаем панель графика с основными данными
        graphPanel = new GraphPanel(experimentalData, a, b, interpolationTimes);

        // Добавляем пользовательские точки если они есть
        if (userPoints != null && !userPoints.isEmpty()) {
            for (DataPoint point : userPoints) {
                graphPanel.addUserPoint(point.getTime(), point.getTemperature());
            }
        }

        add(graphPanel, BorderLayout.CENTER);
    }

    /**
     * Обновляет график новыми данными.
     * Пересоздает весь интерфейс с обновленными значениями.
     *
     * @param experimentalData обновленный список экспериментальных точек
     * @param a новый коэффициент наклона прямой
     * @param b новый коэффициент смещения прямой
     * @param interpolationTimes обновленный список времен для интерполяции
     * @param userPoints обновленный список пользовательских точек
     */
    public void updateGraph(List<DataPoint> experimentalData, double a, double b,
                            List<Double> interpolationTimes, List<DataPoint> userPoints) {
        // Удаляем все компоненты с текущей панели
        getContentPane().removeAll();

        // Пересоздаем интерфейс с новыми данными
        setupUI(experimentalData, a, b, interpolationTimes, userPoints);

        // Обновляем отображение окна
        revalidate();
        repaint();
    }
}