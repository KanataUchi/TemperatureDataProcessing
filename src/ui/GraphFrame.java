package ui;

import model.DataPoint;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class GraphFrame extends JFrame {
    private GraphPanel graphPanel;

    public GraphFrame(List<DataPoint> experimentalData, double a, double b,
                      List<Double> interpolationTimes, List<DataPoint> userPoints,
                      MainFrame mainFrame) {

        setTitle("График экспериментальных данных");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        setupMenuBar();
        setupUI(experimentalData, a, b, interpolationTimes, userPoints);

        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Только меню "Файл" с одной кнопкой
        JMenu fileMenu = new JMenu("Файл");

        JMenuItem closeItem = new JMenuItem("Закрыть");
        closeItem.addActionListener(e -> dispose());

        fileMenu.add(closeItem);

        menuBar.add(fileMenu);

        setJMenuBar(menuBar);
    }

    private void setupUI(List<DataPoint> experimentalData, double a, double b,
                         List<Double> interpolationTimes, List<DataPoint> userPoints) {
        // Создаем панель графика
        graphPanel = new GraphPanel(experimentalData, a, b, interpolationTimes);

        // Добавляем пользовательские точки если есть
        if (userPoints != null) {
            for (DataPoint point : userPoints) {
                graphPanel.addUserPoint(point.getTime(), point.getTemperature());
            }
        }

        add(graphPanel, BorderLayout.CENTER);
    }

    public void updateGraph(List<DataPoint> experimentalData, double a, double b,
                            List<Double> interpolationTimes, List<DataPoint> userPoints) {
        // Пересоздаем интерфейс с новыми данными
        getContentPane().removeAll();
        setupUI(experimentalData, a, b, interpolationTimes, userPoints);
        revalidate();
        repaint();
    }
}