package ru.project.analitic.util;

import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Утилита для выполнения асинхронных операций с UI.
 *
 * <p>Предоставляет методы для выполнения длительных операций в отдельных потоках,
 * чтобы не блокировать UI поток приложения.</p>
 *
 * @author ErmolaevSD
 * @version 2.1
 */
public class AsyncTaskExecutor {
    
    private static final Logger logger = LoggerFactory.getLogger(AsyncTaskExecutor.class);
    private static final ExecutorService executorService = Executors.newFixedThreadPool(4);
    
    /**
     * Выполняет задачу асинхронно.
     *
     * @param <V>  тип результата
     * @param task задача для выполнения
     * @return объект Task для отслеживания выполнения
     */
    public static <V> Task<V> execute(AsyncTask<V> task) {
        logger.debug("Запуск асинхронной задачи: {}", task.getName());
        
        return new Task<V>() {
            @Override
            protected V call() throws Exception {
                try {
                    updateMessage("Выполнение: " + task.getName());
                    updateProgress(0, 100);
                    
                    V result = task.execute();
                    
                    updateProgress(100, 100);
                    updateMessage("Завершено: " + task.getName());
                    
                    return result;
                } catch (Exception e) {
                    logger.error("Ошибка при выполнении асинхронной задачи", e);
                    throw e;
                }
            }
        };
    }
    
    /**
     * Выполняет операцию с обработкой результата.
     *
     * @param <V>               тип результата
     * @param task              задача для выполнения
     * @param onSuccess         обработчик успеха
     * @param onFailure         обработчик ошибки
     */
    public static <V> void executeWithCallback(AsyncTask<V> task, 
                                               SuccessCallback<V> onSuccess,
                                               FailureCallback onFailure) {
        logger.debug("Запуск асинхронной задачи с callback: {}", task.getName());
        
        Task<V> javafxTask = execute(task);
        
        javafxTask.setOnSucceeded(event -> {
            logger.debug("Задача успешно завершена");
            V result = javafxTask.getValue();
            onSuccess.onSuccess(result);
        });
        
        javafxTask.setOnFailed(event -> {
            logger.error("Ошибка при выполнении задачи", javafxTask.getException());
            onFailure.onFailure(javafxTask.getException());
        });
        
        executorService.execute(javafxTask);
    }
    
    /**
     * Завершает работу executor service.
     */
    public static void shutdown() {
        logger.info("Остановка AsyncTaskExecutor");
        executorService.shutdown();
    }
    
    /**
     * Интерфейс для асинхронной задачи.
     *
     * @param <V> тип результата
     */
    @FunctionalInterface
    public interface AsyncTask<V> {
        V execute() throws Exception;
        
        default String getName() {
            return this.getClass().getSimpleName();
        }
    }
    
    /**
     * Интерфейс для обработки успеха.
     *
     * @param <V> тип результата
     */
    @FunctionalInterface
    public interface SuccessCallback<V> {
        void onSuccess(V result);
    }
    
    /**
     * Интерфейс для обработки ошибок.
     */
    @FunctionalInterface
    public interface FailureCallback {
        void onFailure(Throwable throwable);
    }
}

