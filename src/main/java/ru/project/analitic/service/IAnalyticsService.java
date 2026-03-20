package ru.project.analitic.service;

import ru.project.analitic.model.AdmPerson;
import java.util.List;

/**
 * Интерфейс для аналитического сервиса.
 *
 * <p>Определяет контракт для всех операций аналитики и сверки данных.</p>
 *
 * @author ErmolaevSD
 * @version 2.1
 */
public interface IAnalyticsService {
    
    /**
     * Выполняет проверку 116 часть 1.
     *
     * @param admPersonList список записей
     */
    void sverka116PathOne(List<AdmPerson> admPersonList);
    
    /**
     * Находит дубликаты между двумя файлами.
     *
     * @param <T>         тип объектов
     * @param firstList   первый список
     * @param secondList  второй список
     * @param entityClass класс объектов
     */
    <T> void duplicateInTwoFiles(List<T> firstList, List<T> secondList, Class<T> entityClass);
    
    /**
     * Находит дубликаты в одном файле.
     *
     * @param <T>         тип объектов
     * @param dataList    список объектов
     * @param entityClass класс объектов
     * @param writeToFile сохранить результаты в файл
     * @return список найденных дубликатов
     */
    <T> List<T> duplicateInOneFile(List<T> dataList, Class<T> entityClass, boolean writeToFile);
}

