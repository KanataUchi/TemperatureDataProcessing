package ui;

import model.DataPoint;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

/**
 * Панель для отображения графика экспериментальных данных.
 * Рисует оси координат, экспериментальные точки, аппроксимирующую прямую,
 * интерполяционные и пользовательские точки.
 *
 * @author Петрущенко Александр Андреевич
 * @version 1.0
 */
public class GraphPanel extends JPanel {
    /**
     * Список экспериментальных точек данных.
     */
    private List<DataPoint> experimentalData;

    /**
     * Коэффициенты уравнения прямой: T = a*t + b.
     */
    private double a, b;

    /**
     * Времена для интерполяции температуры.
     */
    private List<Double> interpolationTimes;


    // Цвета для различных элементов графика
    private final Color EXPERIMENTAL_COLOR = Color.BLUE;
    private final Color LINE_COLOR = Color.RED;
    private final Color INTERPOLATION_COLOR = Color.GREEN;

    /**
     * Создает новую панель графика с заданными данными.
     *
     * @param experimentalData список экспериментальных точек
     * @param a коэффициент наклона прямой
     * @param b коэффициент смещения прямой
     * @param interpolationTimes список времен для интерполяции
     */
    public GraphPanel(List<DataPoint> experimentalData, double a, double b,
                      List<Double> interpolationTimes) {
        this.experimentalData = experimentalData;
        this.a = a;
        this.b = b;
        this.interpolationTimes = interpolationTimes;

        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
    }

    /**
     * Переопределяет метод отрисовки панели.
     * Рисует график с экспериментальными данными.
     *
     * @param g графический контекст для рисования
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Включаем сглаживание для более качественной отрисовки
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        drawGraph(g2d);
    }

    /**
     * Рисует график с осями, точками и линией регрессии.
     *
     * @param g2d графический контекст 2D
     */
    private void drawGraph(Graphics2D g2d) {
        int padding = 80; // Отступ от краев панели
        int width = getWidth() - 2 * padding;
        int height = getHeight() - 2 * padding;

        // Рисуем оси координат
        g2d.setColor(Color.BLACK);
        g2d.drawLine(padding, padding, padding, padding + height); // Ось Y
        g2d.drawLine(padding, padding + height, padding + width, padding + height); // Ось X

        // Добавляем стрелки на концах осей
        drawAxisArrows(g2d, padding, padding, padding, padding + height);
        drawAxisArrows(g2d, padding, padding + height, padding + width, padding + height);

        // Подписи осей
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Время, час", padding + width / 2 - 30, padding + height + 50);

        // Подпись для оси Y (вертикальный текст)
        Font originalFont = g2d.getFont();
        AffineTransform originalTransform = g2d.getTransform();

        g2d.translate(25, padding + height/2);
        g2d.rotate(-Math.PI/2);
        g2d.drawString("Температура, °C", 0, 0);

        g2d.setTransform(originalTransform);
        g2d.setFont(originalFont);

        // Определяем диапазоны значений для масштабирования
        double[] ranges = calculateValueRanges();
        double minTime = ranges[0], maxTime = ranges[1];
        double minTemp = ranges[2], maxTemp = ranges[3];

        // Масштабирующие коэффициенты
        double xScale = width / (maxTime - minTime);
        double yScale = height / (maxTemp - minTemp);

        // Рисуем деления и сетку на осях
        drawAxisTicks(g2d, padding, width, height, minTime, maxTime,
                minTemp, maxTemp, xScale, yScale);

        // Рисуем экспериментальные точки
        drawExperimentalPoints(g2d, padding, height, minTime, minTemp, xScale, yScale);

        // Рисуем аппроксимирующую прямую
        drawRegressionLine(g2d, padding, width, height, minTime, minTemp, xScale, yScale);

        // Рисуем интерполяционные точки
        drawInterpolationPoints(g2d, padding, height, minTime, minTemp, xScale, yScale);

    }

    /**
     * Рассчитывает минимальные и максимальные значения для масштабирования графика.
     *
     * @return массив [minTime, maxTime, minTemp, maxTemp]
     */
    private double[] calculateValueRanges() {
        double minTime = Double.MAX_VALUE;
        double maxTime = Double.MIN_VALUE;
        double minTemp = Double.MAX_VALUE;
        double maxTemp = Double.MIN_VALUE;

        // Находим экстремумы среди экспериментальных точек
        for (DataPoint point : experimentalData) {
            minTime = Math.min(minTime, point.getTime());
            maxTime = Math.max(maxTime, point.getTime());
            minTemp = Math.min(minTemp, point.getTemperature());
            maxTemp = Math.max(maxTemp, point.getTemperature());
        }

        // Добавляем интерполяционные точки
        for (Double time : interpolationTimes) {
            double temp = a * time + b;
            minTime = Math.min(minTime, time);
            maxTime = Math.max(maxTime, time);
            minTemp = Math.min(minTemp, temp);
            maxTemp = Math.max(maxTemp, temp);
        }

        // Добавляем отступы вокруг данных
        minTime = Math.floor(minTime * 10) / 10 - 0.5;
        maxTime = Math.ceil(maxTime * 10) / 10 + 0.5;
        minTemp = Math.floor(minTemp * 10) / 10 - 1.0;
        maxTemp = Math.ceil(maxTemp * 10) / 10 + 1.0;

        // Гарантируем минимальный диапазон для удобного отображения
        if (maxTime - minTime < 5) {
            double center = (minTime + maxTime) / 2;
            minTime = center - 2.5;
            maxTime = center + 2.5;
        }

        if (maxTemp - minTemp < 10) {
            double center = (minTemp + maxTemp) / 2;
            minTemp = center - 5;
            maxTemp = center + 5;
        }

        return new double[]{minTime, maxTime, minTemp, maxTemp};
    }

    /**
     * Рисует экспериментальные точки на графике.
     *
     * @param g2d графический контекст
     * @param padding отступ от края
     * @param height высота области графика
     * @param minTime минимальное время
     * @param minTemp минимальная температура
     * @param xScale масштаб по оси X
     * @param yScale масштаб по оси Y
     */
    private void drawExperimentalPoints(Graphics2D g2d, int padding, int height,
                                        double minTime, double minTemp,
                                        double xScale, double yScale) {

        for (DataPoint point : experimentalData) {
            g2d.setColor(EXPERIMENTAL_COLOR);
            g2d.setStroke(new BasicStroke(2));
            int x = padding + (int) ((point.getTime() - minTime) * xScale);
            int y = padding + height - (int) ((point.getTemperature() - minTemp) * yScale);
            g2d.fillOval(x - 5, y - 5, 10, 10);

            drawPointLabel(g2d, x, y - 15,
                    String.format("(%.1f; %.1f)", point.getTime(), point.getTemperature()));
        }
    }

    /**
     * Рисует аппроксимирующую прямую на графике.
     *
     * @param g2d графический контекст
     * @param padding отступ от края
     * @param width ширина области графика
     * @param height высота области графика
     * @param minTime минимальное время
     * @param minTemp минимальная температура
     * @param xScale масштаб по оси X
     * @param yScale масштаб по оси Y
     */
    private void drawRegressionLine(Graphics2D g2d, int padding, int width, int height,
                                    double minTime, double minTemp,
                                    double xScale, double yScale) {
        g2d.setColor(LINE_COLOR);
        g2d.setStroke(new BasicStroke(2));

        // Вычисляем координаты точек на концах прямой
        double x1 = minTime;
        double y1 = a * x1 + b;
        double x2 = minTime + width / xScale;
        double y2 = a * x2 + b;

        int lineX1 = padding + (int) ((x1 - minTime) * xScale);
        int lineY1 = padding + height - (int) ((y1 - minTemp) * yScale);
        int lineX2 = padding + (int) ((x2 - minTime) * xScale);
        int lineY2 = padding + height - (int) ((y2 - minTemp) * yScale);

        g2d.drawLine(lineX1, lineY1, lineX2, lineY2);

        // Подписываем уравнение прямой
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString(String.format("T = %.3f * t + %.3f", a, b),
                padding + width/2 - 50, padding + 25);
    }

    /**
     * Рисует интерполяционные точки на графике.
     *
     * @param g2d графический контекст
     * @param padding отступ от края
     * @param height высота области графика
     * @param minTime минимальное время
     * @param minTemp минимальная температура
     * @param xScale масштаб по оси X
     * @param yScale масштаб по оси Y
     */
    private void drawInterpolationPoints(Graphics2D g2d, int padding, int height,
                                         double minTime, double minTemp,
                                         double xScale, double yScale) {

        for (Double time : interpolationTimes) {
            g2d.setColor(INTERPOLATION_COLOR);
            g2d.setStroke(new BasicStroke(2));
            double temp = a * time + b;
            int x = padding + (int) ((time - minTime) * xScale);
            int y = padding + height - (int) ((temp - minTemp) * yScale);
            g2d.fillOval(x - 6, y - 6, 12, 12);

            drawPointLabel(g2d, x, y - 20,
                    String.format("(%.2f; %.2f)", time, temp));
        }
    }

    /**
     * Рисует подпись для точки на графике.
     *
     * @param g2d графический контекст
     * @param x координата X центра точки
     * @param y координата Y для подписи
     * @param label текст подписи
     */
    private void drawPointLabel(Graphics2D g2d, int x, int y, String label) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        FontMetrics fm = g2d.getFontMetrics();
        int labelWidth = fm.stringWidth(label);

        int labelX = x - labelWidth/2;

        // Фон для читаемости подписи
        g2d.setColor(new Color(255, 255, 255, 220));
        g2d.fillRect(labelX - 2, y - fm.getAscent() - 2,
                labelWidth + 4, fm.getHeight() + 2);

        g2d.setColor(Color.BLACK);
        g2d.drawString(label, labelX, y);
    }

    /**
     * Рисует стрелки на концах осей координат.
     *
     * @param g2d графический контекст
     * @param x1 начальная координата X
     * @param y1 начальная координата Y
     * @param x2 конечная координата X
     * @param y2 конечная координата Y
     */
    private void drawAxisArrows(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        // Рисуем основную линию оси
        g2d.drawLine(x1, y1, x2, y2);

        // Рисуем стрелку
        int arrowSize = 8;

        // Для вертикальной оси (Y)
        if (x1 == x2) {
            int[] xPoints = {x1 - arrowSize/2, x1 + arrowSize/2, x1};
            int[] yPoints = {y1 + arrowSize, y1 + arrowSize, y1};
            g2d.fillPolygon(xPoints, yPoints, 3);
        }
        // Для горизонтальной оси (X)
        else if (y1 == y2) {
            int[] xPoints = {x2 - arrowSize, x2 - arrowSize, x2};
            int[] yPoints = {y2 - arrowSize/2, y2 + arrowSize/2, y2};
            g2d.fillPolygon(xPoints, yPoints, 3);
        }
    }

    /**
     * Рисует деления и сетку на осях координат.
     *
     * @param g2d графический контекст
     * @param padding отступ от края
     * @param width ширина области графика
     * @param height высота области графика
     * @param minTime минимальное время
     * @param maxTime максимальное время
     * @param minTemp минимальная температура
     * @param maxTemp максимальная температура
     * @param xScale масштаб по оси X
     * @param yScale масштаб по оси Y
     */
    private void drawAxisTicks(Graphics2D g2d, int padding, int width, int height,
                               double minTime, double maxTime, double minTemp, double maxTemp,
                               double xScale, double yScale) {

        // Определяем оптимальный шаг для делений
        double timeStep = getNiceStep(maxTime - minTime);
        double tempStep = getNiceStep(maxTemp - minTemp);

        // Мелкие деления (в 2 раза чаще основных)
        double minorTimeStep = timeStep / 2;
        double minorTempStep = tempStep / 2;

        g2d.setColor(Color.GRAY);
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));

        // Рисуем мелкие деления без подписей
        drawMinorTicks(g2d, padding, height, minTime, minTemp,
                minorTimeStep, minorTempStep, xScale, yScale);

        // Рисуем основные деления с подписями
        drawMajorTicks(g2d, padding, height, minTime, minTemp,
                timeStep, tempStep, xScale, yScale);

        // Рисуем сетку (пунктирные линии)
        drawGrid(g2d, padding, width, height, minTime, minTemp,
                timeStep, tempStep, xScale, yScale);
    }

    /**
     * Рисует мелкие деления на осях без подписей.
     */
    private void drawMinorTicks(Graphics2D g2d, int padding, int height,
                                double minTime, double minTemp,
                                double timeStep, double tempStep,
                                double xScale, double yScale) {
        g2d.setStroke(new BasicStroke(0.5f));

        // Мелкие деления на оси X (время)
        for (double time = minTime; time <= minTime + (getWidth() - 2*padding) / xScale;
             time += timeStep) {
            int x = padding + (int) ((time - minTime) * xScale);
            int y = padding + height;
            g2d.drawLine(x, y - 3, x, y + 3);
        }

        // Мелкие деления на оси Y (температура)
        for (double temp = minTemp; temp <= minTemp + (getHeight() - 2*padding) / yScale;
             temp += tempStep) {
            int x = padding;
            int y = padding + height - (int) ((temp - minTemp) * yScale);
            g2d.drawLine(x - 3, y, x + 3, y);
        }
    }

    /**
     * Рисует основные деления на осях с подписями.
     */
    private void drawMajorTicks(Graphics2D g2d, int padding, int height,
                                double minTime, double minTemp,
                                double timeStep, double tempStep,
                                double xScale, double yScale) {
        g2d.setStroke(new BasicStroke(1));

        // Основные деления на оси X (время)
        for (double time = minTime; time <= minTime + (getWidth() - 2*padding) / xScale;
             time += timeStep) {
            int x = padding + (int) ((time - minTime) * xScale);
            int y = padding + height;

            // Рисуем большую засечку
            g2d.drawLine(x, y - 6, x, y + 6);

            // Подписываем только "красивые" значения
            if (Math.abs(time - Math.round(time)) < 0.01 ||
                    Math.abs(time*2 - Math.round(time*2)) < 0.01) {
                String label = String.format("%.1f", time);
                FontMetrics fm = g2d.getFontMetrics();
                int labelWidth = fm.stringWidth(label);
                g2d.drawString(label, x - labelWidth/2, y + 18);
            }
        }

        // Основные деления на оси Y (температура)
        for (double temp = minTemp; temp <= minTemp + (getHeight() - 2*padding) / yScale;
             temp += tempStep) {
            int x = padding;
            int y = padding + height - (int) ((temp - minTemp) * yScale);

            // Рисуем большую засечку
            g2d.drawLine(x - 6, y, x + 6, y);

            // Подписываем только "красивые" значения
            if (Math.abs(temp - Math.round(temp)) < 0.01 ||
                    Math.abs(temp*2 - Math.round(temp*2)) < 0.01) {
                String label = String.format("%.1f", temp);
                FontMetrics fm = g2d.getFontMetrics();
                int labelWidth = fm.stringWidth(label);
                g2d.drawString(label, x - labelWidth - 8, y + 4);
            }
        }
    }

    /**
     * Рисует сетку на графике.
     */
    private void drawGrid(Graphics2D g2d, int padding, int width, int height,
                          double minTime, double minTemp,
                          double timeStep, double tempStep,
                          double xScale, double yScale) {
        // Пунктирная линия для сетки
        g2d.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                0, new float[]{2, 2}, 0));
        g2d.setColor(new Color(220, 220, 220));

        // Вертикальные линии сетки
        for (double time = minTime; time <= minTime + width / xScale; time += timeStep) {
            int x = padding + (int) ((time - minTime) * xScale);
            g2d.drawLine(x, padding, x, padding + height);
        }

        // Горизонтальные линии сетки
        for (double temp = minTemp; temp <= minTemp + height / yScale; temp += tempStep) {
            int y = padding + height - (int) ((temp - minTemp) * yScale);
            g2d.drawLine(padding, y, padding + width, y);
        }

        // Восстанавливаем сплошную линию
        g2d.setStroke(new BasicStroke(1));
    }

    /**
     * Вычисляет оптимальный шаг для делений на осях.
     * Возвращает "красивый" шаг для удобного отображения.
     *
     * @param range диапазон значений на оси
     * @return оптимальный шаг для делений
     */
    private double getNiceStep(double range) {
        // Определяем порядок величины диапазона
        double step = Math.pow(10, Math.floor(Math.log10(range)));

        // Корректируем шаг в зависимости от величины диапазона
        if (range / step > 15) step *= 2;
        else if (range / step > 8) step *= 1;
        else if (range / step > 4) step /= 2;
        else if (range / step > 2) step /= 2.5;
        else step /= 5;

        // Гарантируем минимальный шаг для удобства чтения
        if (range < 10) {
            step = Math.max(step, 0.5);
        }

        return step;
    }
}