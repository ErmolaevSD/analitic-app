package ru.project.analitic.service;

import lombok.extern.slf4j.Slf4j;
import ru.project.analitic.fileManager.ExcelFileManager;
import ru.project.analitic.fileManager.TXTFileManager;
import ru.project.analitic.model.AdmPerson;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.isNull;

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
 * @version 1.0
 * @author ErmolaevSD
 * @see ExcelFileManager
 * @see AdmPerson
 */
@Slf4j
public class MainService {

    private final ExcelFileManager excelFileManager;
    private final TXTFileManager txtFileManager;

    private static final String DUPLICATES_TWO_FILES = "Дубликаты.xlsx";
    private static final String UNIQUE_FIRST_FILE = "Уникальные в первом файле.xlsx";
    private static final String UNIQUE_SECOND_FILE = "Уникальные во втором файле.xlsx";
    private static final String DUPLICATES_SINGLE_FILE = "Дубликаты в файле.xlsx";
    private static final String SVERKA_116_PART1_FILE = "Сверка 116_часть_1.xlsx";

    private static final int PARALLEL_PROCESSING_THRESHOLD = 10000;

    /**
     * Конструктор сервиса с внедрением зависимости.
     *
     * @param excelFileManager менеджер для работы с Excel файлами
     */
    public MainService(ExcelFileManager excelFileManager, TXTFileManager txtFileManager) {
        this.excelFileManager = excelFileManager;
        this.txtFileManager = txtFileManager;
        log.info("✅ MainService инициализирован успешно");
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
     * @throws NullPointerException если у записей отсутствуют обязательные поля
     */
    public void sverka116PathOne(List<AdmPerson> admPersonList) {
        validateAdmPersonList(admPersonList, "sverka116PathOne");

        log.info("🔍 Начало проверки 116 часть 1");
        log.debug("Количество записей для анализа: {}", admPersonList.size());

        long startTime = System.currentTimeMillis();

        // Оптимизированная группировка
        Map<AdmPerson, List<AdmPerson>> groupedPersons = groupDuplicate(admPersonList);

        List<AdmPerson> problematicPeriods = Collections.synchronizedList(new ArrayList<>());

        // Параллельная обработка для больших групп
        processGroupsInParallel(groupedPersons, problematicPeriods);

        long duration = System.currentTimeMillis() - startTime;

        logResults("проверки 116 часть 1", admPersonList.size(),
                problematicPeriods.size() / 2, duration);

        if (!problematicPeriods.isEmpty()) {
            saveResultsWithLogging(problematicPeriods, SVERKA_116_PART1_FILE, AdmPerson.class);
            log.warn("⚠️ Обнаружено {} потенциальных повторов. Проверьте файл {}",
                    problematicPeriods.size() / 2, SVERKA_116_PART1_FILE);
        } else {
            log.info("✅ Потенциальные повторы не обнаружены");
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
     * @param <T>            тип объектов для сравнения
     * @param firstList      список объектов из первого файла (не может быть null)
     * @param secondList     список объектов из второго файла (не может быть null)
     * @param entityClass    класс объектов для определения структуры Excel
     * @throws IllegalArgumentException если любой из списков или класс равен null
     */
    public <T> void duplicateInTwoFiles(List<T> firstList,
                                        List<T> secondList,
                                        Class<T> entityClass) {
        validateInputLists(firstList, secondList, entityClass, "duplicateInTwoFiles");

        log.info("🔍 Начало поиска дубликатов между двумя файлами");
        log.debug("Размер первого файла: {}, второго файла: {}",
                firstList.size(), secondList.size());

        long startTime = System.currentTimeMillis();

        // Оптимизация: выбираем меньшее множество для contains
        Set<T> setFirst = new HashSet<>(firstList);
        Set<T> setSecond = new HashSet<>(secondList);

        // Оптимизация: используем размер для выбора стратегии
        boolean useParallel = shouldUseParallel(firstList.size(), secondList.size());

        // Находим дубликаты (пересечение множеств)
        List<T> duplicates = findDuplicates(secondList, setFirst, useParallel);

        // Находим уникальные записи в каждом файле
        List<T> uniqueInFirst = findUnique(firstList, setSecond, useParallel);
        List<T> uniqueInSecond = findUnique(secondList, setFirst, useParallel);

        long duration = System.currentTimeMillis() - startTime;

        // Логируем результаты
        log.info("📊 Результаты поиска дубликатов:");
        log.info("  - Дубликатов: {} ({} мс)", duplicates.size(), duration);
        log.info("  - Уникальных в первом файле: {}", uniqueInFirst.size());
        log.info("  - Уникальных во втором файле: {}", uniqueInSecond.size());

        // Сохраняем результаты
        Map<String, List<T>> results = Map.of(
                DUPLICATES_TWO_FILES, duplicates,
                UNIQUE_FIRST_FILE, uniqueInFirst,
                UNIQUE_SECOND_FILE, uniqueInSecond
        );

        saveMultipleResults(results, entityClass);

        log.info("✅ Обработка дубликатов между файлами завершена");
    }

    /**
     * Находит дубликаты в одном файле и опционально записывает результаты.
     *
     * <p>Метод анализирует список объектов и выявляет повторяющиеся записи.
     * Дубликатами считаются объекты, которые равны согласно методу {@code equals()}.</p>
     *
     * @param <T>            тип объектов для анализа
     * @param dataList       список объектов для проверки (не может быть null)
     * @param entityClass    класс объектов для определения структуры Excel
     * @param writeToFile    если {@code true}, результат сохраняется в файл
     * @return список найденных дубликатов (может быть пустым)
     * @throws IllegalArgumentException если список или класс равен null
     */
    public <T> List<T> duplicateInOneFile(List<T> dataList,
                                          Class<T> entityClass,
                                          boolean writeToFile) {
        validateInputList(dataList, entityClass, "duplicateInOneFile");

        log.info("🔍 Начало поиска дубликатов в одном файле");
        log.debug("Размер файла для анализа: {}", dataList.size());

        long startTime = System.currentTimeMillis();

        // Оптимизированный поиск дубликатов
        DuplicateSearchResult<T> result = findDuplicatesOptimized(dataList);

        long duration = System.currentTimeMillis() - startTime;

        log.info("📊 Результаты поиска дубликатов:");
        log.info("  - Всего записей: {}", dataList.size());
        log.info("  - Уникальных записей: {}", result.uniqueCount);
        log.info("  - Дубликатов: {}", result.duplicates.size());
        log.info("  - Время выполнения: {} мс", duration);

        if (writeToFile && !result.duplicates.isEmpty()) {
            saveResultsWithLogging(result.duplicates, DUPLICATES_SINGLE_FILE, entityClass);
        } else if (writeToFile && result.duplicates.isEmpty()) {
            log.info("ℹ️ Дубликаты не найдены, файл не создан");
        }

        return result.duplicates;
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
        Map<AdmPerson, List<AdmPerson>> groupedPersons;

        if (admPersonList.size() > PARALLEL_PROCESSING_THRESHOLD) {
            groupedPersons = new ConcurrentHashMap<>();
        } else {
            groupedPersons = new HashMap<>();
        }

        for (AdmPerson person : admPersonList) {
            groupedPersons.computeIfAbsent(person, k -> new ArrayList<>()).add(person);
        }

        log.debug("Сгруппировано {} уникальных людей из {} записей",
                groupedPersons.size(), admPersonList.size());

        return groupedPersons;
    }

    /**
     * Обрабатывает группы записей в параллельном режиме при необходимости.
     *
     * @param groupedPersons карта сгруппированных записей
     * @param problematicPeriods список для сбора проблемных записей
     */
    private void processGroupsInParallel(Map<AdmPerson, List<AdmPerson>> groupedPersons,
                                         List<AdmPerson> problematicPeriods) {
        if (groupedPersons.size() > PARALLEL_PROCESSING_THRESHOLD) {
            // Параллельная обработка для больших данных
            groupedPersons.values().parallelStream().forEach(group -> {
                List<AdmPerson> localProblems = analyzeGroup(group);
                synchronized (problematicPeriods) {
                    problematicPeriods.addAll(localProblems);
                }
            });
        } else {
            // Последовательная обработка
            for (List<AdmPerson> group : groupedPersons.values()) {
                problematicPeriods.addAll(analyzeGroup(group));
            }
        }
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

        // Сортируем группу
        group.sort(Comparator.comparing(AdmPerson::getDateFact));

        // Анализируем последовательные записи
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
     * @param current текущая запись
     * @return true если есть разрыв между периодами
     */
    private boolean hasGap(AdmPerson previous, AdmPerson current) {
        return !isNull(previous.getDateEnd()) &&
                current.getDateFact().isAfter(previous.getDateEnd());
    }

    /**
     * Находит дубликаты в списке с оптимизацией.
     *
     * @param <T> тип данных
     * @param list список для поиска
     * @param set множество для проверки
     * @param useParallel использовать параллельную обработку
     * @return список дубликатов
     */
    private <T> List<T> findDuplicates(List<T> list, Set<T> set, boolean useParallel) {
        Stream<T> stream = useParallel ? list.parallelStream() : list.stream();
        return stream.filter(set::contains)
                .collect(Collectors.toList());
    }

    /**
     * Находит уникальные записи в списке.
     *
     * @param <T> тип данных
     * @param list список для анализа
     * @param set множество для проверки
     * @param useParallel использовать параллельную обработку
     * @return список уникальных записей
     */
    private <T> List<T> findUnique(List<T> list, Set<T> set, boolean useParallel) {
        Stream<T> stream = useParallel ? list.parallelStream() : list.stream();
        return stream.filter(person -> !set.contains(person))
                .collect(Collectors.toList());
    }

    /**
     * Оптимизированный поиск дубликатов в одном файле.
     *
     * @param <T> тип данных
     * @param dataList список для анализа
     * @return результат поиска с информацией о дубликатах
     */
    private <T> DuplicateSearchResult<T> findDuplicatesOptimized(List<T> dataList) {
        Set<T> uniqueElements = dataList.size() > PARALLEL_PROCESSING_THRESHOLD
                ? ConcurrentHashMap.newKeySet()
                : new HashSet<>();

        List<T> duplicates = new ArrayList<>();

        if (dataList.size() > PARALLEL_PROCESSING_THRESHOLD) {
            // Параллельная обработка для больших списков
            findDuplicatesParallel(dataList, uniqueElements, duplicates);
        } else {
            // Последовательная обработка
            findDuplicatesSequential(dataList, uniqueElements, duplicates);
        }

        return new DuplicateSearchResult<>(duplicates, uniqueElements.size());
    }

    /**
     * Последовательный поиск дубликатов.
     */
    private <T> void findDuplicatesSequential(List<T> dataList,
                                              Set<T> uniqueElements,
                                              List<T> duplicates) {
        for (T element : dataList) {
            if (!uniqueElements.add(element)) {
                duplicates.add(element);
            }
        }
    }

    /**
     * Параллельный поиск дубликатов.
     */
    private <T> void findDuplicatesParallel(List<T> dataList,
                                            Set<T> uniqueElements,
                                            List<T> duplicates) {
        List<T> syncDuplicates = Collections.synchronizedList(duplicates);

        dataList.parallelStream().forEach(element -> {
            if (!uniqueElements.add(element)) {
                syncDuplicates.add(element);
            }
        });
    }

    /**
     * Определяет, нужно ли использовать параллельную обработку.
     */
    private boolean shouldUseParallel(int size1, int size2) {
        return size1 > PARALLEL_PROCESSING_THRESHOLD ||
                size2 > PARALLEL_PROCESSING_THRESHOLD;
    }

    /**
     * Сохраняет несколько результатов в файлы.
     *
     * @param <T> тип данных
     * @param results карта имя файла -> список данных
     * @param entityClass класс сущности
     */
    private <T> void saveMultipleResults(Map<String, List<T>> results, Class<T> entityClass) {
        results.forEach((fileName, data) -> {
            if (!data.isEmpty()) {
                excelFileManager.writeToExcel(data, fileName, entityClass);
                log.info("💾 Сохранено {} записей в файл: {}", data.size(), fileName);
            } else {
                log.info("ℹ️ Нет данных для сохранения в файл: {}", fileName);
            }
        });
    }

    /**
     * Сохраняет результаты с логированием.
     *
     * @param <T> тип данных
     * @param data список данных для сохранения
     * @param fileName имя файла
     * @param entityClass класс сущности
     */
    private <T> void saveResultsWithLogging(List<T> data, String fileName, Class<T> entityClass) {
        if (data.isEmpty()) {
            log.info("ℹ️ Нет данных для сохранения в файл: {}", fileName);
            return;
        }

        log.info("💾 Сохранение {} записей в файл: {}", data.size(), fileName);
        excelFileManager.writeToExcel(data, fileName, entityClass);
    }

    /**
     * Логирует результаты анализа.
     *
     * @param operationName название операции
     * @param totalRecords общее количество записей
     * @param problematicCount количество проблем
     * @param duration время выполнения
     */
    private void logResults(String operationName, int totalRecords,
                            int problematicCount, long duration) {
        log.info("📊 Результаты {}:", operationName);
        log.info("  - Проанализировано записей: {}", totalRecords);
        log.info("  - Выявлено проблем: {}", problematicCount);
        log.info("  - Время выполнения: {} мс", duration);
    }

    /**
     * Проверяет входные списки для метода duplicateInTwoFiles.
     *
     * @param <T> тип данных
     * @param firstList первый список
     * @param secondList второй список
     * @param entityClass класс сущности
     * @param methodName имя метода для логирования
     * @throws IllegalArgumentException если параметры некорректны
     */
    private <T> void validateInputLists(List<T> firstList,
                                        List<T> secondList,
                                        Class<T> entityClass,
                                        String methodName) {
        if (firstList == null || secondList == null) {
            throw new IllegalArgumentException("Списки данных не могут быть null");
        }
        if (entityClass == null) {
            throw new IllegalArgumentException("Класс сущности не может быть null");
        }
        log.debug("Валидация параметров для {} пройдена успешно", methodName);
    }

    /**
     * Проверяет входной список для метода duplicateInOneFile.
     *
     * @param <T> тип данных
     * @param dataList список данных
     * @param entityClass класс сущности
     * @param methodName имя метода для логирования
     * @throws IllegalArgumentException если параметры некорректны
     */
    private <T> void validateInputList(List<T> dataList,
                                       Class<T> entityClass,
                                       String methodName) {
        if (dataList == null) {
            throw new IllegalArgumentException("Список данных не может быть null");
        }
        if (entityClass == null) {
            throw new IllegalArgumentException("Класс сущности не может быть null");
        }
        log.debug("Валидация параметров для {} пройдена успешно", methodName);
    }

    /**
     * Проверяет список AdmPerson.
     *
     * @param admPersonList список для проверки
     * @param methodName имя метода для логирования
     * @throws IllegalArgumentException если список равен null
     */
    private void validateAdmPersonList(List<AdmPerson> admPersonList, String methodName) {
        if (admPersonList == null) {
            throw new IllegalArgumentException("Список AdmPerson не может быть null");
        }
        if (admPersonList.isEmpty()) {
            log.warn("⚠️ Передан пустой список AdmPerson в метод {}", methodName);
        }
    }

    /**
     * Внутренний класс для хранения результатов поиска дубликатов.
     *
     * @param <T> тип данных
     */
    private static class DuplicateSearchResult<T> {
        final List<T> duplicates;
        final int uniqueCount;

        DuplicateSearchResult(List<T> duplicates, int uniqueCount) {
            this.duplicates = duplicates;
            this.uniqueCount = uniqueCount;
        }
    }
}