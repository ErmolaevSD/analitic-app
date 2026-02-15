package ru.project.analitic.fileManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TxtFileServiceTest {

    private TXTFileManager txtFileService;
    private static final String TEST_RESOURCES_DIR = "src/test/resources/";

    @TempDir
    Path tempDir;

    @BeforeEach
    public void setUp() {
        txtFileService = new TXTFileManager();
    }

    @Test
    void readTXT_ShouldReturnNonNullMap_WhenFileExists() throws IOException {
        String testFilePath = TEST_RESOURCES_DIR + "тест.txt";

        Map<String, List<String[]>> result = txtFileService.readTXT(testFilePath);

        assertNotNull(result, "Результат не должен быть null");
        assertFalse(result.isEmpty(), "Карта не должна быть пустой");
    }

    @Test
    void readTXT_ShouldThrowException_WhenFileDoesNotExist() {
        String nonExistentFile = "src/test/resources/несуществующий.txt";

        assertThrows(IOException.class, () -> {
            txtFileService.readTXT(nonExistentFile);
        }, "Должно быть выброшено исключение при чтении несуществующего файла");
    }

    @Test
    void readTXT_ShouldHandleEmptyFile(@TempDir Path tempDir) throws IOException {
        Path emptyFile = tempDir.resolve("empty.txt");
        Files.createFile(emptyFile);

        Map<String, List<String[]>> result = txtFileService.readTXT(emptyFile.toString());

        assertNotNull(result, "Результат не должен быть null для пустого файла");
        assertTrue(result.isEmpty(), "Карта должна быть пустой для пустого файла");
    }

    @Test
    void readTXT_ShouldHandleFileWithoutSheetMarkers() throws IOException {
        String testFilePath = TEST_RESOURCES_DIR + "тест.txt";
        int expectedSheets = 18;

        Map<String, List<String[]>> result = txtFileService.readTXT(testFilePath);

        assertEquals(expectedSheets, result.size());
    }
}