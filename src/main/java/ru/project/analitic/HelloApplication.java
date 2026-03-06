package ru.project.analitic;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import ru.project.analitic.controller.MainController;
import ru.project.analitic.fileManager.ExcelFileManager;
import ru.project.analitic.fileManager.TXTFileManager;
import ru.project.analitic.service.MainService;

import java.io.IOException;
import java.net.URL;

/**
 * Главный класс приложения для аналитики данных.
 *
 * <p>Отвечает за инициализацию и запуск JavaFX приложения,
 * настройку сервисов и отображение главного окна.</p>
 *
 * @author ErmolaevSD
 * @version 1.0
 */
public class HelloApplication extends Application {

    // Константы для FXML
    private static final String MAIN_FXML = "main-view.fxml";
    private static final String APP_TITLE = "Сверка-УОДУУП";
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private Stage primaryStage;
    private MainService mainService;
    private TXTFileManager txtFileManager;
    private ExcelFileManager excelFileManager;

    /**
     * Инициализирует сервисы приложения перед запуском.
     *
     * <p>Создает экземпляры менеджеров файлов и основного сервиса.</p>
     */
    @Override
    public void init() {

        try {
            excelFileManager = new ExcelFileManager();
            txtFileManager = new TXTFileManager();
            mainService = new MainService(excelFileManager, txtFileManager);

        } catch (Exception e) {
            throw new RuntimeException("Не удалось инициализировать приложение", e);
        }
    }

    /**
     * Запускает JavaFX приложение и отображает главное окно.
     *
     * @param stage главная сцена приложения
     */
    @Override
    public void start(Stage stage) {

        this.primaryStage = stage;

        try {
            showMainScreen();
        } catch (Exception e) {
            showErrorAlert("Ошибка запуска", "Не удалось запустить приложение: " + e.getMessage());
        }
    }

    /**
     * Отображает главный экран приложения.
     *
     * @throws IOException если не удается загрузить FXML файл
     */
    private void showMainScreen() throws IOException {

        // Загружаем FXML
        URL fxmlLocation = HelloApplication.class.getResource(MAIN_FXML);
        if (fxmlLocation == null) {
            String errorMsg = "FXML файл не найден: " + MAIN_FXML;
            throw new IOException(errorMsg);
        }

        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Scene scene = new Scene(loader.load(), WINDOW_WIDTH, WINDOW_HEIGHT);

        MainController controller = loader.getController();
        controller.setExcelFileManager(excelFileManager);
        controller.setMainService(mainService);
        controller.setPrimaryStage(primaryStage);

        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(WINDOW_WIDTH);
        primaryStage.setMinHeight(WINDOW_HEIGHT);

        primaryStage.show();
    }

    /**
     * Показывает диалог с ошибкой при запуске.
     *
     * @param title   заголовок
     * @param message сообщение
     */
    private void showErrorAlert(String title, String message) {
        javafx.scene.control.Alert alert =
                new javafx.scene.control.Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}