package ru.project.analitic.fileManager;

import java.util.List;

/**
 * Интерфейс для работы с файлами данных.
 *
 * <p>Определяет контракт для всех менеджеров файлов, обеспечивая
 * единообразный способ работы с различными форматами файлов.</p>
 *
 * @author ErmolaevSD
 * @version 2.1
 */
public interface IFileManager {
    
    /**
     * Читает данные из файла.
     *
     * @param <T>    тип объектов для чтения
     * @param path   путь к файлу
     * @param tClass класс для преобразования
     * @return список прочитанных объектов
     */
    <T> List<T> readFile(String path, Class<T> tClass);
    
    /**
     * Записывает данные в файл.
     *
     * @param <T>         тип объектов
     * @param dataList    список объектов для записи
     * @param fileName    имя файла
     * @param entityClass класс сущности
     */
    <T> void writeFile(List<T> dataList, String fileName, Class<T> entityClass);
}

