package ru.project.analitic.fileManager;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Сервис для работы с Excel файлами с использованием библиотеки EasyExcel.
 *
 * <p>Предоставляет функциональность для чтения и записи Excel файлов
 * с поддержкой типизированных данных и автоматической обработкой ошибок.</p>
 *
 * <p>Основные возможности:</p>
 * <ul>
 *   <li>Чтение Excel файлов в список объектов заданного класса</li>
 *   <li>Запись списка объектов в Excel файл с автоматическим подбором ширины колонок</li>
 *   <li>Логирование процесса чтения/записи</li>
 *   <li>Обработка ошибок с информативными сообщениями</li>
 * </ul>
 *
 * @author ErmolaevSD
 * @version 1.0
 * @see com.alibaba.excel.EasyExcel
 * @see com.alibaba.excel.event.AnalysisEventListener
 */
public class ExcelFileManager {

    private static final String RESULTS_DIRECTORY = "результаты";
    private static final String DEFAULT_SHEET_NAME = "Persons";

    /**
     * Читает данные из Excel файла и преобразует их в список объектов указанного класса.
     *
     * <p>Метод автоматически определяет структуру Excel файла на основе аннотаций
     * в классе {@code tClass} (например, {@link com.alibaba.excel.annotation.ExcelProperty}).</p>
     *
     * <p>Особенности чтения:</p>
     * <ul>
     *   <li>Читается первый лист книги Excel</li>
     *   <li>Поддерживаются форматы .xls и .xlsx</li>
     *   <li>Пустые строки игнорируются</li>
     *   <li>Ошибки преобразования типов логируются, но не прерывают чтение</li>
     * </ul>
     *
     * @param <T>    тип объектов, в которые преобразуются строки Excel
     * @param path   путь к Excel файлу
     * @param tClass класс, соответствующий структуре данных в Excel
     * @return список объектов типа {@code T}, прочитанных из файла.
     * Возвращает пустой список, если файл не содержит данных
     * @throws IllegalArgumentException если путь пустой или класс не указан
     * @throws ExcelAnalysisException   если возникла ошибка при анализе Excel файла
     * @throws RuntimeException         если файл не найден или произошла ошибка ввода/вывода
     *
     */
    public <T> List<T> readExcel(String path, Class<T> tClass) {
        validateInputParameters(path, tClass);

        List<T> resultList = new ArrayList<>();

        try {
            validateFileExists(path);

            EasyExcel.read(path, tClass, new AnalysisEventListener<T>() {
                @Override
                public void invoke(T data, AnalysisContext analysisContext) {
                    if (data != null) {
                        resultList.add(data);
                    }
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext analysisContext) {
                }

            }).sheet().doRead();

            return resultList;

        } catch (ExcelAnalysisException e) {
            throw new RuntimeException("Ошибка формата Excel файла: " + path, e);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при чтении файла: " + path, e);
        }
    }

    /**
     * Записывает список объектов в Excel файл.
     *
     * <p>Метод создает новый Excel файл или перезаписывает существующий.
     * Автоматически применяет следующие настройки:</p>
     * <ul>
     *   <li>Автоматический подбор ширины колонок на основе содержимого</li>
     *   <li>Использование аннотаций из класса для форматирования</li>
     *   <li>Создание листа с именем "Persons"</li>
     *   <li>Сохранение в директорию "результаты" относительно корня приложения</li>
     * </ul>
     *
     * @param <T>         тип объектов для записи
     * @param dataList    список объектов для записи (не может быть null)
     * @param fileName    имя выходного файла (например, "report.xlsx")
     * @param entityClass класс объектов, определяющий структуру Excel
     * @throws IllegalArgumentException если список пуст или fileName не указан
     * @throws RuntimeException         при ошибках записи или создании директорий
     */
    public <T> void writeToExcel(List<T> dataList, String fileName, Class<T> entityClass) {
        validateWriteParameters(dataList, fileName, entityClass);

        try {

            String path = String.valueOf(Paths.get(getDesktopDirectory(), fileName));

            EasyExcel.write(path, entityClass)
                    .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                    .sheet(DEFAULT_SHEET_NAME)
                    .doWrite(dataList);

        } catch (Exception e) {
            throw new RuntimeException("Не удалось сохранить файл: " + fileName, e);
        }
    }

    private String getDesktopDirectory() {
        String osName = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");
        Path desktopPath;

        if (osName.contains("win")) {
            // Windows - рабочий стол обычно находится по пути:
            // C:\Users\ИмяПользователя\Desktop
            desktopPath = Paths.get(userHome, "Desktop", RESULTS_DIRECTORY);

        } else if (osName.contains("mac")) {
            // macOS - рабочий стол:
            // /Users/ИмяПользователя/Desktop
            desktopPath = Paths.get(userHome, "Desktop", RESULTS_DIRECTORY);

        } else {
            // Linux - может быть по-разному, но чаще всего:
            // /home/ИмяПользователя/Desktop
            // или /home/ИмяПользователя/Рабочий стол
            desktopPath = Paths.get(userHome, "Desktop", RESULTS_DIRECTORY);

            // Проверяем, существует ли папка "Desktop"
            if (!Files.exists(desktopPath)) {
                // Если нет, пробуем "Рабочий стол" (для русской локализации)
                Path russianDesktop = Paths.get(userHome, "Рабочий стол", RESULTS_DIRECTORY);
                if (Files.exists(russianDesktop.getParent())) {
                    desktopPath = russianDesktop;
                } else {
                    // Если ничего не найдено, создаем на "Desktop"
                    desktopPath = Paths.get(userHome, "Desktop", RESULTS_DIRECTORY);
                }
            }
        }

        try {
            Files.createDirectories(desktopPath);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать директорию на рабочем столе", e);
        }

        return desktopPath.toString();
    }

    /**
     * Проверяет существование файла.
     *
     * @param filePath путь к файлу
     * @throws FileNotFoundException если файл не существует
     */
    private void validateFileExists(String filePath) throws FileNotFoundException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new FileNotFoundException("Файл не найден: " + filePath);
        }
        if (!Files.isReadable(path)) {
            throw new RuntimeException("Нет прав на чтение файла: " + filePath);
        }
    }

    /**
     * Проверяет входные параметры для метода чтения.
     *
     * @param path   путь к файлу
     * @param tClass класс для преобразования
     * @throws IllegalArgumentException если параметры некорректны
     */
    private <T> void validateInputParameters(String path, Class<T> tClass) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Путь к файлу не может быть пустым");
        }
        if (tClass == null) {
            throw new IllegalArgumentException("Класс для преобразования не может быть null");
        }
    }

    /**
     * Проверяет параметры для метода записи.
     *
     * @param dataList    список данных
     * @param fileName    имя файла
     * @param entityClass класс сущности
     * @throws IllegalArgumentException если параметры некорректны
     */
    private <T> void validateWriteParameters(List<T> dataList, String fileName, Class<T> entityClass) {
        if (dataList == null) {
            throw new IllegalArgumentException("Список данных не может быть null");
        }
        if (fileName == null || fileName.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя файла не может быть пустым");
        }
        if (entityClass == null) {
            throw new IllegalArgumentException("Класс сущности не может быть null");
        }
    }
}