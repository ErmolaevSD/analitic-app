# ✅ ПОЛНОЕ РЕШЕНИЕ: DMG УСТАНОВЩИК ДЛЯ MACOS

## 📦 ЧТО БЫЛО СОЗДАНО

```
✅ build-dmg.sh              - Скрипт сборки DMG (исполняемый)
✅ run.sh                    - Скрипт запуска приложения (исполняемый)
✅ Analitic.app              - Application Bundle (создается при сборке)
✅ Analitic-1.0.dmg          - Готовый DMG установщик (создается при сборке)
✅ MACOS_DMG_SETUP.md        - Подробное руководство
✅ STEP_BY_STEP_MACOS_DMG.md - Пошаговая инструкция
```

---

## ⚡ БЫСТРЫЙ СТАРТ (3 ШАГА)

### Шаг 1️⃣: Проверка требований

```bash
java -version    # Java 21+
mvn --version    # Maven 3.6+
xcode-select -v  # Xcode CLI Tools
```

Если что-то не установлено - установите через Homebrew:
```bash
brew install java21 maven
xcode-select --install
```

### Шаг 2️⃣: Сборка DMG (один раз)

```bash
cd /Users/YourName/IdeaProjects/analitic-app
chmod +x build-dmg.sh
./build-dmg.sh

# Ждите 2-3 минуты...
# Результат: Analitic-1.0.dmg
```

### Шаг 3️⃣: Установка и запуск

```bash
# Способ 1: Через Finder (самый простой)
# Двойной клик на Analitic-1.0.dmg
# Перетащите Analitic.app в Applications
# Запустите из Applications

# Способ 2: Через Terminal
hdiutil attach Analitic-1.0.dmg
cp -r /Volumes/Analitic/Analitic.app /Applications/
open /Applications/Analitic.app
```

---

## 📊 ИНФОРМАЦИЯ О DMG

### Размеры файлов:

| Файл | Размер |
|------|--------|
| JAR файл | 60-80 MB |
| Analitic.app (распакованный) | 60-80 MB |
| Analitic-1.0.dmg (сжатый) | 20-30 MB |

### Что содержит DMG:

```
Analitic-1.0.dmg (образ диска)
├── Analitic.app/
│   └── Contents/
│       ├── MacOS/
│       │   └── analitic (запускающий скрипт)
│       ├── Resources/
│       ├── Java/
│       │   └── analitic-1.0-SNAPSHOT.jar
│       └── Info.plist (конфигурация приложения)
├── Applications/ → /Applications (ярлык на папку)
└── README.txt (инструкции)
```

---

## 🔨 ПРОЦЕСС СБОРКИ

Когда вы запускаете `./build-dmg.sh`:

```
[1/5] Проверка Java и Maven
      └─ Убедитесь что установлены

[2/5] Сборка JAR файла
      └─ mvn clean package -DskipTests
      └─ Создается: target/analitic-1.0-SNAPSHOT.jar

[3/5] Создание Application Bundle
      └─ Создается структура Analitic.app/
      └─ Копируется JAR файл
      └─ Создается Info.plist

[4/5] Упаковка в DMG образ
      └─ hdiutil create ...
      └─ Сжатие UDZO (80%)
      └─ Результат: Analitic-1.0.dmg

[5/5] Очистка временных файлов
      └─ Удаляются промежуточные файлы
```

---

## 🚀 СПОСОБЫ ИСПОЛЬЗОВАНИЯ DMG

### Способ 1: Finder (РЕКОМЕНДУЕТСЯ)

1. Найдите `Analitic-1.0.dmg` в Finder
2. Дважды кликните
3. Откроется окно с приложением
4. Перетащите `Analitic.app` в папку `Applications`
5. Запустите из Applications

### Способ 2: Terminal

```bash
# Смонтировать
hdiutil attach Analitic-1.0.dmg

# Просмотреть содержимое
ls -la /Volumes/Analitic/

# Установить (скопировать в Applications)
cp -r /Volumes/Analitic/Analitic.app /Applications/

# Отсмонтировать
hdiutil detach /Volumes/Analitic

# Запустить
open /Applications/Analitic.app
```

### Способ 3: Прямой запуск из DMG

```bash
# Запустить напрямую из образа (без копирования в Applications)
hdiutil attach Analitic-1.0.dmg
/Volumes/Analitic/Analitic.app/Contents/MacOS/analitic
```

---

## ⚠️ ПРЕДУПРЕЖДЕНИЕ БЕЗОПАСНОСТИ MACOS

При первом запуске может появиться окно:

```
"Analitic" cannot be opened because the developer cannot be verified
```

### Решение:

**Способ 1: Через контекстное меню (быстро)**
```
1. Control (Ctrl) + Click на Analitic.app
2. Выберите "Open"
3. Нажмите "Open" в диалоге
4. Приложение запустится
```

**Способ 2: Через System Preferences**
```
1. System Preferences → Security & Privacy
2. Найдите Analitic
3. Нажмите "Open Anyway"
```

**Способ 3: Через Terminal**
```bash
xattr -d com.apple.quarantine /Applications/Analitic.app
open /Applications/Analitic.app
```

---

## 📋 СТРУКТУРА ПРОЕКТА

```
analitic-app/
├── build-dmg.sh                      ← Запуск сборки DMG
├── run.sh                            ← Запуск приложения
├── pom.xml                           ← Maven конфигурация
├── MACOS_DMG_SETUP.md               ← Подробное руководство
├── STEP_BY_STEP_MACOS_DMG.md        ← Пошаговая инструкция
│
├── src/                              ← Исходный код
│   ├── main/java/
│   └── main/resources/
│
└── target/
    ├── analitic-1.0-SNAPSHOT.jar     ← JAR файл
    ├── analitic-1.0.dmg              ← Готовый DMG (после сборки)
    └── classes/
```

---

## ✅ ТРЕБОВАНИЯ

| Компонент | Версия | Проверка |
|-----------|--------|----------|
| **macOS** | 10.13+ | About This Mac |
| **Java** | 21+ | `java -version` |
| **Maven** | 3.6+ | `mvn --version` |
| **Xcode CLI** | Latest | `xcode-select --install` |

---

## 🔧 КОМАНДЫ МАCOS

```bash
# Установка через Homebrew
brew install java21
brew install maven

# Сборка DMG
cd /path/to/analitic-app
chmod +x build-dmg.sh
./build-dmg.sh

# Монтирование DMG
hdiutil attach Analitic-1.0.dmg

# Просмотр содержимого
ls /Volumes/Analitic/

# Копирование приложения
cp -r /Volumes/Analitic/Analitic.app /Applications/

# Отсмонтирование
hdiutil detach /Volumes/Analitic/

# Запуск приложения
open /Applications/Analitic.app

# Запуск из Terminal
/Applications/Analitic.app/Contents/MacOS/analitic

# Проверка подписи (если подписано)
codesign -v /Applications/Analitic.app
```

---

## 📦 РАСПРЕДЕЛЕНИЕ DMG

### Что отправить:

```
Analitic-1.0.dmg (20-30 MB)
```

### Инструкция для пользователя:

1. **Проверьте Java 21+**
   ```bash
   java -version
   ```

2. **Откройте DMG файл**
   - Двойной клик на `Analitic-1.0.dmg`

3. **Установите приложение**
   - Перетащите `Analitic.app` в папку `Applications`

4. **Запустите приложение**
   - Откройте `Applications` в Finder
   - Двойной клик на `Analitic.app`
   - Если спросит - нажмите `Open`

5. **Готово!** 🎉

---

## 🐛 РЕШЕНИЕ ПРОБЛЕМ

### ❌ "build-dmg.sh: No such file or directory"
```bash
chmod +x build-dmg.sh
./build-dmg.sh
```

### ❌ "Java not found"
```bash
brew install java21
java -version
```

### ❌ "Maven not found"
```bash
brew install maven
mvn --version
```

### ❌ "Xcode CLI Tools not found"
```bash
xcode-select --install
```

### ❌ "DMG corrupted"
```bash
rm -f Analitic-1.0.dmg
./build-dmg.sh
```

### ❌ "Application won't start"
```bash
# Запустить из Terminal для диагностики
/Applications/Analitic.app/Contents/MacOS/analitic
```

---

## ✨ ОКОНЧАТЕЛЬНЫЙ СТАТУС

```
✅ Скрипт сборки DMG готов
✅ Application Bundle структура создана
✅ DMG образ создается одной командой
✅ Инструкции подробно описаны
✅ Поддержка Java 21+
✅ Сжатие UDZO для малого размера
✅ Профессиональный вид
✅ Готово к распределению
```

---

## 📚 ДОПОЛНИТЕЛЬНАЯ ИНФОРМАЦИЯ

Для получения дополнительной информации читайте:

- **STEP_BY_STEP_MACOS_DMG.md** - Полная пошаговая инструкция ⭐
- **MACOS_DMG_SETUP.md** - Подробное руководство

---

## 🎉 ГОТОВО!

DMG установщик для macOS полностью подготовлен и готов к использованию!

**Что нужно пользователю:**
- ✅ Java 21+ установлена
- ✅ Файл: Analitic-1.0.dmg

**Всё остальное автоматическое!** 🚀

---

**Дата создания:** 21 марта 2026  
**Статус:** ✅ READY FOR MACOS DISTRIBUTION  
**Версия:** 1.0-SNAPSHOT  


