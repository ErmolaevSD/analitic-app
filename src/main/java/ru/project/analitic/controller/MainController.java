package ru.project.analitic.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import ru.project.analitic.fileManager.ExcelFileManager;
import ru.project.analitic.model.AdmPerson;
import ru.project.analitic.model.Person;
import ru.project.analitic.service.MainService;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Контроллер главного окна приложения.
 *
 * <p>Обрабатывает действия пользователя на главном экране и управляет
 * навигацией между различными функциями приложения.</p>
 *
 * @author ErmolaevSD
 * @version 2.0
 * @see MainService
 * @see ExcelFileManager
 */
@Getter
@Setter
public class MainController implements Initializable {

    private static final String SUCCESS_TITLE = "Успех";
    private static final String WARNING_TITLE = "Предупреждение";
    private static final String ERROR_TITLE = "Ошибка";
    private static final String ABOUT_TITLE = "О программе";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public VBox logsPanel;

    private ExcelFileManager excelFileManager;
    private MainService mainService;
    private Stage primaryStage;

    @FXML
    private TextArea logTextArea;
    @FXML
    private Label logCountLabel;
    @FXML
    private CheckBox autoScrollCheck;
    private int logCounter = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        appendLog(Level.INFO, "Приложение корректно запущено. Готово к использованию.");
        setupSystemLogging();
    }

    /**
     * Настройка перехвата системных логов
     */
    private void setupSystemLogging() {
        Logger rootLogger = Logger.getLogger("");
        rootLogger.addHandler(new Handler() {
            @Override
            public void publish(LogRecord record) {
                if (isLoggable(record)) {
                    Platform.runLater(() -> appendLog(record.getLevel(), record.getMessage()));
                }
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
            }
        });
    }

    /**
     * Добавление записи в лог
     *
     * @param level   уровень логирования
     * @param message сообщение
     */
    public void appendLog(Level level, String message) {

        String timestamp = LocalDateTime.now().format(TIME_FORMATTER);
        String logEntry = String.format("[%s] [%s] %s%n", timestamp, level.getName(), message);

        Platform.runLater(() -> {
            logTextArea.appendText(logEntry);
            logCounter++;
            logCountLabel.setText(String.valueOf(logCounter));

            if (autoScrollCheck.isSelected()) {
                logTextArea.setScrollTop(Double.MAX_VALUE);
            }
        });
    }

    /**
     * Очистка логов
     */
    @FXML
    private void handleClearLogs() {
        logTextArea.clear();
        logCounter = 0;
        logCountLabel.setText("0");
        appendLog(Level.INFO, "Логи очищены");
    }

    /**
     * Сохранение логов в файл
     */
    @FXML
    private void handleSaveLogs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить логи");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt"),
                new FileChooser.ExtensionFilter("Лог файлы", "*.log")
        );

        File file = fileChooser.showSaveDialog(primaryStage);
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.print(logTextArea.getText());
                appendLog(Level.INFO, "Логи сохранены в: " + file.getName());
            } catch (Exception e) {
                showError("Не удалось сохранить файл: " + e.getMessage());
            }
        }
    }

    /**
     * Обрабатывает поиск дубликатов между двумя файлами.
     *
     * <p>Пользователь выбирает два Excel файла, после чего выполняется
     * анализ на наличие дублирующихся записей. Результаты сохраняются
     * в отдельные файлы.</p>
     */
    @FXML
    private void duplicateInTwoFiles() {
        appendLog(Level.INFO, "=== ЗАПУСК ОПЕРАЦИИ: Поиск дубликатов в двух файлах ===");

        File fileOne = openFile("Выберите первый Excel файл");
        if (fileOne == null) {
            return;
        }

        File fileTwo = openFile("Выберите второй Excel файл");
        if (fileTwo == null) {
            return;
        }

        try {
            appendLog(Level.FINE, "Чтение файла = %s".formatted(fileOne.getName()));
            List<Person> admPeople = excelFileManager.readExcel(fileOne.getPath(), Person.class);
            appendLog(Level.FINE, "Прочитано записей файла = %s: %d ".formatted(fileOne.getName(), admPeople.size()));

            appendLog(Level.FINE, "Чтение файла = %s".formatted(fileTwo.getName()));
            List<Person> admPeople2 = excelFileManager.readExcel(fileTwo.getPath(), Person.class);
            appendLog(Level.FINE, "Прочитано записей из файла = %s: %d ".formatted(fileTwo.getName(), admPeople2.size()));

            if (isDataEmpty(admPeople) || isDataEmpty(admPeople2)) {
                String errorMsg = "Один из файлов не содержит данных или они не распознаны";
                appendLog(Level.WARNING, errorMsg);
                showWarning(errorMsg);
                return;
            }

            appendLog(Level.INFO, "Поиск дубликатов в файлах {%s, %s}".formatted(fileOne.getName(), fileTwo.getName()));
            mainService.duplicateInTwoFiles(admPeople, admPeople2, Person.class);

            String successMsg = "Операция успешно завершена";
            appendLog(Level.INFO, successMsg);
            showInfo(successMsg);

        } catch (Exception e) {
            String errorMsg = "Произошла ошибка: " + e.getMessage();
            appendLog(Level.SEVERE, errorMsg);
            showError(errorMsg);
        }

        appendLog(Level.INFO, "=== ОПЕРАЦИЯ ЗАВЕРШЕНА ===");
    }

    /**
     * Обрабатывает поиск дубликатов в одном файле.
     *
     * <p>Пользователь выбирает Excel файл, после чего выполняется
     * анализ на наличие дублирующихся записей внутри файла.</p>
     */
    @FXML
    private void duplicateInOneFiles() {
        appendLog(Level.INFO, "=== ЗАПУСК ОПЕРАЦИИ: Поиск дубликатов в одном файле ===");

        File fileOne = openFile("Выберите Excel файл для анализа");
        if (fileOne == null) {
            return;
        }

        try {
            appendLog(Level.FINE, "Чтение файла = %s".formatted(fileOne.getName()));
            List<Person> admPeople = excelFileManager.readExcel(fileOne.getPath(), Person.class);
            appendLog(Level.FINE, "Прочитано записей файла = %s: %d ".formatted(fileOne.getName(), admPeople.size()));

            if (isDataEmpty(admPeople)) {
                String errorMsg = "Файл не содержит данных или они не распознаны";
                appendLog(Level.WARNING, errorMsg);
                showWarning(errorMsg);
                return;
            }

            appendLog(Level.INFO, "Поиск дубликатов в файле = %s".formatted(fileOne.getName()));
            List<Person> duplicates = mainService.duplicateInOneFile(admPeople, Person.class, true);

            if (duplicates.isEmpty()) {
                String successMsg = "Дубликаты не найдены";
                appendLog(Level.INFO, successMsg);
                showInfo(successMsg);
            } else {
                String successMsg = String.format("Сверка завершена! Найдено дубликатов: %d", duplicates.size());
                appendLog(Level.INFO, successMsg);
                showInfo(successMsg);
            }

        } catch (Exception e) {
            String errorMsg = "Произошла ошибка: " + e.getMessage();
            appendLog(Level.SEVERE, errorMsg);
            showError(errorMsg);
        }

        appendLog(Level.INFO, "=== ОПЕРАЦИЯ ЗАВЕРШЕНА ===");
    }

    /**
     * Обрабатывает сверку 116 часть 1.
     *
     * <p>Выполняет анализ временных периодов для выявления проблемных
     * ситуаций с повторными правонарушениями.</p>
     */
    @FXML
    private void sverka116PathOne() {
        appendLog(Level.INFO, "=== ЗАПУСК ОПЕРАЦИИ: Сверка по ст. 116.1 ===");

        File file = openFile("Выберите Excel файл для сверки 116");
        if (file == null) {
            return;
        }

        try {
            appendLog(Level.FINE, "Чтение файла = %s".formatted(file.getName()));
            List<AdmPerson> admPeople = excelFileManager.readExcel(file.getPath(), AdmPerson.class);
            appendLog(Level.FINE, "Прочитано записей файла = %s: %d ".formatted(file.getName(), admPeople.size()));

            if (isDataEmpty(admPeople)) {
                String errorMsg = "Файл не содержит данных или они не распознаны";
                appendLog(Level.WARNING, errorMsg);
                showWarning(errorMsg);
                return;
            }

            appendLog(Level.INFO, "Анализ повторности");
            mainService.sverka116PathOne(admPeople);

            String successMsg = "Сверка успешно завершена!";
            appendLog(Level.INFO, successMsg);
            showInfo(successMsg);

        } catch (Exception e) {
            String errorMsg = "Произошла ошибка: " + e.getMessage();
            appendLog(Level.SEVERE, errorMsg);
            showError(errorMsg);
        }

        appendLog(Level.INFO, "=== ОПЕРАЦИЯ ЗАВЕРШЕНА ===");
    }

    /**
     * Показывает информацию о программе.
     */
    @FXML
    private void showAbout() {
        appendLog(Level.INFO, "Открыто окно 'О программе'");

        Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
        alertInfo.setTitle(ABOUT_TITLE);
        alertInfo.setHeaderText("Аналитика - Онлайн");
        alertInfo.setContentText(
                """
                        Версия 1.0
                        
                        Приложение для автоматической сверки данных
                        Возможности:
                        • Поиск дубликатов в одном или двух файлах
                        • Сверка по статье 116.1
                        
                        © ErmolaevSD, 2026"""
        );

        if (primaryStage != null) {
            alertInfo.initOwner(primaryStage);
        }

        alertInfo.showAndWait();
    }

    /**
     * Открывает диалог выбора файла.
     *
     * @param title заголовок диалога
     * @return выбранный файл или null, если выбор отменен
     */
    private File openFile(String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel файлы", "*.xlsx", "*.xls"),
                new FileChooser.ExtensionFilter("Все файлы", "*.*")
        );

        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            appendLog(Level.INFO, "Файл выбран: " + selectedFile.getName());
        } else {
            appendLog(Level.FINE, "Выбор файла отменен");
        }

        return selectedFile;
    }

    /**
     * Проверяет, пуст ли список данных.
     *
     * @param list список для проверки
     * @return true если список null или пуст
     */
    private boolean isDataEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    /**
     * Показывает диалог с ошибкой.
     */
    private void showError(String message) {
        showAlert(Alert.AlertType.ERROR, MainController.ERROR_TITLE, message);
    }

    /**
     * Показывает информационный диалог.
     */
    private void showInfo(String message) {
        showAlert(Alert.AlertType.INFORMATION, MainController.SUCCESS_TITLE, message);
    }

    /**
     * Показывает диалог с предупреждением.
     */
    private void showWarning(String message) {
        showAlert(Alert.AlertType.WARNING, MainController.WARNING_TITLE, message);
    }

    /**
     * Универсальный метод для показа диалогов.
     *
     * @param alertType тип диалога
     * @param title     заголовок
     * @param message   сообщение
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        if (primaryStage != null) {
            alert.initOwner(primaryStage);
        }

        alert.showAndWait();
    }
}