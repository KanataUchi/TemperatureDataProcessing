package ui;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class AboutAuthorDialog extends JDialog {

    public AboutAuthorDialog(JFrame parent) {
        super(parent, "Об авторе", true);
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
        JLabel titleLabel = new JLabel("ОБ АВТОРЕ", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(0, 51, 102));

        // Картинка автора
        JLabel photoLabel = createPhotoLabel();

        // Информация об авторе
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel("Петрущенко Александр Андреевич", SwingConstants.CENTER);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel groupLabel = new JLabel("Студент группы 10702423", SwingConstants.CENTER);
        groupLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        groupLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel facultyLabel = new JLabel("Факультет информационных технологий и робототехники", SwingConstants.CENTER);
        facultyLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        facultyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel universityLabel = new JLabel("Белорусский национальный технический университет", SwingConstants.CENTER);
        universityLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        universityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Email (просто текст, не кликабельный)
        JLabel emailTitle = new JLabel("Email:", SwingConstants.CENTER);
        emailTitle.setFont(new Font("Arial", Font.BOLD, 12));
        emailTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emailLabel = new JLabel("aleksandr0620122022@gmail.com", SwingConstants.CENTER);
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailLabel.setForeground(Color.BLACK); // Просто черный цвет, не синий

        // Кнопка "Назад"
        JButton backButton = new JButton("Назад");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setPreferredSize(new Dimension(100, 35));
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        backButton.addActionListener(e -> dispose());

        // Добавляем компоненты
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 35)));
        mainPanel.add(photoLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 35)));
        mainPanel.add(nameLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        mainPanel.add(groupLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        mainPanel.add(facultyLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        mainPanel.add(universityLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 35)));
        mainPanel.add(emailTitle);
        mainPanel.add(emailLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 40)));
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
                Image scaledImage = image.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImage);
                photoLabel.setIcon(scaledIcon);
            } else {
                // Используем эмодзи если картинка не найдена
                photoLabel.setText("👨‍💻");
                photoLabel.setFont(new Font("Arial", Font.PLAIN, 60));
            }

        } catch (Exception e) {
            // Запасной вариант - эмодзи
            photoLabel.setText("👨‍💻");
            photoLabel.setFont(new Font("Arial", Font.PLAIN, 60));
        }

        return photoLabel;
    }

    private ImageIcon loadImageFromResources() {
        try {
            // Пробуем загрузить из resources
            return new ImageIcon("src/resources/avatar.jpg");

        } catch (Exception e) {
            return null;
        }
    }

    public static void showDialog(JFrame parent) {
        AboutAuthorDialog dialog = new AboutAuthorDialog(parent);
        dialog.setVisible(true);
    }
}