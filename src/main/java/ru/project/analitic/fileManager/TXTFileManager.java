package ru.project.analitic.fileManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * Сервис для чтения и обработки текстовых файлов специального формата.
 *
 * <p>Класс выполняет разбивку содержимого текстового файла на логические блоки
 * (листы) на основе маркеров в тексте, что позволяет структурировать
 * большие текстовые документы и обрабатывать их по частям.</p>
 *
 * <p>Формат файла:</p>
 * <pre>
 * ЛИСТ 1                    (маркер начала нового листа)
 * данные:поля:значения      (строки данных, разделенные на части по ":")
 * ...
 * \f                         (маркер конца листа - символ перевода страницы)
 * ЛИСТ 2
 * ...
 * </pre>
 *
 * @author ErmolaevSD
 * @version 2.0
 */
public class TXTFileManager {

    private static final Logger logger = LoggerFactory.getLogger(TXTFileManager.class);

    // Константы для маркеров
    private static final String SHEET_MARKER = "ЛИСТ";
    private static final String PAGE_BREAK_MARKER = "\f";
    private static final String FIELD_SEPARATOR = ":";
    private static final String SHEET_NAME_FORMAT = "Лист %d";

    // Константы для логирования
    private static final String LOG_READING_FILE = "Чтение текстового файла: {}";
    private static final String LOG_FILE_READ_SUCCESS = "Успешно прочитано {} листов из файла: {}";
    private static final String LOG_SHEET_CREATED = "Создан новый лист: {}";
    private static final String LOG_PROCESSING_LINE = "Обработана строка: {}";
    private static final String LOG_FILE_NOT_FOUND = "Текстовый файл не найден: {}";
    private static final String LOG_VALIDATION_ERROR = "Ошибка валидации параметров";

    /**
     * Читает текстовый файл и разбивает его содержимое на листы.
     *
     * <p>Метод выполняет следующие операции:</p>
     * <ol>
     *   <li>Читает все строки из файла с использованием кодировки UTF-8</li>
     *   <li>Разделяет содержимое на листы на основе маркеров:
     *     <ul>
     *       <li>Строки, содержащие "ЛИСТ" - маркер начала нового листа</li>
     *       <li>Строки, содержащие "\f" (символ перевода страницы) - маркер конца текущего листа</li>
     *     </ul>
     *   </li>
     *   <li>Каждая строка внутри листа разбивается на части по символу ":"</li>
     *   <li>Результат сохраняется в карту с автоматической нумерацией листов</li>
     * </ol>
     *
     * <p>Особенности работы:</p>
     * <ul>
     *   <li>Сохраняет порядок листов (используется LinkedHashMap)</li>
     *   <li>Нумерация листов начинается с 1</li>
     *   <li>Строка с маркером конца листа ("\f") исключается из результата</li>
     *   <li>Все операции логируются на разных уровнях (DEBUG, INFO, ERROR)</li>
     *   <li>Полная обработка ошибок с контекстом</li>
     * </ul>
     *
     * @param pathName путь к файлу для чтения (не может быть null или пустым)
     * @return карта (LinkedHashMap для сохранения порядка), где:
     * - ключ: строка с номером листа (формат: "Лист N")
     * - значение: список массивов строк, где каждый массив представляет
     * строку файла, разбитую по разделителю ":"
     * Возвращает пустую карту, если файл не содержит листов
     * @throws IllegalArgumentException если путь null или пустой
     * @throws RuntimeException         если возникает ошибка при чтении файла
     */
    public Map<String, List<String[]>> readTXT(String pathName) {
        logger.info(LOG_READING_FILE, pathName);
        validateFilePath(pathName);

        Map<String, List<String[]>> listMap = new LinkedHashMap<>();
        List<String[]> buffer = new ArrayList<>();
        int sheetCount = 1;

        try {
            Path filePath = Paths.get(pathName);
            validateFileExists(filePath);
            logger.debug("Начало обработки текстового файла: {}", pathName);

            try (Stream<String> lines = Files.lines(filePath, StandardCharsets.UTF_8)) {
                Iterator<String> iterator = lines.iterator();

                while (iterator.hasNext()) {
                    String line = iterator.next();
                    logger.debug(LOG_PROCESSING_LINE, line.length() > 50 ? line.substring(0, 50) + "..." : line);

                    if (line.contains(SHEET_MARKER)) {
                        // Сохраняем предыдущий буфер, если он не пуст
                        if (!buffer.isEmpty()) {
                            saveSheetToMap(listMap, buffer, sheetCount++);
                            buffer.clear();
                        }
                        // Добавляем строку маркера в новый буфер
                        addLineToBuffer(buffer, line);

                    } else if (line.contains(PAGE_BREAK_MARKER)) {
                        // Удаляем строку с маркером конца листа (символ \f)
                        if (!buffer.isEmpty()) {
                            buffer.removeLast();
                            saveSheetToMap(listMap, buffer, sheetCount++);
                            buffer.clear();
                        }

                    } else if (!line.trim().isEmpty()) {
                        // Добавляем обычную строку данных
                        addLineToBuffer(buffer, line);
                    }
                }

                // Сохраняем оставшиеся данные, если они есть
                if (!buffer.isEmpty()) {
                    saveSheetToMap(listMap, buffer, sheetCount);
                    logger.debug(LOG_SHEET_CREATED, String.format(SHEET_NAME_FORMAT, sheetCount));
                }
            }

            logger.info(LOG_FILE_READ_SUCCESS, listMap.size(), pathName);
            return listMap;

        } catch (IOException e) {
            logger.error(LOG_FILE_NOT_FOUND, pathName, e);
            throw new RuntimeException("Ошибка при чтении текстового файла: " + pathName, e);
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при обработке файла: {}", pathName, e);
            throw new RuntimeException("Ошибка при обработке текстового файла: " + pathName, e);
        }
    }

    /**
     * Сохраняет буфер в карту листов.
     *
     * @param listMap     карта для сохранения
     * @param buffer      буфер данных
     * @param sheetNumber номер листа
     */
    private void saveSheetToMap(Map<String, List<String[]>> listMap, List<String[]> buffer, int sheetNumber) {
        String sheetName = String.format(SHEET_NAME_FORMAT, sheetNumber);
        listMap.put(sheetName, new ArrayList<>(buffer));
        logger.debug(LOG_SHEET_CREATED, sheetName);
    }

    /**
     * Добавляет строку в буфер, разбивая её по разделителю.
     *
     * @param buffer буфер для добавления
     * @param line   строка для добавления
     */
    private void addLineToBuffer(List<String[]> buffer, String line) {
        String[] parts = line.split(FIELD_SEPARATOR, -1); // -1 для сохранения пустых полей в конце
        buffer.add(parts);
        logger.debug("Добавлено {} полей в буфер", parts.length);
    }

    /**
     * Проверяет корректность пути к файлу.
     *
     * @param filePath путь к файлу
     * @throws IllegalArgumentException если путь null или пустой
     */
    private void validateFilePath(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            logger.error(LOG_VALIDATION_ERROR + ": путь к файлу пустой");
            throw new IllegalArgumentException("Путь к файлу не может быть null или пустым");
        }
        logger.debug("Путь к файлу валидирован: {}", filePath);
    }

    /**
     * Проверяет существование файла.
     *
     * @param filePath путь к файлу
     * @throws RuntimeException если файл не существует или не читаемый
     */
    private void validateFileExists(Path filePath) {
        if (!Files.exists(filePath)) {
            logger.warn(LOG_FILE_NOT_FOUND, filePath);
            throw new RuntimeException("Файл не найден: " + filePath);
        }
        if (!Files.isReadable(filePath)) {
            logger.warn("Нет прав на чтение файла: {}", filePath);
            throw new RuntimeException("Нет прав на чтение файла: " + filePath);
        }
        logger.debug("Файл существует и доступен для чтения: {}", filePath);
    }
}