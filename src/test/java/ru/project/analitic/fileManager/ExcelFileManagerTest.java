package ru.project.analitic.fileManager;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import ru.project.analitic.model.Person;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовый класс для ExcelFileManager.
 *
 * <p>Содержит unit-тесты для проверки функциональности чтения и записи Excel файлов
 * с использованием библиотеки EasyExcel.</p>
 *
 * @version 1.0
 * @author ErmolaevSD
 */
@Slf4j
class ExcelFileManagerTest {

    private ExcelFileManager excelFileManager;
    private String path;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        excelFileManager = new ExcelFileManager();
        path = "src/test/resources/тест.xlsx";
    }

    @Test
    void readExcel_ShouldReturnList_WhenValidFile() {
        List<Person> actualPersons = excelFileManager.readExcel(path, Person.class);

        assertNotNull(actualPersons, "Результат не должен быть null");
        assertFalse(actualPersons.isEmpty(), "Список не должен быть пустым");
        assertEquals(3539, actualPersons.size());
    }
}