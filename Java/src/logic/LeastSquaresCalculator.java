package logic;

import model.DataPoint;
import java.util.List;

/**
 * Класс LeastSquaresCalculator реализует метод наименьших квадратов (МНК)
 * для вычисления коэффициентов линейной регрессии.
 * Используется для аппроксимации экспериментальных данных линейной функцией.
 *
 * @author Petrushchenko A.A.
 * @version 1.0
 */
public class LeastSquaresCalculator {

    /**
     * Вычисляет коэффициенты линейной регрессии a и b методом наименьших квадратов.
     * Уравнение регрессии: T = a*t + b
     *
     * @param data список экспериментальных точек (время, температура)
     * @return массив из двух элементов: [a, b]
     * @throws IllegalArgumentException если данные некорректны
     */
    public static double[] calculateCoefficients(List<DataPoint> data) {
        // Проверка входных данных
        if (data == null) {
            throw new IllegalArgumentException("Список данных не может быть null");
        }

        if (data.isEmpty()) {
            throw new IllegalArgumentException("Список данных не может быть пустым");
        }

        if (data.size() < 2) {
            throw new IllegalArgumentException("Для расчета нужно как минимум 2 точки данных");
        }

        int n = data.size();
        double sumX = 0, sumY = 0, sumXY = 0, sumXX = 0;

        // Проверка корректности данных и вычисление сумм
        for (DataPoint point : data) {
            double x = point.getTime();
            double y = point.getTemperature();

            if (Double.isNaN(x) || Double.isInfinite(x)) {
                throw new IllegalArgumentException("Обнаружено недопустимое значение времени: " + x);
            }

            if (Double.isNaN(y) || Double.isInfinite(y)) {
                throw new IllegalArgumentException("Обнаружено недопустимое значение температуры: " + y);
            }

            sumX += x;
            sumY += y;
            sumXY += x * y;
            sumXX += x * x;
        }

        double denominator = n * sumXX - sumX * sumX;

        // Проверка на вырожденный случай
        if (Math.abs(denominator) < 1e-10) {
            throw new ArithmeticException("Невозможно вычислить коэффициенты: точки расположены вертикально");
        }

        double a = (n * sumXY - sumX * sumY) / denominator;
        double b = (sumY - a * sumX) / n;

        // Проверка корректности результатов
        if (Double.isNaN(a) || Double.isInfinite(a) || Double.isNaN(b) || Double.isInfinite(b)) {
            throw new ArithmeticException("Получены недопустимые значения коэффициентов");
        }

        return new double[]{a, b};
    }

    /**
     * Вычисляет температуру в заданный момент времени по уравнению линейной регрессии.
     *
     * @param time время, для которого вычисляется температура
     * @param a коэффициент наклона прямой
     * @param b коэффициент смещения
     * @return вычисленное значение температуры
     * @throws IllegalArgumentException если time имеет недопустимое значение
     */
    public static double calculateTemperature(double time, double a, double b) {
        if (Double.isNaN(time) || Double.isInfinite(time)) {
            throw new IllegalArgumentException("Время должно быть конечным числом");
        }

        double result = a * time + b;

        if (Double.isNaN(result) || Double.isInfinite(result)) {
            throw new ArithmeticException("Результат вычисления температуры недопустим");
        }

        return result;
    }

    /**
     * Проверяет, находится ли заданное время в диапазоне экспериментальных данных.
     *
     * @param time время для проверки
     * @param data список экспериментальных точек
     * @return true если время находится в диапазоне данных
     */
    public static boolean isTimeInRange(double time, List<DataPoint> data) {
        if (data == null || data.isEmpty()) {
            return false;
        }

        double minTime = data.stream().mapToDouble(DataPoint::getTime).min().orElse(0);
        double maxTime = data.stream().mapToDouble(DataPoint::getTime).max().orElse(0);

        return time >= minTime && time <= maxTime;
    }

    /**
     * Возвращает строковое представление диапазона времени экспериментальных данных.
     *
     * @param data список экспериментальных точек
     * @return строка в формате "min - max часов" или "нет данных"
     */
    public static String getTimeRangeString(List<DataPoint> data) {
        if (data == null || data.isEmpty()) {
            return "нет данных";
        }

        double minTime = data.stream().mapToDouble(DataPoint::getTime).min().orElse(0);
        double maxTime = data.stream().mapToDouble(DataPoint::getTime).max().orElse(0);

        return String.format("%.1f - %.1f часов", minTime, maxTime);
    }
}