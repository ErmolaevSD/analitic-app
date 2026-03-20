package ru.project.analitic.util;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Утилита для управления диалогами приложения.
 *
 * <p>Предоставляет единообразный способ отображения различных типов диалогов.</p>
 *
 * @author ErmolaevSD
 * @version 2.1
 */
public class DialogUtil {
    
    private static final Logger logger = LoggerFactory.getLogger(DialogUtil.class);
    
    private static Stage ownerStage;
    
    /**
     * Устанавливает родительское окно для диалогов.
     *
     * @param stage родительское окно
     */
    public static void setOwnerStage(Stage stage) {
        ownerStage = stage;
    }
    
    /**
     * Показывает диалог с ошибкой.
     *
     * @param title заголовок диалога
     * @param message сообщение об ошибке
     */
    public static void showError(String title, String message) {
        logger.warn("Показ диалога ошибки: {} - {}", title, message);
        showAlert(Alert.AlertType.ERROR, title, message);
    }
    
    /**
     * Показывает информационный диалог.
     *
     * @param title заголовок диалога
     * @param message информационное сообщение
     */
    public static void showInfo(String title, String message) {
        logger.info("Показ информационного диалога: {} - {}", title, message);
        showAlert(Alert.AlertType.INFORMATION, title, message);
    }
    
    /**
     * Показывает диалог с предупреждением.
     *
     * @param title заголовок диалога
     * @param message сообщение предупреждения
     */
    public static void showWarning(String title, String message) {
        logger.warn("Показ диалога предупреждения: {} - {}", title, message);
        showAlert(Alert.AlertType.WARNING, title, message);
    }
    
    /**
     * Показывает подтверждающий диалог.
     *
     * @param title заголовок диалога
     * @param message сообщение подтверждения
     * @return true если пользователь нажал OK
     */
    public static boolean showConfirmation(String title, String message) {
        logger.debug("Показ диалога подтверждения: {} - {}", title, message);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        if (ownerStage != null) {
            alert.initOwner(ownerStage);
        }
        
        return alert.showAndWait()
                .filter(response -> response == javafx.scene.control.ButtonType.OK)
                .isPresent();
    }
    
    /**
     * Универсальный метод для показа диалогов.
     *
     * @param alertType тип диалога
     * @param title заголовок
     * @param message сообщение
     */
    private static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        if (ownerStage != null) {
            alert.initOwner(ownerStage);
        }
        
        alert.showAndWait();
    }
}

