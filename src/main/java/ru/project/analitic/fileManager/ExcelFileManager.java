package ru.project.analitic.fileManager;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.project.analitic.config.AppConfig;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Сервис для работы с Excel файлами с использованием библиотеки EasyExcel.
 *
 * <p>Реализует интерфейс IFileManager для работы с Excel файлами
 * с поддержкой типизированных данных и автоматической обработкой ошибок.</p>
 *
 * @author ErmolaevSD
 * @version 2.1
 */
public class ExcelFileManager implements IFileManager {

    private static final Logger logger = LoggerFactory.getLogger(ExcelFileManager.class);
    private static final String RESULTS_DIRECTORY = "рeзультаты";
    private static final String DEFAULT_SHEET_NAME = "Persons";

    // Константы для логирования
    private static final String LOG_READING_FILE = "Чтение Excel файла: {}";
    private static final String LOG_FILE_READ_SUCCESS = "Успешно прочитано {} записей из файла: {}";
    private static final String LOG_WRITING_FILE = "Запись {} записей в файл: {}";
    private static final String LOG_FILE_WRITE_SUCCESS = "Файл успешно сохранен: {}";
    private static final String LOG_CREATING_DIRECTORY = "Создание директории результатов: {}";
    private static final String LOG_EXCEL_ERROR = "Ошибка формата Excel файла: {}";
    private static final String LOG_FILE_NOT_FOUND = "Файл не найден: {}";
    private static final String LOG_FILE_NOT_READABLE = "Нет прав на чтение файла: {}";
    private static final String LOG_VALIDATION_ERROR = "Ошибка валидации параметров";

    private static final String[] SUPPORTED_EXTENSIONS = {".xlsx", ".xls"};

    @Override
    public <T> List<T> readFile(String path, Class<T> tClass) {
        logger.info(LOG_READING_FILE, path);
        validateInputParameters(path, tClass);
        validateFileExtension(path);

        List<T> resultList = new ArrayList<>();

        try {
            validateFileExists(path);
            logger.debug("Начало анализа Excel файла: {}", path);

            EasyExcel.read(path, tClass, new AnalysisEventListener<T>() {
                @Override
                public void invoke(T data, AnalysisContext analysisContext) {
                    if (data != null) {
                        resultList.add(data);
                    }
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                    logger.debug("Завершен анализ Excel файла");
                }
            }).sheet().doRead();

            logger.info(LOG_FILE_READ_SUCCESS, resultList.size(), path);
            return resultList;

        } catch (ExcelAnalysisException e) {
            logger.error(LOG_EXCEL_ERROR, path, e);
            throw new RuntimeException("Ошибка формата Excel файла: " + path, e);
        } catch (FileNotFoundException e) {
            logger.error(LOG_FILE_NOT_FOUND, path, e);
            throw new RuntimeException("Файл не найден: " + path, e);
        } catch (Exception e) {
            logger.error("Ошибка при чтении файла: {}", path, e);
            throw new RuntimeException("Ошибка при чтении файла: " + path, e);
        }
    }

    /**
     * Читает данные из Excel файла (для обратной совместимости с старым кодом).
     */
    public <T> List<T> readExcel(String path, Class<T> tClass) {
        return readFile(path, tClass);
    }

    @Override
    public <T> void writeFile(List<T> dataList, String fileName, Class<T> entityClass) {
        logger.info(LOG_WRITING_FILE, dataList.size(), fileName);
        validateWriteParameters(dataList, fileName, entityClass);
        validateFileExtension(fileName);

        try {
            String path = String.valueOf(Paths.get(getDesktopDirectory(), fileName));
            logger.debug("Путь для сохранения файла: {}", path);

            EasyExcel.write(path, entityClass)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet(DEFAULT_SHEET_NAME)
                    .doWrite(dataList);

            logger.info(LOG_FILE_WRITE_SUCCESS, path);

        } catch (Exception e) {
            logger.error("Ошибка при сохранении файла: {}", fileName, e);
            throw new RuntimeException("Не удалось сохранить файл: " + fileName, e);
        }
    }

    /**
     * Записывает данные в Excel файл (для обратной совместимости с старым кодом).
     */
    public <T> void writeToExcel(List<T> dataList, String fileName, Class<T> entityClass) {
        writeFile(dataList, fileName, entityClass);
    }

    private String getDesktopDirectory() {
        logger.debug("Определение директории рабочего стола");
        String userHome = System.getProperty("user.home");
        Path desktopPath = Paths.get(userHome, "Desktop", RESULTS_DIRECTORY);

        try {
            Files.createDirectories(desktopPath);
            logger.info(LOG_CREATING_DIRECTORY, desktopPath);
        } catch (IOException e) {
            logger.error("Не удалось создать директорию результатов: {}", desktopPath, e);
            throw new RuntimeException("Не удалось создать директорию на рабочем столе", e);
        }

        logger.debug("Директория результатов: {}", desktopPath);
        return desktopPath.toString();
    }

    private void validateFileExists(String filePath) throws FileNotFoundException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            logger.warn(LOG_FILE_NOT_FOUND, filePath);
            throw new FileNotFoundException("Файл не найден: " + filePath);
        }
        if (!Files.isReadable(path)) {
            logger.warn(LOG_FILE_NOT_READABLE, filePath);
            throw new RuntimeException("Нет прав на чтение файла: " + filePath);
        }
        logger.debug("Файл существует и доступен для чтения: {}", filePath);
    }

    private <T> void validateInputParameters(String path, Class<T> tClass) {
        if (path == null || path.trim().isEmpty()) {
            logger.error(LOG_VALIDATION_ERROR + ": путь к файлу пустой");
            throw new IllegalArgumentException("Путь к файлу не может быть пустым");
        }
        Objects.requireNonNull(tClass, "Класс для преобразования не может быть null");
        logger.debug("Входные параметры валидированы: путь={}, класс={}", path, tClass.getSimpleName());
    }

    private <T> void validateWriteParameters(List<T> dataList, String fileName, Class<T> entityClass) {
        Objects.requireNonNull(dataList, "Список данных не может быть null");
        if (dataList.isEmpty()) {
            logger.warn(LOG_VALIDATION_ERROR + ": список данных пустой");
            throw new IllegalArgumentException("Список данных не может быть пустым");
        }
        if (fileName == null || fileName.trim().isEmpty()) {
            logger.error(LOG_VALIDATION_ERROR + ": имя файла пустое");
            throw new IllegalArgumentException("Имя файла не может быть пустым");
        }
        Objects.requireNonNull(entityClass, "Класс сущности не может быть null");
        logger.debug("Параметры записи валидированы: файл={}, записей={}, класс={}",
                fileName, dataList.size(), entityClass.getSimpleName());
    }

    private void validateFileExtension(String fileName) {
        boolean isValidExtension = false;
        for (String extension : SUPPORTED_EXTENSIONS) {
            if (fileName.toLowerCase().endsWith(extension)) {
                isValidExtension = true;
                break;
            }
        }
        if (!isValidExtension) {
            logger.error("Файл должен иметь расширение .xlsx или .xls: {}", fileName);
            throw new IllegalArgumentException(String.format("Файл должен иметь расширение .xlsx или .xls: %s", fileName));
        }
        logger.debug("Расширение файла валидировано: {}", fileName);
    }
}

