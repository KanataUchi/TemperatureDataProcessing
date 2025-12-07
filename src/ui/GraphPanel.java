package ui;

import model.DataPoint;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

public class GraphPanel extends JPanel {
    private List<DataPoint> experimentalData;
    private double a, b;
    private List<Double> interpolationTimes;
    private List<DataPoint> userPoints;

    // Простые цвета
    private final Color EXPERIMENTAL_COLOR = Color.BLUE;
    private final Color LINE_COLOR = Color.RED;
    private final Color INTERPOLATION_COLOR = Color.GREEN;
    private final Color USER_POINT_COLOR = Color.MAGENTA;

    public GraphPanel(List<DataPoint> experimentalData, double a, double b, List<Double> interpolationTimes) {
        this.experimentalData = experimentalData;
        this.a = a;
        this.b = b;
        this.interpolationTimes = interpolationTimes;
        this.userPoints = new ArrayList<>();

        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
    }

    public void addUserPoint(double time, double temperature) {
        DataPoint newPoint = new DataPoint(time, temperature);
        userPoints.add(newPoint);
        repaint();
    }

    public void clearUserPoints() {
        userPoints.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Включаем сглаживание
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawGraph(g2d);
    }

    private void drawGraph(Graphics2D g2d) {
        int padding = 80;
        int width = getWidth() - 2 * padding;
        int height = getHeight() - 2 * padding;

        // Рисуем оси
        g2d.setColor(Color.BLACK);
        g2d.drawLine(padding, padding, padding, padding + height);
        g2d.drawLine(padding, padding + height, padding + width, padding + height);

        // Добавляем стрелки на концах осей
        drawAxisArrows(g2d, padding, padding, padding, padding + height);
        drawAxisArrows(g2d, padding, padding + height, padding + width, padding + height);

        // Подписи осей
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        g2d.drawString("Время, час", padding + width / 2 - 30, padding + height + 50);

        // Подпись для оси Y (Температура) - вертикальный текст
        Font originalFont = g2d.getFont();
        AffineTransform originalTransform = g2d.getTransform();

        g2d.translate(25, padding + height/2);
        g2d.rotate(-Math.PI/2);
        g2d.drawString("Температура, °C", 0, 0);

        g2d.setTransform(originalTransform);
        g2d.setFont(originalFont);

        // Диапазоны значений
        double minTime = Double.MAX_VALUE;
        double maxTime = Double.MIN_VALUE;
        double minTemp = Double.MAX_VALUE;
        double maxTemp = Double.MIN_VALUE;

        for (DataPoint point : experimentalData) {
            minTime = Math.min(minTime, point.getTime());
            maxTime = Math.max(maxTime, point.getTime());
            minTemp = Math.min(minTemp, point.getTemperature());
            maxTemp = Math.max(maxTemp, point.getTemperature());
        }

        for (DataPoint userPoint : userPoints) {
            minTime = Math.min(minTime, userPoint.getTime());
            maxTime = Math.max(maxTime, userPoint.getTime());
            minTemp = Math.min(minTemp, userPoint.getTemperature());
            maxTemp = Math.max(maxTemp, userPoint.getTemperature());
        }

        for (Double time : interpolationTimes) {
            double temp = a * time + b;
            minTime = Math.min(minTime, time);
            maxTime = Math.max(maxTime, time);
            minTemp = Math.min(minTemp, temp);
            maxTemp = Math.max(maxTemp, temp);
        }

        // Добавляем отступы
        minTime = Math.floor(minTime * 10) / 10 - 0.5;
        maxTime = Math.ceil(maxTime * 10) / 10 + 0.5;
        minTemp = Math.floor(minTemp * 10) / 10 - 1.0;
        maxTemp = Math.ceil(maxTemp * 10) / 10 + 1.0;

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

        double xScale = width / (maxTime - minTime);
        double yScale = height / (maxTemp - minTemp);

        drawAxisTicks(g2d, padding, width, height, minTime, maxTime, minTemp, maxTemp, xScale, yScale);

        // Экспериментальные точки
        g2d.setColor(EXPERIMENTAL_COLOR);
        g2d.setStroke(new BasicStroke(2));
        for (DataPoint point : experimentalData) {
            int x = padding + (int) ((point.getTime() - minTime) * xScale);
            int y = padding + height - (int) ((point.getTemperature() - minTemp) * yScale);
            g2d.fillOval(x - 5, y - 5, 10, 10);

            // Подпись в формате (время; температура)
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            String label = String.format("(%.1f; %.1f)", point.getTime(), point.getTemperature());
            FontMetrics fm = g2d.getFontMetrics();
            int labelWidth = fm.stringWidth(label);

            int labelX = x - labelWidth/2;
            int labelY = y - 15;

            // Фон для читаемости
            g2d.setColor(new Color(255, 255, 255, 220));
            g2d.fillRect(labelX - 2, labelY - fm.getAscent() - 2,
                    labelWidth + 4, fm.getHeight() + 2);

            g2d.setColor(Color.BLACK);
            g2d.drawString(label, labelX, labelY);
            g2d.setColor(EXPERIMENTAL_COLOR);
        }

        // Аппроксимирующая прямая
        g2d.setColor(LINE_COLOR);
        g2d.setStroke(new BasicStroke(2));
        double x1 = minTime;
        double y1 = a * x1 + b;
        double x2 = maxTime;
        double y2 = a * x2 + b;

        int lineX1 = padding + (int) ((x1 - minTime) * xScale);
        int lineY1 = padding + height - (int) ((y1 - minTemp) * yScale);
        int lineX2 = padding + (int) ((x2 - minTime) * xScale);
        int lineY2 = padding + height - (int) ((y2 - minTemp) * yScale);

        g2d.drawLine(lineX1, lineY1, lineX2, lineY2);

        // Уравнение прямой
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.setColor(LINE_COLOR);
        g2d.drawString(String.format("T = %.3f * t + %.3f", a, b), padding + width/2 - 50, padding + 25);

        // Интерполированные точки
        g2d.setColor(INTERPOLATION_COLOR);
        g2d.setStroke(new BasicStroke(2));
        for (Double time : interpolationTimes) {
            double temp = a * time + b;
            int x = padding + (int) ((time - minTime) * xScale);
            int y = padding + height - (int) ((temp - minTemp) * yScale);
            g2d.fillOval(x - 6, y - 6, 12, 12);

            // Подпись в формате (время; температура)
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            String label = String.format("(%.2f; %.2f)", time, temp);
            FontMetrics fm = g2d.getFontMetrics();
            int labelWidth = fm.stringWidth(label);

            int labelX = x - labelWidth/2;
            int labelY = y - 20;

            // Фон для читаемости
            g2d.setColor(new Color(255, 255, 255, 220));
            g2d.fillRect(labelX - 2, labelY - fm.getAscent() - 2,
                    labelWidth + 4, fm.getHeight() + 2);

            g2d.setColor(Color.BLACK);
            g2d.drawString(label, labelX, labelY);
            g2d.setColor(INTERPOLATION_COLOR);
        }

        // Пользовательские точки
        g2d.setColor(USER_POINT_COLOR);
        g2d.setStroke(new BasicStroke(2));
        for (DataPoint userPoint : userPoints) {
            int x = padding + (int) ((userPoint.getTime() - minTime) * xScale);
            int y = padding + height - (int) ((userPoint.getTemperature() - minTemp) * yScale);
            g2d.fillRect(x - 5, y - 5, 10, 10);

            // Подпись в формате (время; температура)
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.PLAIN, 10));
            String label = String.format("(%.1f; %.1f)", userPoint.getTime(), userPoint.getTemperature());
            FontMetrics fm = g2d.getFontMetrics();
            int labelWidth = fm.stringWidth(label);

            int labelX = x - labelWidth/2;
            int labelY = y + 20;

            // Фон для читаемости
            g2d.setColor(new Color(255, 255, 255, 220));
            g2d.fillRect(labelX - 2, labelY - fm.getAscent() - 2,
                    labelWidth + 4, fm.getHeight() + 2);

            g2d.setColor(Color.BLACK);
            g2d.drawString(label, labelX, labelY);
            g2d.setColor(USER_POINT_COLOR);
        }
    }

    private void drawAxisArrows(Graphics2D g2d, int x1, int y1, int x2, int y2) {
        // Рисуем основную линию
        g2d.drawLine(x1, y1, x2, y2);

        // Рисуем стрелку
        int arrowSize = 8;
        double angle = Math.atan2(y2 - y1, x2 - x1);

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

    private void drawAxisTicks(Graphics2D g2d, int padding, int width, int height,
                               double minTime, double maxTime, double minTemp, double maxTemp,
                               double xScale, double yScale) {

        // Определяем шаг для делений - УМЕНЬШЕННЫЙ ШАГ
        double timeRange = maxTime - minTime;
        double tempRange = maxTemp - minTemp;

        double timeStep = getNiceStep(timeRange);
        double tempStep = getNiceStep(tempRange);

        // Дополнительные мелкие деления (в 2 или 5 раз чаще)
        double minorTimeStep = timeStep / 2;
        double minorTempStep = tempStep / 2;

        g2d.setColor(Color.GRAY);
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));

        // Мелкие деления на оси X (время) - БЕЗ ПОДПИСЕЙ
        g2d.setStroke(new BasicStroke(0.5f));
        for (double time = minTime; time <= maxTime; time += minorTimeStep) {
            int x = padding + (int) ((time - minTime) * xScale);
            int y = padding + height;

            // Рисуем маленькую засечку
            g2d.drawLine(x, y - 3, x, y + 3);
        }

        // Мелкие деления на оси Y (температура) - БЕЗ ПОДПИСЕЙ
        for (double temp = minTemp; temp <= maxTemp; temp += minorTempStep) {
            int x = padding;
            int y = padding + height - (int) ((temp - minTemp) * yScale);

            // Рисуем маленькую засечку
            g2d.drawLine(x - 3, y, x + 3, y);
        }

        // Основные деления на оси X (время) - С ПОДПИСЯМИ
        g2d.setStroke(new BasicStroke(1));
        for (double time = minTime; time <= maxTime; time += timeStep) {
            int x = padding + (int) ((time - minTime) * xScale);
            int y = padding + height;

            // Рисуем большую засечку
            g2d.drawLine(x, y - 6, x, y + 6);

            // Подписываем значение (только целые или .5)
            if (Math.abs(time - Math.round(time)) < 0.01 || Math.abs(time*2 - Math.round(time*2)) < 0.01) {
                String label = String.format("%.1f", time);
                FontMetrics fm = g2d.getFontMetrics();
                int labelWidth = fm.stringWidth(label);
                g2d.drawString(label, x - labelWidth/2, y + 18);
            }
        }

        // Основные деления на оси Y (температура) - С ПОДПИСЯМИ
        for (double temp = minTemp; temp <= maxTemp; temp += tempStep) {
            int x = padding;
            int y = padding + height - (int) ((temp - minTemp) * yScale);

            // Рисуем большую засечку
            g2d.drawLine(x - 6, y, x + 6, y);

            // Подписываем значение (только целые или .5)
            if (Math.abs(temp - Math.round(temp)) < 0.01 || Math.abs(temp*2 - Math.round(temp*2)) < 0.01) {
                String label = String.format("%.1f", temp);
                FontMetrics fm = g2d.getFontMetrics();
                int labelWidth = fm.stringWidth(label);
                g2d.drawString(label, x - labelWidth - 8, y + 4);
            }
        }

        // Сетка (пунктирные линии) - только по основным делениям
        g2d.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                0, new float[]{2, 2}, 0));
        g2d.setColor(new Color(220, 220, 220));

        // Вертикальные линии сетки
        for (double time = minTime; time <= maxTime; time += timeStep) {
            int x = padding + (int) ((time - minTime) * xScale);
            g2d.drawLine(x, padding, x, padding + height);
        }

        // Горизонтальные линии сетки
        for (double temp = minTemp; temp <= maxTemp; temp += tempStep) {
            int y = padding + height - (int) ((temp - minTemp) * yScale);
            g2d.drawLine(padding, y, padding + width, y);
        }

        // Восстанавливаем сплошную линию
        g2d.setStroke(new BasicStroke(1));
    }

    private double getNiceStep(double range) {
        // Уменьшаем шаг для более частых делений
        double step = Math.pow(10, Math.floor(Math.log10(range)));

        // Уменьшаем шаг в 2-5 раз для более мелкой сетки
        if (range / step > 15) step *= 2;
        else if (range / step > 8) step *= 1;
        else if (range / step > 4) step /= 2;
        else if (range / step > 2) step /= 2.5;
        else step /= 5;

        // Обеспечиваем минимальный шаг 0.5 для времени и 1 для температуры
        if (range < 10) {
            step = Math.max(step, 0.5);
        }

        return step;
    }
}