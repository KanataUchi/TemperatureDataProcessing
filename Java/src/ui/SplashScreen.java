package ui;

import javax.swing.*;
import java.awt.*;

/**
 * Класс SplashScreen представляет собой стартовый экран приложения.
 * Отображает информацию о курсовой работе и университете с прогресс-баром загрузки.
 *
 * @author Petrushchenko A.A.
 * @version 1.0
 */
public class SplashScreen extends JWindow {
    /**
     * Прогресс-бар для отображения процесса загрузки.
     */
    private JProgressBar progressBar;

    /**
     * Таймер для анимации прогресс-бара.
     */
    private Timer timer;

    /**
     * Текущее значение прогресса (0-100).
     */
    private int progress = 0;

    /**
     * Флаг для отслеживания нажатия кнопки "Пропустить".
     */
    private boolean isSkipped = false;

    /**
     * Конструктор создает и настраивает стартовый экран.
     */
    public SplashScreen() {
        // Создаем панель
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));

        // Заголовок университета
        JPanel headerPanel = createHeaderPanel();

        // Основная информация о курсовой работе
        JPanel infoPanel = createInfoPanel();

        // Панель с прогресс-баром и кнопкой
        JPanel bottomPanel = createBottomPanel();

        // Собираем интерфейс
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(infoPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        setContentPane(panel);
        setSize(900, 800);
        setLocationRelativeTo(null); // Центрируем окно

        // Запускаем таймер для прогресс-бара
        startTimer();
    }

    /**
     * Создает панель заголовка с логотипом и информацией об университете.
     *
     * @return панель заголовка
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Логотип университета
        JLabel logoLabel = createPhotoLabel();

        // Название университета и факультета
        JLabel universityLabel = new JLabel("Белорусский национальный технический университет");
        universityLabel.setFont(new Font("Arial", Font.BOLD, 14));
        universityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel facultyLabel = new JLabel("Факультет информационных технологий и робототехники");
        facultyLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        facultyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel departmentLabel = new JLabel("Кафедра программного обеспечения информационных систем и технологий");
        departmentLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        departmentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(logoLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        headerPanel.add(universityLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(facultyLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(departmentLabel);

        return headerPanel;
    }
    /**
     * Создает панель с информацией о курсовой работе.
     *
     * @return панель с информацией
     */
    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Заголовок и тема курсовой
        JLabel titleLabel = new JLabel("КУРСОВАЯ РАБОТА", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subjectLabel = new JLabel("по дисциплине «Программирование на языке Java»");
        subjectLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subjectLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel topicLabel = new JLabel("Тема: Обработка экспериментальных данных");
        topicLabel.setFont(new Font("Arial", Font.BOLD, 14));
        topicLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel variantLabel = new JLabel("Вариант 60");
        variantLabel.setFont(new Font("Arial", Font.BOLD, 14));
        variantLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Информация о студенте
        JLabel studentLabel = new JLabel("Выполнил: студент группы 10702423");
        studentLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        studentLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel nameLabel = new JLabel("Петрущенко Александр Андреевич");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Информация о преподавателе
        JLabel teacherLabel = new JLabel("Преподаватель: к.ф.-м.н., доц.");
        teacherLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        teacherLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel teacherNameLabel = new JLabel("Сидорик В.В.");
        teacherNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        teacherNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Год выполнения
        JLabel yearLabel = new JLabel("Минск, 2025");
        yearLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        yearLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Добавляем все компоненты с отступами
        infoPanel.add(titleLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        infoPanel.add(subjectLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        infoPanel.add(topicLabel);
        infoPanel.add(variantLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        infoPanel.add(studentLabel);
        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        infoPanel.add(teacherLabel);
        infoPanel.add(teacherNameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        infoPanel.add(yearLabel);

        return infoPanel;
    }

    /**
     * Создает нижнюю панель с прогресс-баром и кнопкой пропуска.
     *
     * @return нижняя панель
     */
    private JPanel createBottomPanel() {
        // Прогресс-бар для отображения загрузки
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setForeground(new Color(0, 102, 204));
        progressBar.setString("Загрузка...");

        // Панель для кнопки (внизу справа)
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);

        // Кнопка пропустить - размещаем внизу справа
        JButton skipButton = new JButton("Пропустить");
        skipButton.addActionListener(e -> skipAndOpenMain());

        // Панель для выравнивания кнопки по правому краю
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(skipButton);

        bottomPanel.add(progressBar, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        return bottomPanel;
    }

    /**
     * Запускает таймер для анимации прогресс-бара.
     * Таймер обновляет прогресс каждые 50 миллисекунд.
     */
    private void startTimer() {
        timer = new Timer(600, e -> {
            // Проверяем, не была ли нажата кнопка "Пропустить"
            if (isSkipped) {
                timer.stop();
                return;
            }

            progress += 1;
            progressBar.setValue(progress);
            progressBar.setString("Загрузка: " + progress + "%");

            // Завершение загрузки при достижении 100%
            if (progress >= 100) {
                timer.stop();
                closeAndOpenMain();
            }
        });
        timer.start();
    }

    /**
     * Создает метку с логотипом университета.
     * Если изображение не найдено, отображается эмодзи.
     *
     * @return JLabel с изображением или эмодзи
     */
    private JLabel createPhotoLabel() {
        JLabel photoLabel = new JLabel("", SwingConstants.CENTER);
        photoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        try {
            // Пробуем загрузить картинку из ресурсов
            ImageIcon originalIcon = loadImageFromResources();

            if (originalIcon != null) {
                // Масштабируем картинку до нужного размера
                Image image = originalIcon.getImage();
                Image scaledImage = image.getScaledInstance(283, 283, Image.SCALE_SMOOTH);
                ImageIcon scaledIcon = new ImageIcon(scaledImage);
                photoLabel.setIcon(scaledIcon);
            } else {
                // Используем эмодзи если картинка не найдена
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
     * Загружает изображение логотипа из ресурсов.
     *
     * @return ImageIcon загруженного изображения или null в случае ошибки
     */
    private ImageIcon loadImageFromResources() {
        try {
            // Пытаемся загрузить логотип из папки resources
            return new ImageIcon("Java/src/resources/logo.png");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Обрабатывает нажатие кнопки "Пропустить".
     * Останавливает таймер и открывает главное окно.
     */
    private void skipAndOpenMain() {
        // Устанавливаем флаг, что кнопка была нажата
        isSkipped = true;

        // Останавливаем таймер прогресса
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        closeAndOpenMain();
    }

    /**
     * Закрывает стартовый экран и открывает главное окно приложения.
     * Выполняется в потоке обработки событий EDT.
     */
    private void closeAndOpenMain() {
        dispose(); // Закрываем splash screen

        // Запускаем главное окно в потоке обработки событий
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}