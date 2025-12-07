package util;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Класс для ограничения ввода в JTextField (только числа).
 * Наследуется от PlainDocument для контроля ввода данных.
 * Разрешает ввод цифр, точки, запятой и минуса.
 *
 * @author Петрущенко Александр Андреевич
 * @version 1.0
 */
public class NumberDocument extends PlainDocument {
    /**
     * Максимальная длина вводимого текста.
     */
    private final int maxLength;

    /**
     * Создает новый NumberDocument с заданной максимальной длиной.
     *
     * @param maxLength максимальное количество символов в поле ввода
     */
    public NumberDocument(int maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * Вставляет строку в документ с проверкой на числовой формат.
     *
     * @param offs смещение для вставки
     * @param str строка для вставки
     * @param a атрибуты стиля
     * @throws BadLocationException если смещение недопустимо
     */
    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (str == null) return;

        // Проверка длины вводимого текста
        if (getLength() + str.length() > maxLength) {
            return;
        }

        // Разрешаем только цифры, точку, запятую и минус
        String filtered = str.replaceAll("[^0-9.,-]", "");

        // Проверяем, что минус только в начале
        if (filtered.contains("-") && offs != 0) {
            filtered = filtered.replace("-", "");
        }

        // Проверяем, что только одна десятичная точка/запятая
        String currentText = getText(0, getLength());
        if ((filtered.contains(".") || filtered.contains(",")) &&
                (currentText.contains(".") || currentText.contains(","))) {
            filtered = filtered.replace(".", "").replace(",", "");
        }

        if (!filtered.isEmpty()) {
            super.insertString(offs, filtered, a);
        }
    }
}