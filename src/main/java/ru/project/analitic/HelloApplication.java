package ru.project.analitic;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.project.analitic.controller.MainController;
import ru.project.analitic.fileManager.ExcelFileManager;
import ru.project.analitic.fileManager.TXTFileManager;
import ru.project.analitic.service.MainService;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * Главный класс приложения для аналитики данных.
 *
 * <p>Отвечает за инициализацию и запуск JavaFX приложения,
 * настройку сервисов и отображение главного окна.</p>
 *
 * @author ErmolaevSD
 * @version 2.0
 */
public class HelloApplication extends Application {

    // Логгер приложения
    private static final Logger logger = LoggerFactory.getLogger(HelloApplication.class);

    // Константы для FXML
    private static final String MAIN_FXML = "main-view.fxml";
    private static final String APP_TITLE = "Сверка-УОДУУП";
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;

    // Константы для диалогов
    private static final String ERROR_TITLE = "Ошибка";
    private static final String STARTUP_ERROR_MSG = "Не удалось инициализировать приложение";
    private static final String FXML_NOT_FOUND_MSG = "FXML файл не найден: ";

    private Stage primaryStage;
    private MainService mainService;
    private TXTFileManager txtFileManager;
    private ExcelFileManager excelFileManager;

    /**
     * Инициализирует сервисы приложения перед запуском.
     *
     * <p>Создает экземпляры менеджеров файлов и основного сервиса.</p>
     *
     * @throws RuntimeException если не удается инициализировать компоненты приложения
     */
    @Override
    public void init() throws RuntimeException {

        logger.info("Инициализация приложения...");

        try {
            excelFileManager = new ExcelFileManager();
            logger.debug("ExcelFileManager инициализирован успешно");

            txtFileManager = new TXTFileManager();
            logger.debug("TXTFileManager инициализирован успешно");

            mainService = new MainService(excelFileManager, txtFileManager);
            logger.info("MainService инициализирован успешно");

        } catch (IllegalArgumentException e) {
            logger.error("Ошибка валидации параметров инициализации", e);
            throw new RuntimeException(STARTUP_ERROR_MSG, e);
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при инициализации приложения", e);
            throw new RuntimeException(STARTUP_ERROR_MSG, e);
        }
    }

    /**
     * Запускает JavaFX приложение и отображает главное окно.
     *
     * @param stage главная сцена приложения
     */
    @Override
    public void start(Stage stage) {

        this.primaryStage = Objects.requireNonNull(stage, "Stage не может быть null");
        logger.info("Запуск приложения: {}", APP_TITLE);

        try {
            validateInitialization();
            showMainScreen();
            logger.info("Приложение успешно запущено");
        } catch (Exception e) {
            logger.error("Ошибка при запуске приложения", e);
            showErrorAlert("Не удалось запустить приложение: " + e.getMessage());
        }
    }

    /**
     * Проверяет, что все необходимые компоненты инициализированы.
     *
     * @throws IllegalStateException если какой-либо компонент не инициализирован
     */
    private void validateInitialization() throws IllegalStateException {
        if (mainService == null) {
            throw new IllegalStateException("MainService не инициализирован");
        }
        if (excelFileManager == null) {
            throw new IllegalStateException("ExcelFileManager не инициализирован");
        }
        if (txtFileManager == null) {
            throw new IllegalStateException("TXTFileManager не инициализирован");
        }
        logger.debug("Все компоненты успешно проверены");
    }

    /**
     * Отображает главный экран приложения.
     *
     * @throws IOException если не удается загрузить FXML файл
     */
    private void showMainScreen() throws IOException {
        logger.debug("Загрузка главного экрана...");

        // Загружаем FXML
        URL fxmlLocation = loadFxmlResource();
        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Scene scene = new Scene(loader.load(), WINDOW_WIDTH, WINDOW_HEIGHT);

        // Инициализируем контроллер
        MainController controller = loader.getController();
        initializeController(controller);

        // Настраиваем и показываем сцену
        configurePrimaryStage(scene);
        primaryStage.show();
        logger.info("Главный экран успешно загружен и отображен");
    }

    /**
     * Загружает FXML ресурс.
     *
     * @return URL ресурса
     * @throws IOException если файл не найден
     */
    private URL loadFxmlResource() throws IOException {
        URL fxmlLocation = HelloApplication.class.getResource(MAIN_FXML);
        if (fxmlLocation == null) {
            String errorMsg = FXML_NOT_FOUND_MSG + MAIN_FXML;
            logger.error(errorMsg);
            throw new IOException(errorMsg);
        }
        logger.debug("FXML ресурс найден: {}", MAIN_FXML);
        return fxmlLocation;
    }

    /**
     * Инициализирует контроллер главного окна.
     *
     * @param controller контроллер для инициализации
     */
    private void initializeController(MainController controller) {
        Objects.requireNonNull(controller, "MainController не может быть null");
        controller.setExcelFileManager(excelFileManager);
        controller.setMainService(mainService);
        controller.setPrimaryStage(primaryStage);
        logger.debug("MainController инициализирован со всеми сервисами");
    }

    /**
     * Настраивает основное окно приложения.
     *
     * @param scene сцена приложения
     */
    private void configurePrimaryStage(Scene scene) {
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(WINDOW_WIDTH);
        primaryStage.setMinHeight(WINDOW_HEIGHT);
        logger.debug("Основное окно настроено: {} x {}", WINDOW_WIDTH, WINDOW_HEIGHT);
    }

    /**
     * Показывает диалог с ошибкой при запуске.
     *
     * @param message сообщение
     */
    private void showErrorAlert(String message) {
        logger.warn("Показ алерта: {} - {}", ERROR_TITLE, message);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(ERROR_TITLE);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}