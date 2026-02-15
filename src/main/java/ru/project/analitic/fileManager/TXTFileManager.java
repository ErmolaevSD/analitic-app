package ru.project.analitic.fileManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

/**
 * Класс для чтения и обработки текстовых файлов специального формата.
 * Выполняет разбивку содержимого файла на логические блоки (листы) на основе маркеров в тексте.
 *
 * @author ErmolaevSD
 * @version 1.0
 */
public class TXTFileManager {

    /**
     * Читает текстовый файл и разбивает его содержимое на листы.
     *
     * <p>Метод выполняет следующие операции:</p>
     * <ul>
     *   <li>Читает все строки из файла с использованием кодировки UTF-8</li>
     *   <li>Разделяет содержимое на листы на основе маркеров:
     *     <ul>
     *       <li>Строки, содержащие "ЛИСТ" - маркер начала нового листа</li>
     *       <li>Строки, содержащие "\f" (символ перевода страницы) - маркер конца текущего листа</li>
     *     </ul>
     *   </li>
     *   <li>Каждая строка внутри листа разбивается на части по символу ":"</li>
     *   <li>Результат сохраняется в карту с автоматической нумерацией листов</li>
     * </ul>
     *
     * <p>Особенности работы:</p>
     * <ul>
     *   <li>Сохраняет порядок листов (используется LinkedHashMap)</li>
     *   <li>Нумерация листов начинается с 1</li>
     *   <li>Строка с маркером конца листа ("\f") исключается из результата</li>
     * </ul>
     *
     * @param pathName путь к файлу для чтения
     * @return карта, где ключ - строка с номером листа (формат: "Лист N"),
     * значение - список массивов строк, где каждый массив представляет
     * строку файла, разбитую по разделителю ":"
     * @throws IOException если возникает ошибка при чтении файла
     *
     */
    public Map<String, List<String[]>> readTXT(String pathName) throws IOException {
        Map<String, List<String[]>> listMap = new LinkedHashMap<>();
        List<String[]> buffer = new ArrayList<>();
        int count = 1;

        try (Stream<String> lines = Files.lines(Paths.get(pathName), StandardCharsets.UTF_8)) {
            Iterator<String> iterator = lines.iterator();

            while (iterator.hasNext()) {
                String line = iterator.next();

                if (line.contains("ЛИСТ")) {
                    if (!buffer.isEmpty()) {
                        listMap.put("Лист " + count++, new ArrayList<>(buffer));
                        buffer.clear();
                    }
                    String[] parts = line.split(":");
                    buffer.add(parts);
                } else if (line.contains("\f")) {
                    if (!buffer.isEmpty()) {
                        buffer.removeLast();
                        listMap.put("Лист " + count++, new ArrayList<>(buffer));
                        buffer.clear();
                    }
                } else {
                    String[] parts = line.split(":");
                    buffer.add(parts);
                }
            }

            if (!buffer.isEmpty()) {
                listMap.put("Лист " + count, new ArrayList<>(buffer));
            }
        }

        return listMap;
    }
}