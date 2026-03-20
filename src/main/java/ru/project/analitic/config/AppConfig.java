package ru.project.analitic.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Конфигурация приложения.
 *
 * <p>Загружает и управляет параметрами приложения из properties файла.</p>
 *
 * @author ErmolaevSD
 * @version 2.1
 */
public class AppConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);
    private static final String CONFIG_FILE = "application.properties";
    private static final Properties properties = new Properties();
    
    static {
        loadConfig();
    }
    
    /**
     * Загружает конфигурацию из properties файла.
     */
    private static void loadConfig() {
        try (InputStream input = AppConfig.class.getClassLoader()
                .getResourceAsStream(CONFIG_FILE)) {
            
            if (input == null) {
                logger.warn("Конфиг файл {} не найден, используются значения по умолчанию", CONFIG_FILE);
                setDefaults();
                return;
            }
            
            properties.load(input);
            logger.info("Конфигурация успешно загружена из {}", CONFIG_FILE);
            
        } catch (IOException e) {
            logger.error("Ошибка при загрузке конфигурации", e);
            setDefaults();
        }
    }
    
    /**
     * Устанавливает значения конфигурации по умолчанию.
     */
    private static void setDefaults() {
        properties.setProperty("app.title", "Сверка-УОДУУП");
        properties.setProperty("app.version", "2.1");
        properties.setProperty("app.window.width", "800");
        properties.setProperty("app.window.height", "600");
        properties.setProperty("app.results.directory", "результаты");
        properties.setProperty("app.thread.pool.size", "4");
        properties.setProperty("app.log.level", "INFO");
        logger.info("Установлены значения конфигурации по умолчанию");
    }
    
    /**
     * Получает строковое значение из конфигурации.
     *
     * @param key      ключ параметра
     * @param defValue значение по умолчанию
     * @return значение параметра
     */
    public static String getString(String key, String defValue) {
        return properties.getProperty(key, defValue);
    }
    
    /**
     * Получает целочисленное значение из конфигурации.
     *
     * @param key      ключ параметра
     * @param defValue значение по умолчанию
     * @return значение параметра
     */
    public static int getInt(String key, int defValue) {
        try {
            String value = properties.getProperty(key, String.valueOf(defValue));
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warn("Ошибка при парсинге параметра {}: используется значение по умолчанию {}", key, defValue);
            return defValue;
        }
    }
    
    /**
     * Получает булево значение из конфигурации.
     *
     * @param key      ключ параметра
     * @param defValue значение по умолчанию
     * @return значение параметра
     */
    public static boolean getBoolean(String key, boolean defValue) {
        String value = properties.getProperty(key, String.valueOf(defValue));
        return Boolean.parseBoolean(value);
    }
    
    // Удобные методы для часто используемых параметров
    
    public static String getAppTitle() {
        return getString("app.title", "Сверка-УОДУУП");
    }
    
    public static String getAppVersion() {
        return getString("app.version", "2.1");
    }
    
    public static int getWindowWidth() {
        return getInt("app.window.width", 800);
    }
    
    public static int getWindowHeight() {
        return getInt("app.window.height", 600);
    }
    
    public static int getThreadPoolSize() {
        return getInt("app.thread.pool.size", 4);
    }
    
    public static String getLogLevel() {
        return getString("app.log.level", "INFO");
    }
}

