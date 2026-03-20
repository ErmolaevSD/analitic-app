package ru.project.analitic.util;

import java.io.File;
import java.util.List;

/**
 * Утилита для валидации входных данных.
 *
 * <p>Предоставляет методы для проверки корректности данных,
 * файлов и других параметров приложения.</p>
 *
 * @author ErmolaevSD
 * @version 2.1
 */
public class ValidationUtil {
    
    private static final long MAX_FILE_SIZE = 100_000_000; // 100 MB
    
    /**
     * Проверяет, существует ли файл и его можно прочитать.
     *
     * @param file файл для проверки
     * @return true если файл существует и доступен для чтения
     */
    public static boolean isFileValid(File file) {
        if (file == null) {
            return false;
        }
        return file.exists() && file.isFile() && file.canRead();
    }
    
    /**
     * Проверяет размер файла.
     *
     * @param file файл для проверки
     * @return true если размер файла в допустимых пределах
     */
    public static boolean isFileSizeValid(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        return file.length() <= MAX_FILE_SIZE;
    }
    
    /**
     * Проверяет расширение файла.
     *
     * @param fileName имя файла
     * @param extensions допустимые расширения (например, ".xlsx", ".xls")
     * @return true если расширение допустимо
     */
    public static boolean isFileExtensionValid(String fileName, String... extensions) {
        if (fileName == null || fileName.isEmpty()) {
            return false;
        }
        
        String lowerFileName = fileName.toLowerCase();
        for (String ext : extensions) {
            if (lowerFileName.endsWith(ext)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Проверяет, не пуст ли список.
     *
     * @param list список для проверки
     * @return true если список содержит элементы
     */
    public static boolean isListNotEmpty(List<?> list) {
        return list != null && !list.isEmpty();
    }
    
    /**
     * Проверяет строку на пустоту.
     *
     * @param str строка для проверки
     * @return true если строка не null и не пустая
     */
    public static boolean isStringValid(String str) {
        return str != null && !str.trim().isEmpty();
    }
}

