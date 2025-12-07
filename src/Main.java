import ui.SplashScreen;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Устанавливаем Look and Feel для Windows
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Запускаем splash screen
        SwingUtilities.invokeLater(() -> {
            SplashScreen splash = new SplashScreen();
            splash.setVisible(true);
        });
    }
}