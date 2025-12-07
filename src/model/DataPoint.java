package model;

/**
 * Класс DataPoint представляет одну точку экспериментальных данных.
 * Каждая точка содержит два значения:
 * - время (в часах)
 * - температура (в градусах Цельсия)
 *
 *
 * @author Петрущенко Александр Андреевич
 * @version 1.0
 */
public class DataPoint {
    /**
     * Время измерения в часах.
     * Значение должно быть в диапазоне от 0 до 24 часов.
     */
    private final double time;

    /**
     * Температура в градусах Цельсия.
     * Значение должно быть в разумных физических пределах.
     */
    private final double temperature;

    /**
     * Конструктор создает новую точку данных с заданными значениями.
     *
     * @param time время измерения в часах
     * @param temperature температура в градусах Цельсия
     * @throws IllegalArgumentException если значения не являются числами
     */
    public DataPoint(double time, double temperature) {
        this.time = time;
        this.temperature = temperature;
    }

    /**
     * Возвращает время измерения.
     *
     * @return время в часах
     */
    public double getTime() {
        return time;
    }

    /**
     * Возвращает значение температуры.
     *
     * @return температура в градусах Цельсия
     */
    public double getTemperature() {
        return temperature;
    }

    /**
     * Возвращает строковое представление точки данных.
     * Формат: "(время час, температура°C)"
     *
     * @return отформатированная строка с данными точки
     */
    @Override
    public String toString() {
        return String.format("(%.1f час, %.1f°C)", time, temperature);
    }

    /**
     * Сравнивает эту точку данных с другим объектом.
     * Две точки считаются равными, если их времена и температуры
     * совпадают с учетом погрешности сравнения чисел с плавающей точкой.
     *
     * @param obj объект для сравнения
     * @return true если объекты равны, false в противном случае
     */
    @Override
    public boolean equals(Object obj) {
        // Проверка ссылочного равенства
        if (this == obj) return true;

        // Проверка на null и соответствие классов
        if (obj == null || getClass() != obj.getClass()) return false;

        // Приведение типа и поэлементное сравнение
        DataPoint dataPoint = (DataPoint) obj;
        return Double.compare(dataPoint.time, time) == 0 &&
                Double.compare(dataPoint.temperature, temperature) == 0;
    }

    /**
     * Возвращает хэш-код объекта.
     * Хэш-код вычисляется на основе значений времени и температуры
     * с использованием простой формулы для минимизации коллизий.
     *
     * @return хэш-код объекта
     */
    @Override
    public int hashCode() {
        return Double.hashCode(time) * 31 + Double.hashCode(temperature);
    }
}