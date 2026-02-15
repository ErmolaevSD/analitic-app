package ru.project.analitic.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.project.analitic.model.AdmPerson;
import ru.project.analitic.model.Person;
import ru.project.analitic.service.MainService;
import ru.project.analitic.fileManager.ExcelFileManager;

import java.io.File;
import java.util.List;

/**
 * Контроллер главного окна приложения.
 *
 * <p>Обрабатывает действия пользователя на главном экране и управляет
 * навигацией между различными функциями приложения.</p>
 *
 * @version 1.0
 * @author
 * @see MainService
 * @see ExcelFileManager
 */
@Slf4j
@Getter
@Setter
public class MainController {

    private ExcelFileManager excelFileManager;
    private MainService mainService;
    private Stage primaryStage;

    // Константы для сообщений
    private static final String SUCCESS_TITLE = "Успех";
    private static final String WARNING_TITLE = "Предупреждение";
    private static final String ERROR_TITLE = "Ошибка";
    private static final String ABOUT_TITLE = "О программе";

    /**
     * Обрабатывает поиск дубликатов между двумя файлами.
     *
     * <p>Пользователь выбирает два Excel файла, после чего выполняется
     * анализ на наличие дублирующихся записей. Результаты сохраняются
     * в отдельные файлы.</p>
     */
    @FXML
    private void duplicateInTwoFiles() {
        log.info("Запущена функция поиска дубликатов в двух файлах");

        File fileOne = openFile("Выберите первый Excel файл");
        if (fileOne == null) {
            log.debug("Пользователь отменил выбор первого файла");
            return;
        }

        File fileTwo = openFile("Выберите второй Excel файл");
        if (fileTwo == null) {
            log.debug("Пользователь отменил выбор второго файла");
            return;
        }

        try {
            log.info("Чтение файла: {}", fileOne.getName());
            List<Person> admPeople = excelFileManager.readExcel(fileOne.getPath(), Person.class);

            log.info("Чтение файла: {}", fileTwo.getName());
            List<Person> admPeople2 = excelFileManager.readExcel(fileTwo.getPath(), Person.class);

            if (isDataEmpty(admPeople) || isDataEmpty(admPeople2)) {
                showWarning(WARNING_TITLE, "Один из файлов не содержит данных или они не распознаны");
                return;
            }

            log.info("Найден дубликатов в первом файле: {}, во втором: {}",
                    admPeople.size(), admPeople2.size());

            mainService.duplicateInTwoFiles(admPeople, admPeople2, Person.class);

            showInfo(SUCCESS_TITLE, "Сверка завершена! Результаты сохранены в папку 'результаты'");

        } catch (Exception e) {
            log.error("Ошибка при обработке файлов", e);
            showError(ERROR_TITLE, "Произошла ошибка: " + e.getMessage());
        }
    }

    /**
     * Обрабатывает поиск дубликатов в одном файле.
     *
     * <p>Пользователь выбирает Excel файл, после чего выполняется
     * анализ на наличие дублирующихся записей внутри файла.</p>
     */
    @FXML
    private void duplicateInOneFiles() {
        log.info("Запущена функция поиска дубликатов в одном файле");

        File fileOne = openFile("Выберите Excel файл для анализа");
        if (fileOne == null) {
            log.debug("Пользователь отменил выбор файла");
            return;
        }

        try {
            log.info("Чтение файла: {}", fileOne.getName());
            List<Person> admPeople = excelFileManager.readExcel(fileOne.getPath(), Person.class);

            if (isDataEmpty(admPeople)) {
                showWarning(WARNING_TITLE, "Файл не содержит данных или они не распознаны");
                return;
            }

            log.info("Найдено записей в файле: {}", admPeople.size());

            List<Person> duplicates = mainService.duplicateInOneFile(admPeople, Person.class, true);

            if (duplicates.isEmpty()) {
                showInfo(SUCCESS_TITLE, "Дубликаты не найдены");
            } else {
                showInfo(SUCCESS_TITLE,
                        String.format("Сверка завершена! Найдено дубликатов: %d", duplicates.size()));
            }

        } catch (Exception e) {
            log.error("Ошибка при обработке файла", e);
            showError(ERROR_TITLE, "Произошла ошибка: " + e.getMessage());
        }
    }

    /**
     * Обрабатывает сверку 116 часть 1.
     *
     * <p>Выполняет анализ временных периодов для выявления проблемных
     * ситуаций с повторными правонарушениями.</p>
     */
    @FXML
    private void sverka116PathOne() {
        log.info("Запущена функция сверки 116 часть 1");

        File file = openFile("Выберите Excel файл для сверки 116");
        if (file == null) {
            log.debug("Пользователь отменил выбор файла");
            return;
        }

        try {
            log.info("Чтение файла: {}", file.getName());
            List<AdmPerson> admPeople = excelFileManager.readExcel(file.getPath(), AdmPerson.class);

            if (isDataEmpty(admPeople)) {
                showWarning(WARNING_TITLE, "Файл не содержит данных или они не распознаны");
                return;
            }

            log.info("Найдено записей для анализа: {}", admPeople.size());

            mainService.sverka116PathOne(admPeople);

            showInfo(SUCCESS_TITLE, "Сверка завершена! Результат сохранен в файл 'Сверка 116_часть_1.xlsx'");

        } catch (Exception e) {
            log.error("Ошибка при обработке файла", e);
            showError(ERROR_TITLE, "Произошла ошибка: " + e.getMessage());
        }
    }

    /**
     * Показывает информацию о программе.
     */
    @FXML
    private void showAbout() {
        log.debug("Открытие окна 'О программе'");

        Alert alertInfo = new Alert(Alert.AlertType.INFORMATION);
        alertInfo.setTitle(ABOUT_TITLE);
        alertInfo.setHeaderText("Аналитика - Онлайн");
        alertInfo.setContentText(
                "Версия 2.0\n\n" +
                        "Приложение для автоматической сверки данных\n" +
                        "Возможности:\n" +
                        "• Поиск дубликатов в одном или двух файлах\n" +
                        "• Сверка по статье 116.1\n" +
                        "• Анализ временных периодов\n\n" +
                        "© ErmolaevSD, 2025"
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

        // Добавляем фильтры для разных типов файлов
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel файлы", "*.xlsx", "*.xls"),
                new FileChooser.ExtensionFilter("Все файлы", "*.*")
        );

        File selectedFile = fileChooser.showOpenDialog(primaryStage);

        if (selectedFile != null) {
            log.info("Выбран файл: {}", selectedFile.getAbsolutePath());
            showInfo("Файл выбран", "Файл успешно загружен: " + selectedFile.getName());
        } else {
            log.debug("Выбор файла отменен");
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
    private void showError(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, message);
    }

    /**
     * Показывает информационный диалог.
     */
    private void showInfo(String title, String message) {
        showAlert(Alert.AlertType.INFORMATION, title, message);
    }

    /**
     * Показывает диалог с предупреждением.
     */
    private void showWarning(String title, String message) {
        showAlert(Alert.AlertType.WARNING, title, message);
    }

    /**
     * Универсальный метод для показа диалогов.
     *
     * @param alertType тип диалога
     * @param title заголовок
     * @param message сообщение
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