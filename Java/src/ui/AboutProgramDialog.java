package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Диалоговое окно "О программе" с информацией о функционале приложения.
 * Содержит описание возможностей программы и версию.
 *
 * @author Петрущенко Александр Андреевич
 * @version 1.0
 */
public class AboutProgramDialog extends JDialog {

    /**
     * Создает диалоговое окно "О программе".
     *
     * @param parent родительское окно для позиционирования диалога
     */
    public AboutProgramDialog(JFrame parent) {
        super(parent, "О программе", true);
        setupUI();
    }

    /**
     * Настраивает пользовательский интерфейс диалогового окна.
     * Создает и размещает все компоненты с информацией о программе.
     */
    private void setupUI() {
        setLayout(new BorderLayout());
        setSize(500, 600);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Заголовок окна
        JLabel titleLabel = new JLabel("О ПРОГРАММЕ", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(0, 51, 102));

        // Изображение программы
        JLabel photoLabel = createPhotoLabel();

        // Название и версия программы
        JLabel programNameLabel = new JLabel("Обработка экспериментальных данных", SwingConstants.CENTER);
        programNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        programNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel versionLabel = new JLabel("Версия 1.0.0", SwingConstants.CENTER);
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Разделительная линия
        JSeparator separator = new JSeparator();
        separator.setAlignmentX(Component.CENTER_ALIGNMENT);
        separator.setMaximumSize(new Dimension(400, 1));

        // Описание возможностей программы
        JTextArea descriptionArea = createDescriptionArea();

        // Кнопка для закрытия окна
        JButton backButton = createBackButton();

        // Добавляем все компоненты на главную панель
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

    /**
     * Создает текстовую область с описанием возможностей программы.
     *
     * @return JTextArea с описанием программы
     */
    private JTextArea createDescriptionArea() {
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

        return descriptionArea;
    }

    /**
     * Создает кнопку для закрытия диалогового окна.
     *
     * @return кнопка "Назад"
     */
    private JButton createBackButton() {
        JButton backButton = new JButton("Назад");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setPreferredSize(new Dimension(100, 35));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Закрываем окно при нажатии
        backButton.addActionListener(e -> dispose());

        return backButton;
    }

    /**
     * Создает метку с изображением программы.
     * Если изображение не найдено, отображается эмодзи.
     *
     * @return JLabel с изображением или эмодзи
     */
    private JLabel createPhotoLabel() {
        JLabel photoLabel = new JLabel("", SwingConstants.CENTER);
        photoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        try {
            // Пробуем загрузить изображение из ресурсов
            ImageIcon originalIcon = loadImageFromResources();

            if (originalIcon != null) {
                // Масштабируем изображение до размера 300x160
                Image image = originalIcon.getImage();
                Image scaledImage = image.getScaledInstance(300, 160, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImage);
                photoLabel.setIcon(scaledIcon);
            } else {
                // Используем эмодзи если изображение не найдено
                photoLabel.setText("📊");
                photoLabel.setFont(new Font("Arial", Font.PLAIN, 60));
            }

        } catch (Exception e) {
            // Запасной вариант - эмодзи при ошибке загрузки
            photoLabel.setText("📊");
            photoLabel.setFont(new Font("Arial", Font.PLAIN, 60));
        }

        return photoLabel;
    }

    /**
     * Загружает изображение программы из ресурсов приложения.
     *
     * @return ImageIcon загруженного изображения или null в случае ошибки
     */
    private ImageIcon loadImageFromResources() {
        try {
            // Путь к файлу изображения в папке resources
            return new ImageIcon("Java/src/resources/img.jpg");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Статический метод для отображения диалогового окна.
     *
     * @param parent родительское окно для позиционирования
     */
    public static void showDialog(JFrame parent) {
        AboutProgramDialog dialog = new AboutProgramDialog(parent);
        dialog.setVisible(true);
    }
}