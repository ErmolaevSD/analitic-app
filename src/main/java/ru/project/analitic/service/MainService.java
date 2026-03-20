package ru.project.analitic.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.project.analitic.fileManager.ExcelFileManager;
import ru.project.analitic.fileManager.TXTFileManager;
import ru.project.analitic.model.AdmPerson;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;

/**
 * Основной сервис для выполнения аналитических операций над данными.
 *
 * <p>Предоставляет функциональность для:</p>
 * <ul>
 *   <li>Поиска дубликатов в одном или двух файлах</li>
 *   <li>Проверки временных периодов (сверка 116 часть 1)</li>
 *   <li>Группировки и анализа записей</li>
 * </ul>
 *
 * <p>Класс оптимизирован для работы с большими объемами данных и
 * обеспечивает детальное логирование всех операций.</p>
 *
 * @author ErmolaevSD
 * @version 2.1
 * @see ExcelFileManager
 * @see AdmPerson
 * @see IAnalyticsService
 */
public class MainService implements IAnalyticsService {

    private static final Logger logger = LoggerFactory.getLogger(MainService.class);

    private static final String DUPLICATES_TWO_FILES = "Дубликаты.xlsx";
    private static final String UNIQUE_FIRST_FILE = "Уникальные в первом файле.xlsx";
    private static final String UNIQUE_SECOND_FILE = "Уникальные во втором файле.xlsx";
    private static final String DUPLICATES_SINGLE_FILE = "Дубликаты в файле.xlsx";
    private static final String SVERKA_116_PART1_FILE = "Сверка 116_часть_1.xlsx";

    private static final String LOG_DUPLICATES_NOT_FOUND = "Дубликатов не найдено в файле";
    private static final String LOG_DUPLICATES_FOUND = "Найдено {} дубликатов в файле";
    private static final String LOG_RESULTS_SAVED = "Результаты сохранены в файл: {}";
    private static final String LOG_GROUPS_ANALYZED = "Проанализировано {} групп записей";
    private static final String LOG_PROBLEMS_FOUND = "Найдено {} проблемных периодов";

    private final ExcelFileManager excelFileManager;
    @SuppressWarnings({"unused", "FieldCanBeLocal"})
    private final TXTFileManager txtFileManager;

    /**
     * Конструктор сервиса с внедрением зависимости.
     *
     * @param excelFileManager менеджер для работы с Excel файлами (не может быть null)
     * @param txtFileManager   менеджер для работы с TXT файлами (не может быть null)
     * @throws IllegalArgumentException если любой из менеджеров равен null
     */
    public MainService(ExcelFileManager excelFileManager, TXTFileManager txtFileManager) {
        this.excelFileManager = requireNonNull(excelFileManager, "ExcelFileManager не может быть null");
        this.txtFileManager = requireNonNull(txtFileManager, "TXTFileManager не может быть null");
        logger.debug("MainService инициализирован с менеджерами файлов");
    }

    /**
     * Выполняет проверку 116 часть 1 - выявление проблемных периодов в данных.
     *
     * <p>Метод анализирует сгруппированные записи и выявляет периоды,
     * где дата начала следующей записи позже даты окончания предыдущей,
     * что может указывать на проблемы в данных.</p>
     *
     * <p>Алгоритм работы:</p>
     * <ol>
     *   <li>Группирует записи по идентификатору человека</li>
     *   <li>Сортирует записи в каждой группе по дате факта</li>
     *   <li>Сравнивает последовательные записи на предмет разрыва между датами</li>
     *   <li>Сохраняет проблемные записи в файл "Сверка 116_часть_1.xlsx"</li>
     * </ol>
     *
     * @param admPersonList список записей для анализа (не может быть null)
     * @throws IllegalArgumentException если список равен null
     * @throws NullPointerException     если у записей отсутствуют обязательные поля
     */
    public void sverka116PathOne(List<AdmPerson> admPersonList) {
        logger.info("Начало проверки сверки 116 часть 1 для {} записей", admPersonList != null ? admPersonList.size() : 0);

        try {
            validateAdmPersonList(admPersonList);

            Map<AdmPerson, List<AdmPerson>> groupedPersons = groupDuplicate(admPersonList);
            logger.debug(LOG_GROUPS_ANALYZED, groupedPersons.size());

            List<AdmPerson> problematicPeriods = new ArrayList<>();

            for (List<AdmPerson> group : groupedPersons.values()) {
                problematicPeriods.addAll(analyzeGroup(group));
            }

            logger.info(LOG_PROBLEMS_FOUND, problematicPeriods.size());

            if (!problematicPeriods.isEmpty()) {
                saveResultsWithLogging(problematicPeriods, SVERKA_116_PART1_FILE, AdmPerson.class);
            } else {
                logger.info("Проблемные периоды не выявлены");
            }
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка валидации при проверке сверки 116 часть 1", e);
            throw e;
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при проверке сверки 116 часть 1", e);
            throw new RuntimeException("Ошибка при выполнении проверки сверки 116", e);
        }
    }

    /**
     * Находит дубликаты между двумя файлами и записывает результаты в отдельные Excel файлы.
     *
     * <p>Метод выполняет сравнение двух списков объектов и создает три выходных файла:</p>
     * <ul>
     *   <li><b>Дубликаты.xlsx</b> - записи, присутствующие в обоих файлах</li>
     *   <li><b>Уникальные в первом файле.xlsx</b> - записи, уникальные для первого файла</li>
     *   <li><b>Уникальные во втором файле.xlsx</b> - записи, уникальные для второго файла</li>
     * </ul>
     *
     * @param <T>         тип объектов для сравнения
     * @param firstList   список объектов из первого файла (не может быть null)
     * @param secondList  список объектов из второго файла (не может быть null)
     * @param entityClass класс объектов для определения структуры Excel
     * @throws IllegalArgumentException если любой из списков или класс равен null
     */
    public <T> void duplicateInTwoFiles(List<T> firstList,
                                        List<T> secondList,
                                        Class<T> entityClass) {
        logger.info("Начало поиска дубликатов между двумя файлами. Первый файл: {} записей, второй файл: {} записей",
                firstList != null ? firstList.size() : 0,
                secondList != null ? secondList.size() : 0);

        try {
            validateInputLists(firstList, secondList, entityClass);

            // Оптимизация: выбираем меньшее множество для contains
            Set<T> setFirst = new HashSet<>(firstList);
            Set<T> setSecond = new HashSet<>(secondList);
            logger.debug("Множества для сравнения созданы. Размер 1: {}, размер 2: {}", setFirst.size(), setSecond.size());

            // Находим дубликаты (пересечение множеств)
            List<T> duplicates = findDuplicates(secondList, setFirst);
            logger.info(LOG_DUPLICATES_FOUND, duplicates.size());

            // Находим уникальные записи в каждом файле
            List<T> uniqueInFirst = findUnique(firstList, setSecond);
            List<T> uniqueInSecond = findUnique(secondList, setFirst);
            logger.info("Уникальные в первом файле: {}, во втором файле: {}", uniqueInFirst.size(), uniqueInSecond.size());

            // Сохраняем результаты
            Map<String, List<T>> results = Map.of(
                    DUPLICATES_TWO_FILES, duplicates,
                    UNIQUE_FIRST_FILE, uniqueInFirst,
                    UNIQUE_SECOND_FILE, uniqueInSecond
            );

            saveMultipleResults(results, entityClass);
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка валидации при поиске дубликатов в двух файлах", e);
            throw e;
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при поиске дубликатов в двух файлах", e);
            throw new RuntimeException("Ошибка при выполнении анализа дубликатов", e);
        }
    }

    /**
     * Находит дубликаты в одном файле и опционально записывает результаты.
     *
     * <p>Метод анализирует список объектов и выявляет повторяющиеся записи.
     * Дубликатами считаются объекты, которые равны согласно методу {@code equals()}.</p>
     *
     * @param <T>         тип объектов для анализа
     * @param dataList    список объектов для проверки (не может быть null)
     * @param entityClass класс объектов для определения структуры Excel
     * @param writeToFile если {@code true}, результат сохраняется в файл
     * @return список найденных дубликатов (может быть пустым)
     * @throws IllegalArgumentException если список или класс равен null
     */
    public <T> List<T> duplicateInOneFile(List<T> dataList,
                                          Class<T> entityClass,
                                          boolean writeToFile) {
        logger.info("Начало поиска дубликатов в одном файле. Размер списка: {}, сохранить результаты: {}",
                dataList != null ? dataList.size() : 0, writeToFile);

        try {
            validateInputList(dataList, entityClass);

            DuplicateSearchResult<T> result = findDuplicatesOptimized(dataList);

            if (result.duplicates().isEmpty()) {
                logger.info(LOG_DUPLICATES_NOT_FOUND);
            } else {
                logger.info(LOG_DUPLICATES_FOUND, result.duplicates().size());
                if (writeToFile) {
                    saveResultsWithLogging(result.duplicates(), DUPLICATES_SINGLE_FILE, entityClass);
                }
            }

            return result.duplicates();
        } catch (IllegalArgumentException e) {
            logger.error("Ошибка валидации при поиске дубликатов в файле", e);
            throw e;
        } catch (Exception e) {
            logger.error("Неожиданная ошибка при поиске дубликатов в файле", e);
            throw new RuntimeException("Ошибка при выполнении поиска дубликатов", e);
        }
    }

    /**
     * Группирует одинаковых людей в один список с оптимизацией.
     *
     * <p>Для больших списков использует ConcurrentHashMap для возможности
     * параллельной обработки в будущем.</p>
     *
     * @param admPersonList список записей для группировки
     * @return карта, где ключ - человек, значение - список его записей
     */
    private Map<AdmPerson, List<AdmPerson>> groupDuplicate(List<AdmPerson> admPersonList) {
        Map<AdmPerson, List<AdmPerson>> groupedPersons = new HashMap<>();

        for (AdmPerson person : admPersonList) {
            groupedPersons.computeIfAbsent(person, k -> new ArrayList<>()).add(person);
        }

        return groupedPersons;
    }

    /**
     * Анализирует группу записей на наличие временных разрывов.
     *
     * @param group список записей одной группы
     * @return список проблемных записей в группе
     */
    private List<AdmPerson> analyzeGroup(List<AdmPerson> group) {
        if (group.size() < 2) {
            return Collections.emptyList();
        }

        List<AdmPerson> localProblems = new ArrayList<>();

        group.sort(Comparator.comparing(AdmPerson::getDateFact));

        for (int i = 1; i < group.size(); i++) {
            AdmPerson previous = group.get(i - 1);
            AdmPerson current = group.get(i);

            if (hasGap(previous, current)) {
                localProblems.add(previous);
                localProblems.add(current);
            }
        }

        return localProblems;
    }

    /**
     * Проверяет наличие временного разрыва между двумя записями.
     *
     * @param previous предыдущая запись
     * @param current  текущая запись
     * @return true если есть разрыв между периодами
     */
    private boolean hasGap(AdmPerson previous, AdmPerson current) {
        return !isNull(previous.getDateEnd()) &&
                current.getDateFact().isAfter(previous.getDateEnd());
    }

    /**
     * Находит дубликаты в списке с оптимизацией.
     *
     * @param <T>  тип данных
     * @param list список для поиска
     * @param set  множество для проверки
     * @return список дубликатов
     */
    private <T> List<T> findDuplicates(List<T> list, Set<T> set) {
        return list.stream()
                .filter(set::contains)
                .collect(Collectors.toList());
    }

    /**
     * Находит уникальные записи в списке.
     *
     * @param <T>  тип данных
     * @param list список для анализа
     * @param set  множество для проверки
     * @return список уникальных записей
     */
    private <T> List<T> findUnique(List<T> list, Set<T> set) {
        return list.stream()
                .filter(person -> !set.contains(person))
                .collect(Collectors.toList());
    }

    /**
     * Оптимизированный поиск дубликатов в одном файле.
     *
     * @param <T>      тип данных
     * @param dataList список для анализа
     * @return результат поиска с информацией о дубликатах
     */
    private <T> DuplicateSearchResult<T> findDuplicatesOptimized(List<T> dataList) {
        Set<T> uniqueElements = new HashSet<>();
        Set<T> duplicates = new HashSet<>();

        for (T element : dataList) {
            if (!uniqueElements.add(element)) {
                duplicates.add(element);
            }
        }

        return new DuplicateSearchResult<>(new ArrayList<>(duplicates), uniqueElements.size());
    }

    /**
     * Сохраняет несколько результатов в файлы с логированием.
     *
     * @param <T>         тип данных
     * @param results     карта имя файла -> список данных
     * @param entityClass класс сущности
     */
    private <T> void saveMultipleResults(Map<String, List<T>> results, Class<T> entityClass) {
        results.forEach((fileName, data) -> {
            if (!data.isEmpty()) {
                try {
                    excelFileManager.writeToExcel(data, fileName, entityClass);
                    logger.info(LOG_RESULTS_SAVED, fileName);
                } catch (Exception e) {
                    logger.error("Ошибка при сохранении результатов в файл: {}", fileName, e);
                    throw new RuntimeException("Ошибка при сохранении файла: " + fileName, e);
                }
            }
        });
    }

    /**
     * Сохраняет результаты с логированием.
     *
     * @param <T>         тип данных
     * @param data        список данных для сохранения
     * @param fileName    имя файла
     * @param entityClass класс сущности
     */
    private <T> void saveResultsWithLogging(List<T> data, String fileName, Class<T> entityClass) {
        if (data.isEmpty()) {
            logger.debug("Данные для сохранения в файл {} пусты, сохранение пропущено", fileName);
            return;
        }
        try {
            excelFileManager.writeToExcel(data, fileName, entityClass);
            logger.info(LOG_RESULTS_SAVED, fileName);
        } catch (Exception e) {
            logger.error("Ошибка при сохранении результатов в файл: {}", fileName, e);
            throw new RuntimeException("Ошибка при сохранении файла: " + fileName, e);
        }
    }

    /**
     * Проверяет входные параметры для операций с данными.
     *
     * @param <T>         тип данных
     * @param entityClass класс сущности (не может быть null)
     * @throws IllegalArgumentException если класс равен null
     */
    private <T> void validateEntityClass(Class<T> entityClass) {
        if (entityClass == null) {
            throw new IllegalArgumentException("Класс сущности не может быть null");
        }
    }

    /**
     * Проверяет входные списки для метода duplicateInTwoFiles.
     *
     * @param <T>         тип данных
     * @param firstList   первый список (не может быть null)
     * @param secondList  второй список (не может быть null)
     * @param entityClass класс сущности (не может быть null)
     * @throws IllegalArgumentException если параметры некорректны
     */
    private <T> void validateInputLists(List<T> firstList,
                                        List<T> secondList,
                                        Class<T> entityClass) {
        if (firstList == null || secondList == null) {
            throw new IllegalArgumentException("Списки данных не могут быть null");
        }
        validateEntityClass(entityClass);
    }

    /**
     * Проверяет входной список для метода duplicateInOneFile.
     *
     * @param <T>         тип данных
     * @param dataList    список данных (не может быть null)
     * @param entityClass класс сущности (не может быть null)
     * @throws IllegalArgumentException если параметры некорректны
     */
    private <T> void validateInputList(List<T> dataList,
                                       Class<T> entityClass) {
        if (dataList == null) {
            throw new IllegalArgumentException("Список данных не может быть null");
        }
        validateEntityClass(entityClass);
    }

    /**
     * Проверяет список AdmPerson.
     *
     * @param admPersonList список для проверки
     * @throws IllegalArgumentException если список равен null
     */
    private void validateAdmPersonList(List<AdmPerson> admPersonList) {
        if (admPersonList == null) {
            throw new IllegalArgumentException("Список AdmPerson не может быть null");
        }
    }

    /**
     * Внутренний класс для хранения результатов поиска дубликатов.
     *
     * @param <T> тип данных
     */
    private record DuplicateSearchResult<T>(List<T> duplicates, int uniqueCount) {
    }
}