package ui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class AboutProgramDialog extends JDialog {

    public AboutProgramDialog(JFrame parent) {
        super(parent, "О программе", true);
        setupUI();
    }

    private void setupUI() {
        setLayout(new BorderLayout());
        setSize(500, 600);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Заголовок
        JLabel titleLabel = new JLabel("О ПРОГРАММЕ", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(0, 51, 102));

        // Картинка программы
        JLabel photoLabel = createPhotoLabel();

        // Название программы
        JLabel programNameLabel = new JLabel("Обработка экспериментальных данных", SwingConstants.CENTER);
        programNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        programNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Версия
        JLabel versionLabel = new JLabel("Версия 1.0.0", SwingConstants.CENTER);
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Разделитель
        JSeparator separator = new JSeparator();
        separator.setAlignmentX(Component.CENTER_ALIGNMENT);
        separator.setMaximumSize(new Dimension(400, 1));

        // Описание программы
        JTextArea descriptionArea = new JTextArea();
        descriptionArea.setText("Программа предназначена для обработки экспериментальных\n" +
                "данных измерения температуры в течение суток.\n\n" +
                "Основные возможности:\n" +
                "• Обработка экспериментальных данных\n" +
                "• Вычисление коэффициентов аппроксимирующей прямой\n" +
                "• Линейная интерполяция методом МНК\n" +
                "• Визуализация данных на графике\n" +
                "• Добавление пользовательских точек\n" +
                "• Сохранение и отображение результатов");
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 12));
        descriptionArea.setEditable(false);
        descriptionArea.setBackground(Color.WHITE);
        descriptionArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        descriptionArea.setMaximumSize(new Dimension(400, 200));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        // Кнопка "Назад"
        JButton backButton = new JButton("Назад");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setPreferredSize(new Dimension(100, 35));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> dispose());

        // Добавляем компоненты
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(photoLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(programNameLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        mainPanel.add(versionLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(separator);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(descriptionArea);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(backButton);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JLabel createPhotoLabel() {
        JLabel photoLabel = new JLabel("", SwingConstants.CENTER);
        photoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        try {
            // Пробуем загрузить картинку
            ImageIcon originalIcon = loadImageFromResources();

            if (originalIcon != null) {
                // Масштабируем картинку
                Image image = originalIcon.getImage();
                Image scaledImage = image.getScaledInstance(300, 160, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImage);
                photoLabel.setIcon(scaledIcon);
            } else {
                // Используем эмодзи если картинка не найдена
                photoLabel.setText("📊");
                photoLabel.setFont(new Font("Arial", Font.PLAIN, 60));
            }

        } catch (Exception e) {
            // Запасной вариант - эмодзи
            photoLabel.setText("📊");
            photoLabel.setFont(new Font("Arial", Font.PLAIN, 60));
        }

        return photoLabel;
    }

    private ImageIcon loadImageFromResources() {
        try {
            return new ImageIcon("src/resources/img.jpg");
        } catch (Exception e) {
            return null;
        }
    }

    public static void showDialog(JFrame parent) {
        AboutProgramDialog dialog = new AboutProgramDialog(parent);
        dialog.setVisible(true);
    }
}