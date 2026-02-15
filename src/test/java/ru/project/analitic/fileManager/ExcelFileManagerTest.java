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

    @Test
    void writeToExcel_ShouldCreateFile_WhenValidData() {
        String fileName = "test_output.xlsx";
        Person personOne = Person.builder()
                .firstName("Sergey")
                .lastName("Ermolaev")
                .surName("Dmitrievich")
                .birthdate(LocalDate.of(1999, 2, 16))
                .build();
        Person personTwo = Person.builder()
                .firstName("Masha")
                .lastName("Ermolaeva")
                .surName("Eduardovna")
                .birthdate(LocalDate.of(2001, 4, 12))
                .build();
        List<Person> personList = List.of(personOne, personTwo);

        excelFileManager.writeToExcel(personList, fileName, Person.class);

        File writenFile = new File("результаты/test_output.xlsx");
        assertTrue(writenFile.exists());
    }
}