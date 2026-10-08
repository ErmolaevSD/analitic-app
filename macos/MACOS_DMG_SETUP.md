# 🍎 DMG УСТАНОВЩИК ДЛЯ MACOS

## 📦 ЧТО ТАКОЕ DMG?

**DMG** = Disk Image (образ диска) - стандартный формат распределения приложений на macOS.

**Преимущества DMG:**
- ✅ Профессиональный вид
- ✅ Легко устанавливать (перетащить в Applications)
- ✅ Автоматическое монтирование при открытии
- ✅ Компактный размер (сжатие)
- ✅ Безопасный способ распределения

---

## ⚡ БЫСТРЫЙ СТАРТ

### Шаг 1: Построить DMG

```bash
# Откройте Terminal
# Перейдите в папку проекта
cd /path/to/analitic-app

# Запустите скрипт сборки
./build-dmg.sh

# Ждите 2-3 минуты...
```

**Результат:** `Analitic-1.0.dmg` в текущей папке

### Шаг 2: Использовать DMG

```bash
# Способ 1: Двойной клик в Finder (самый простой)
# Найдите Analitic-1.0.dmg в Finder
# Дважды кликните
# Откроется окно с инструкциями

# Способ 2: Через Terminal
# hdiutil attach Analitic-1.0.dmg

# Способ 3: Отправить друзьям
# Просто отправьте файл Analitic-1.0.dmg
```

---

## 📋 ПРОЦЕСС СБОРКИ DMG

### Что происходит когда вы запускаете `./build-dmg.sh`:

```
1. Проверка Java и Maven
   └─ Убедитесь что установлены

2. Сборка JAR файла
   └─ mvn clean package -DskipTests
   └─ Создается: target/analitic-1.0-SNAPSHOT.jar

3. Создание Application Bundle (.app)
   └─ Структура: Analitic.app/
      ├── Contents/
      │   ├── MacOS/
      │   │   └── analitic (запускающий скрипт)
      │   ├── Resources/
      │   ├── Java/
      │   │   └── analitic-1.0-SNAPSHOT.jar
      │   └── Info.plist (информация о приложении)

4. Упаковка в DMG образ
   └─ hdiutil create ...
   └─ Сжатие UDZO (до 80%)
   └─ Создается: Analitic-1.0.dmg (~20-30 MB)

5. Очистка временных файлов
   └─ Удаляются промежуточные файлы
```

---

## 🖥️ УСТАНОВКА ПРИЛОЖЕНИЯ ИЗ DMG

### Способ 1: Через Finder (РЕКОМЕНДУЕТСЯ)

1. **Откройте Finder**
2. **Найдите файл `Analitic-1.0.dmg`**
3. **Дважды кликните** на файл
4. **Откроется окно с инструкциями:**
   ```
   Analitic App
   ├── Analitic.app (значок приложения)
   ├── Applications → (ярлык на папку Applications)
   └── README.txt (инструкции)
   ```
5. **Перетащите `Analitic.app` в папку `Applications`**
6. **Закройте окно DMG**
7. **Откройте Applications и запустите `Analitic.app`**

### Способ 2: Через Terminal

```bash
# Смонтировать образ
hdiutil attach Analitic-1.0.dmg

# Открыть в Finder
open /Volumes/Analitic

# Скопировать в Applications
cp -r /Volumes/Analitic/Analitic.app /Applications/

# Отсмонтировать образ
hdiutil detach /Volumes/Analitic

# Запустить приложение
open /Applications/Analitic.app
```

### Способ 3: Прямой запуск из DMG

```bash
# Просто откройте DMG и запустите приложение прямо оттуда
# Оно будет работать без установки в Applications
hdiutil attach Analitic-1.0.dmg
/Volumes/Analitic/Analitic.app/Contents/MacOS/analitic
```

---

## ⚠️ ПРЕДУПРЕЖДЕНИЕ БЕЗОПАСНОСТИ MACOS

При первом запуске приложение может показать:

```
"Analitic" cannot be opened because the developer cannot be verified.
```

**Решение:**

### Способ 1: Быстрое решение

1. Откройте **System Preferences** → **Security & Privacy**
2. Нажмите иконку замка внизу слева (введите пароль)
3. Найдите сообщение об Analitic
4. Нажмите **"Open Anyway"**

### Способ 2: Через контекстное меню

1. Нажмите **Control (Ctrl)** и кликните на **Analitic.app**
2. Выберите **Open**
3. В диалоге нажмите **Open**
4. Приложение запустится

### Способ 3: Через Terminal

```bash
# Удалить атрибут карантина
xattr -d com.apple.quarantine /Applications/Analitic.app

# Затем запустить
open /Applications/Analitic.app
```

---

## 📊 СТРУКТУРА DMG ФАЙЛА

После монтирования Analitic-1.0.dmg видно:

```
Analitic (образ)
├── Analitic.app
│   └── Contents/
│       ├── MacOS/
│       │   └── analitic (запускающий скрипт)
│       ├── Resources/
│       ├── Java/
│       │   └── analitic-1.0-SNAPSHOT.jar
│       └── Info.plist
├── Applications → /Applications (ярлык)
└── README.txt (инструкции)
```

---

## 🔧 ТРЕБОВАНИЯ ДЛЯ СБОРКИ DMG

| Компонент | Требование | Проверка |
|-----------|-----------|----------|
| **macOS** | 10.13+ | About This Mac |
| **Java** | 21+ | `java -version` |
| **Maven** | 3.6+ | `mvn --version` |
| **Xcode CLI** | Последняя версия | `xcode-select --install` |

---

## 📝 ПОЛНАЯ ИНСТРУКЦИЯ ДЛЯ ПОЛЬЗОВАТЕЛЯ

### Для конечного пользователя (получившего DMG):

**На macOS:**

1. **Скачайте Java 21** (если не установлена)
   - https://oracle.com/java/
   - Или: `brew install java21`

2. **Откройте DMG файл**
   - Найдите `Analitic-1.0.dmg`
   - Дважды кликните

3. **Установите приложение**
   - Перетащите `Analitic.app` в папку `Applications`
   - Закройте окно

4. **Запустите приложение**
   - Откройте `Applications` в Finder
   - Двойной клик на `Analitic.app`
   - Если спросит о разработчике - кликните `Open`

5. **Наслаждайтесь!** 🎉

---

## 🐛 РЕШЕНИЕ ПРОБЛЕМ

### ❌ "build-dmg.sh: command not found"

```bash
# Убедитесь что скрипт исполняемый
chmod +x build-dmg.sh

# Запустите
./build-dmg.sh
```

### ❌ "Java not found"

```bash
# Установить Java через Homebrew
brew tap homebrew/cask-versions
brew install java21

# Проверить
java -version
```

### ❌ "Maven not found"

```bash
# Установить Maven через Homebrew
brew install maven

# Проверить
mvn --version
```

### ❌ "Xcode Command Line Tools not found"

```bash
# Установить
xcode-select --install

# Выберите Install в диалоге
```

### ❌ "DMG file corrupted"

```bash
# Пересобрать DMG
rm -f Analitic-1.0.dmg
./build-dmg.sh
```

### ❌ "Application won't start"

1. Проверьте что Java 21+ установлена
2. Попробуйте запустить из Terminal:
   ```bash
   /Applications/Analitic.app/Contents/MacOS/analitic
   ```
3. Если ошибка - посмотрите вывод в Terminal

---

## 📊 ПАРАМЕТРЫ СКРИПТА

В `build-dmg.sh` можно изменить:

```bash
# Имя приложения
APP_NAME="Analitic App"

# Версия
VERSION="1.0"

# Память JVM
-Xmx1024m -Xms512m  # Измените при необходимости
```

---

## 🔐 ПОДПИСАНИЕ ПРИЛОЖЕНИЯ (ОПЦИОНАЛЬНО)

Для распределения в App Store нужна подпись:

```bash
# Подписать приложение своим сертификатом
codesign --deep --force --verify --verbose \
  --sign "Developer ID Application" \
  /Applications/Analitic.app

# Проверить подпись
codesign -v /Applications/Analitic.app
```

---

## 📦 РАСПРЕДЕЛЕНИЕ DMG

### Что отправить пользователю:

```
Analitic-1.0.dmg (20-30 MB)
↓
Пользователь дважды кликает
↓
Видит окно установки
↓
Перетаскивает в Applications
↓
ГОТОВО! 🎉
```

### Размер файлов:

| Файл | Размер |
|------|--------|
| target/analitic-1.0-SNAPSHOT.jar | 60-80 MB |
| Analitic.app (распакованный) | 60-80 MB |
| Analitic-1.0.dmg (сжатый) | 20-30 MB |

---

## ✅ CHECKLIST

- [ ] Java 21+ установлена
- [ ] Maven установлен
- [ ] Скрипт `build-dmg.sh` исполняемый
- [ ] Запустили `./build-dmg.sh`
- [ ] Создан файл `Analitic-1.0.dmg`
- [ ] DMG можно открыть в Finder
- [ ] Приложение можно установить
- [ ] Приложение запускается

---

## 🎉 ГОТОВО!

Вы можете теперь распространять `Analitic-1.0.dmg` пользователям macOS!

**Что нужно пользователю:**
- ✅ Java 21+
- ✅ Файл: Analitic-1.0.dmg

**Всё остальное автоматическое!** 🚀


