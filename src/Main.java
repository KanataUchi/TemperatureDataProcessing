import ui.SplashScreen;
import javax.swing.*;

/**
 * Главный класс приложения для обработки экспериментальных данных.
 * Содержит точку входа в программу - метод main().
 * Запускает стартовый экран приложения.
 *
 * @author Петрущенко Александр Андреевич
 * @version 1.0
 */
public class Main {
    /**
     * Главный метод приложения - точка входа.
     * Настраивает внешний вид приложения и запускает стартовый экран.
     *
     * @param args аргументы командной строки (не используются)
     */
    public static void main(String[] args) {
        // Устанавливаем системный Look and Feel для Windows
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Выводим ошибку, но продолжаем работу с дефолтным стилем
            e.printStackTrace();
        }

        // Запускаем splash screen в потоке обработки событий Swing
        SwingUtilities.invokeLater(() -> {
            SplashScreen splash = new SplashScreen();
            splash.setVisible(true);
        });
    }
}