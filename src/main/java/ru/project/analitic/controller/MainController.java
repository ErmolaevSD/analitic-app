package ru.project.analitic.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

/**
 * Контроллер главного окна приложения.
 *
 * <p>Обрабатывает действия пользователя на главном экране и управляет
 * навигацией между различными функциями приложения.</p>
 *
 * <p>Основные функции:</p>
 * <ul>
 *   <li>Отображение логов операций в текстовом поле</li>
 *   <li>Управление операциями с файлами (выбор, анализ)</li>
 *   <li>Вызов сервисов анализа данных</li>
 *   <li>Отображение диалогов результатов</li>
 *   <li>Сохранение логов в файл</li>
 * </ul>
 *
 * <p>Интеграция с сервисами:</p>
 * <ul>
 *   <li>MainService - основной сервис анализа данных (версия 2.0)</li>
 *   <li>ExcelFileManager - менеджер Excel файлов (версия 2.0)</li>
 *   <li>Логирование через SLF4J + java.util.logging для UI</li>
 * </ul>
 *
 * @author ErmolaevSD
 * @version 2.0
 * @see MainService
 * @see ExcelFileManager
 */
@Getter
@Setter
public class MainController implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    // Константы для диалогов
    private static final String SUCCESS_TITLE = "Успех";
    private static final String WARNING_TITLE = "Предупреждение";
    private static final String ERROR_TITLE = "Ошибка";
    private static final String ABOUT_TITLE = "О программе";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    // Константы для сообщений операций
    private static final String OP_DUPLICATES_TWO_FILES = "Поиск дубликатов в двух файлах";
    private static final String OP_DUPLICATES_ONE_FILE = "Поиск дубликатов в одном файле";
    private static final String OP_SVERKA_116 = "Сверка по ст. 116.1";
    private static final String FILE_READING_FORMAT = "Чтение файла = {}";
    private static final String FILE_RECORDS_FORMAT = "Прочитано записей из файла = {}: {}";
    private static final String EMPTY_DATA_ERROR = "Файл не содержит данных или они не распознаны";
    private static final String OPERATION_STARTED = "=== ЗАПУСК ОПЕРАЦИИ: {} ===";
    private static final String OPERATION_COMPLETED = "=== ОПЕРАЦИЯ ЗАВЕРШЕНА ===";

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

    @FXML
    private Label applicationTitleLabel;
    @FXML
    private Label versionLabel;

    @FXML
    private Button duplicatesInTwoFilesButton;
    @FXML
    private Button duplicatesInOneFileButton;
    @FXML
    private Button sverka116Button;
    @FXML
    private Button clearLogsButton;
    @FXML
    private Button saveLogsButton;

    private int logCounter = 0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        logger.debug("Инициализация MainController...");
        logger.debug("Сервисы успешно валидированы");

        initializeUIElements();

        appendLog(Level.INFO, "Приложение корректно запущено. Готово к использованию.");
        setupSystemLogging();

        logger.debug("MainController инициализирован успешно");
    }

    /**
     * Инициализирует UI элементы версии 2.0
     */
    private void initializeUIElements() {
        logger.debug("Инициализация UI элементов версии 2.0");

        // Убеждаемся что элементы инициализированы
        if (applicationTitleLabel != null) {
            logger.debug("Заголовок приложения найден");
        }
        if (versionLabel != null) {
            logger.debug("Label версии найден");
        }
        if (duplicatesInTwoFilesButton != null) {
            logger.debug("Кнопка 'Дубликаты в двух файлах' найдена");
        }
        if (duplicatesInOneFileButton != null) {
            logger.debug("Кнопка 'Дубликаты в одном файле' найдена");
        }
        if (sverka116Button != null) {
            logger.debug("Кнопка 'Сверка 116' найдена");
        }

        // Инициализация контекстного меню для TextArea
        if (logTextArea != null) {
            logger.debug("Инициализация контекстного меню для TextArea");
            javafx.scene.control.ContextMenu contextMenu = new javafx.scene.control.ContextMenu();
            javafx.scene.control.MenuItem clearItem = new javafx.scene.control.MenuItem("🗑️ Очистить");
            clearItem.setOnAction(event -> handleClearLogs());
            javafx.scene.control.MenuItem saveItem = new javafx.scene.control.MenuItem("💾 Сохранить");
            saveItem.setOnAction(event -> handleSaveLogs());
            contextMenu.getItems().addAll(clearItem, saveItem);
            logTextArea.setContextMenu(contextMenu);
            logger.debug("Контекстное меню TextArea инициализировано");
        }

        logger.debug("Все UI элементы успешно инициализированы");
    }

    /**
     * Настройка перехвата системных логов
     */
    private void setupSystemLogging() {
        java.util.logging.Logger rootLogger = java.util.logging.Logger.getLogger("");
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
        logger.debug("Открыт диалог сохранения логов");
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
                String message = "Логи сохранены в: " + file.getName();
                appendLog(Level.INFO, message);
                logger.info("Логи успешно сохранены в файл: {}", file.getAbsolutePath());
            } catch (Exception e) {
                String errorMsg = "Не удалось сохранить файл: " + e.getMessage();
                showError(errorMsg);
                logger.error("Ошибка при сохранении логов", e);
            }
        } else {
            logger.debug("Диалог сохранения логов отменен пользователем");
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
        logger.info("Начало операции: {}", OP_DUPLICATES_TWO_FILES);
        appendLog(Level.INFO, OPERATION_STARTED.replace("{}", OP_DUPLICATES_TWO_FILES));

        File fileOne = openFile("Выберите первый Excel файл");
        if (fileOne == null) {
            logger.debug("Первый файл не выбран, операция отменена");
            return;
        }

        File fileTwo = openFile("Выберите второй Excel файл");
        if (fileTwo == null) {
            logger.debug("Второй файл не выбран, операция отменена");
            return;
        }

        try {
            List<Person> admPeople = readExcelSafely(fileOne, Person.class);
            List<Person> admPeople2 = readExcelSafely(fileTwo, Person.class);

            if (isDataEmpty(admPeople) || isDataEmpty(admPeople2)) {
                logger.warn("Один из файлов пуст или содержит невалидные данные");
                appendLog(Level.WARNING, EMPTY_DATA_ERROR);
                showWarning();
                return;
            }

            logger.info("Начало анализа дубликатов между файлами: {} и {}", fileOne.getName(), fileTwo.getName());
            appendLog(Level.INFO, "Поиск дубликатов в файлах {%s, %s}".formatted(fileOne.getName(), fileTwo.getName()));
            mainService.duplicateInTwoFiles(admPeople, admPeople2, Person.class);

            String successMsg = "Операция успешно завершена";
            logger.info(successMsg);
            appendLog(Level.INFO, successMsg);
            showInfo(successMsg);

        } catch (Exception e) {
            String errorMsg = "Произошла ошибка: " + e.getMessage();
            logger.error("Ошибка при поиске дубликатов в двух файлах", e);
            appendLog(Level.SEVERE, errorMsg);
            showError(errorMsg);
        }

        appendLog(Level.INFO, OPERATION_COMPLETED);
        logger.info("Операция завершена");
    }

    /**
     * Обрабатывает поиск дубликатов в одном файле.
     *
     * <p>Пользователь выбирает Excel файл, после чего выполняется
     * анализ на наличие дублирующихся записей внутри файла.</p>
     */
    @FXML
    private void duplicateInOneFiles() {
        logger.info("Начало операции: {}", OP_DUPLICATES_ONE_FILE);
        appendLog(Level.INFO, OPERATION_STARTED.replace("{}", OP_DUPLICATES_ONE_FILE));

        File fileOne = openFile("Выберите Excel файл для анализа");
        if (fileOne == null) {
            logger.debug("Файл не выбран, операция отменена");
            return;
        }

        try {
            List<Person> admPeople = readExcelSafely(fileOne, Person.class);

            if (isDataEmpty(admPeople)) {
                logger.warn("Файл пуст или содержит невалидные данные: {}", fileOne.getName());
                appendLog(Level.WARNING, EMPTY_DATA_ERROR);
                showWarning();
                return;
            }

            logger.info("Начало поиска дубликатов в файле: {}", fileOne.getName());
            appendLog(Level.INFO, "Поиск дубликатов в файле = %s".formatted(fileOne.getName()));
            List<Person> duplicates = mainService.duplicateInOneFile(admPeople, Person.class, true);

            if (duplicates.isEmpty()) {
                String successMsg = "Дубликаты не найдены";
                logger.info(successMsg);
                appendLog(Level.INFO, successMsg);
                showInfo(successMsg);
            } else {
                String successMsg = String.format("Сверка завершена! Найдено дубликатов: %d", duplicates.size());
                logger.info(successMsg);
                appendLog(Level.INFO, successMsg);
                showInfo(successMsg);
            }

        } catch (Exception e) {
            String errorMsg = "Произошла ошибка: " + e.getMessage();
            logger.error("Ошибка при поиске дубликатов в файле", e);
            appendLog(Level.SEVERE, errorMsg);
            showError(errorMsg);
        }

        appendLog(Level.INFO, OPERATION_COMPLETED);
        logger.info("Операция завершена");
    }

    /**
     * Обрабатывает сверку 116 часть 1.
     *
     * <p>Выполняет анализ временных периодов для выявления проблемных
     * ситуаций с повторными правонарушениями.</p>
     */
    @FXML
    private void sverka116PathOne() {
        logger.info("Начало операции: {}", OP_SVERKA_116);
        appendLog(Level.INFO, OPERATION_STARTED.replace("{}", OP_SVERKA_116));

        File file = openFile("Выберите Excel файл для сверки 116");
        if (file == null) {
            logger.debug("Файл не выбран, операция отменена");
            return;
        }

        try {
            List<AdmPerson> admPeople = readExcelSafely(file, AdmPerson.class);

            if (isDataEmpty(admPeople)) {
                logger.warn("Файл пуст или содержит невалидные данные: {}", file.getName());
                appendLog(Level.WARNING, EMPTY_DATA_ERROR);
                showWarning();
                return;
            }

            logger.info("Начало анализа сверки 116 для файла: {}", file.getName());
            appendLog(Level.INFO, "Анализ повторности");
            mainService.sverka116PathOne(admPeople);

            String successMsg = "Сверка успешно завершена!";
            logger.info(successMsg);
            appendLog(Level.INFO, successMsg);
            showInfo(successMsg);

        } catch (Exception e) {
            String errorMsg = "Произошла ошибка: " + e.getMessage();
            logger.error("Ошибка при выполнении сверки 116", e);
            appendLog(Level.SEVERE, errorMsg);
            showError(errorMsg);
        }

        appendLog(Level.INFO, OPERATION_COMPLETED);
        logger.info("Операция завершена");
    }

    /**
     * Показывает информацию о программе.
     */
    @FXML
    private void showAbout() {
        logger.debug("Открыто окно 'О программе'");
        appendLog(Level.INFO, "Открыто окно 'О программе'");

        Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
        alertInfo.setTitle(ABOUT_TITLE);
        alertInfo.setHeaderText("Аналитика - Онлайн");
        alertInfo.setContentText(
                """
                        Версия 2.0
                        
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
            logger.info("Файл выбран: {}", selectedFile.getName());
            appendLog(Level.INFO, "Файл выбран: " + selectedFile.getName());
        } else {
            logger.debug("Выбор файла отменен пользователем");
            appendLog(Level.FINE, "Выбор файла отменен");
        }

        return selectedFile;
    }

    /**
     * Безопасно читает Excel файл с логированием и обработкой ошибок.
     *
     * @param <T>    тип объектов для чтения
     * @param file   файл для чтения
     * @param tClass класс объектов
     * @return список прочитанных объектов
     * @throws RuntimeException если произойдет ошибка при чтении
     */
    private <T> List<T> readExcelSafely(File file, Class<T> tClass) {
        try {
            logger.debug(FILE_READING_FORMAT, file.getName());
            appendLog(Level.FINE, FILE_READING_FORMAT.replace("{}", file.getName()));

            List<T> data = excelFileManager.readExcel(file.getPath(), tClass);

            logger.debug(FILE_RECORDS_FORMAT.replace("{}", file.getName()).replace("{}", String.valueOf(data.size())));
            appendLog(Level.FINE, FILE_RECORDS_FORMAT.replace("{}", file.getName()).replace("{}", String.valueOf(data.size())));

            return data;
        } catch (Exception e) {
            logger.error("Ошибка при чтении файла: {}", file.getName(), e);
            throw new RuntimeException("Ошибка при чтении файла: " + file.getName(), e);
        }
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
    private void showWarning() {
        showAlert(Alert.AlertType.WARNING, MainController.WARNING_TITLE, MainController.EMPTY_DATA_ERROR);
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