package ru.project.analitic.controller;

import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Вспомогательный класс для создания асинхронных задач чтения файлов.
 *
 * @author ErmolaevSD
 * @version 2.1
 */
public class FileReadTask<T> extends Task<java.util.List<T>> {
    
    private static final Logger logger = LoggerFactory.getLogger(FileReadTask.class);
    private final String filePath;
    private final Class<T> tClass;
    private final ru.project.analitic.fileManager.IFileManager fileManager;
    
    public FileReadTask(String filePath, Class<T> tClass, ru.project.analitic.fileManager.IFileManager fileManager) {
        this.filePath = filePath;
        this.tClass = tClass;
        this.fileManager = fileManager;
    }
    
    @Override
    protected java.util.List<T> call() throws Exception {
        logger.debug("Начало асинхронного чтения файла: {}", filePath);
        updateMessage("Чтение файла: " + filePath);
        updateProgress(0, 100);
        
        try {
            java.util.List<T> result = fileManager.readFile(filePath, tClass);
            updateProgress(100, 100);
            updateMessage("Файл успешно прочитан");
            return result;
        } catch (Exception e) {
            logger.error("Ошибка при асинхронном чтении файла", e);
            throw e;
        }
    }
}

