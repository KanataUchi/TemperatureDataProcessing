package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Диалоговое окно "Об авторе" с информацией о разработчике программы.
 * Содержит фото автора, контактные данные и информацию об образовании.
 *
 * @author Petrushchenko A.A.
 * @version 1.0
 */
public class AboutAuthorDialog extends JDialog {

    /**
     * Создает диалоговое окно "Об авторе".
     *
     * @param parent родительское окно для позиционирования диалога
     */
    public AboutAuthorDialog(JFrame parent) {
        super(parent, "Об авторе", true);
        setupUI();
    }

    /**
     * Настраивает пользовательский интерфейс диалогового окна.
     * Создает и размещает все компоненты с информацией об авторе.
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
        JLabel titleLabel = new JLabel("ОБ АВТОРЕ", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(0, 51, 102));

        // Фотография автора
        JLabel photoLabel = createPhotoLabel();

        // Панель с информацией об авторе
        JPanel infoPanel = createInfoPanel();

        // Кнопка для закрытия окна
        JButton backButton = createBackButton();

        // Добавляем все компоненты на главную панель
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 35)));
        mainPanel.add(photoLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 35)));
        mainPanel.add(infoPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        mainPanel.add(backButton);

        add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * Создает панель с текстовой информацией об авторе.
     *
     * @return панель с информацией
     */
    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Имя автора
        JLabel nameLabel = new JLabel("Петрущенко Александр Андреевич", SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Номер группы
        JLabel groupLabel = new JLabel("Студент группы 10702423", SwingConstants.CENTER);
        groupLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        groupLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Факультет
        JLabel facultyLabel = new JLabel("Факультет информационных технологий и робототехники", SwingConstants.CENTER);
        facultyLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        facultyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Университет
        JLabel universityLabel = new JLabel("Белорусский национальный технический университет", SwingConstants.CENTER);
        universityLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        universityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Email автора
        JLabel emailTitle = new JLabel("Email:", SwingConstants.CENTER);
        emailTitle.setFont(new Font("Arial", Font.BOLD, 12));
        emailTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emailLabel = new JLabel("aleksandr0620122022@gmail.com", SwingConstants.CENTER);
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailLabel.setForeground(Color.BLACK); // Не кликабельная ссылка

        // Добавляем все текстовые компоненты
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        infoPanel.add(groupLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        infoPanel.add(facultyLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        infoPanel.add(universityLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 35)));
        infoPanel.add(emailTitle);
        infoPanel.add(emailLabel);

        return infoPanel;
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
     * Создает метку с фотографией автора.
     * Если фото не найдено, отображается эмодзи.
     *
     * @return JLabel с фотографией или эмодзи
     */
    private JLabel createPhotoLabel() {
        JLabel photoLabel = new JLabel("", SwingConstants.CENTER);
        photoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        try {
            // Пробуем загрузить фотографию из ресурсов
            ImageIcon originalIcon = loadImageFromResources();

            if (originalIcon != null) {
                // Масштабируем изображение до размера 120x120
                Image image = originalIcon.getImage();
                Image scaledImage = image.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImage);
                photoLabel.setIcon(scaledIcon);
            } else {
                // Используем эмодзи если фотография не найдена
                photoLabel.setText("👨‍💻");
                photoLabel.setFont(new Font("Arial", Font.PLAIN, 60));
            }

        } catch (Exception e) {
            // Запасной вариант - эмодзи при ошибке загрузки
            photoLabel.setText("👨‍💻");
            photoLabel.setFont(new Font("Arial", Font.PLAIN, 60));
        }

        return photoLabel;
    }

    /**
     * Загружает фотографию автора из ресурсов приложения.
     *
     * @return ImageIcon загруженной фотографии или null в случае ошибки
     */
    private ImageIcon loadImageFromResources() {
        try {
            // Путь к файлу фотографии в папке resources
            return new ImageIcon("Java/src/resources/avatar.jpg");
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
        AboutAuthorDialog dialog = new AboutAuthorDialog(parent);
        dialog.setVisible(true);
    }
}