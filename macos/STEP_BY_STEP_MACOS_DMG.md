# 🍎 ПОШАГОВАЯ ИНСТРУКЦИЯ: СОЗДАНИЕ И ИСПОЛЬЗОВАНИЕ DMG

## 📝 ЭТАП 1: ПОДГОТОВКА

### Шаг 1.1: Проверка Java

```bash
java -version
```

**Должно показать:**
```
java version "21.0.1" 2023-10-17 LTS
Java(TM) SE Runtime Environment (build 21.0.1+12-LTS-29)
```

**Если Java не установлена:**
```bash
# Способ 1: Через Homebrew (рекомендуется)
brew tap homebrew/cask-versions
brew install java21

# Способ 2: Скачать с oracle.com
# https://oracle.com/java/technologies/downloads/#java21
```

### Шаг 1.2: Проверка Maven

```bash
mvn --version
```

**Если Maven не установлен:**
```bash
brew install maven
```

### Шаг 1.3: Проверка Xcode Command Line Tools

```bash
xcode-select -v
```

**Если не установлены:**
```bash
xcode-select --install
# Выберите Install в диалоге
```

---

## 🔨 ЭТАП 2: СБОРКА DMG

### Шаг 2.1: Откройте Terminal

- Нажмите `Cmd + Space`
- Введите `Terminal`
- Нажмите Enter

### Шаг 2.2: Перейдите в папку проекта

```bash
cd /Users/YourName/IdeaProjects/analitic-app
```

Замените `YourName` на ваше имя пользователя macOS.

### Шаг 2.3: Дайте права на выполнение скрипту

```bash
chmod +x build-dmg.sh
```

### Шаг 2.4: Запустите сборку DMG

```bash
./build-dmg.sh
```

**Вы увидите:**
```
╔════════════════════════════════════════════════════════════════╗
║         Building DMG Installer for Analitic App               ║
╚════════════════════════════════════════════════════════════════╝

[1/5] Checking Java installation...
Java version: java version "21.0.1" 2023-10-17 LTS
✅

[2/5] Checking Maven installation...
Apache Maven 3.9.6
✅

[3/5] Building JAR file...
✅ JAR file created successfully

[4/5] Creating macOS Application Bundle...
✅ Application Bundle created

[5/5] Creating DMG installer...
✅ DMG installer created successfully

📦 File: Analitic-1.0.dmg
📏 Size: 28 MB

╔════════════════════════════════════════════════════════════════╗
║                   BUILD COMPLETED SUCCESSFULLY                 ║
╚════════════════════════════════════════════════════════════════╝
```

### Шаг 2.5: Проверьте что DMG создан

```bash
ls -lh Analitic-1.0.dmg
```

**Должно показать:**
```
-rw-r--r--  1 user  group  28M Mar 21 11:30 Analitic-1.0.dmg
```

---

## 🚀 ЭТАП 3: УСТАНОВКА ПРИЛОЖЕНИЯ

### Способ 1: Через Finder (РЕКОМЕНДУЕТСЯ)

1. **Откройте Finder**
   - `Cmd + Space` → `Finder` → Enter

2. **Перейдите в папку проекта**
   - `/Users/YourName/IdeaProjects/analitic-app`

3. **Найдите файл `Analitic-1.0.dmg`**

4. **Дважды кликните на DMG файл**
   - Откроется окно с инструкциями
   - Видно: `Analitic.app` и папка `Applications`

5. **Перетащите `Analitic.app` в папку `Applications`**
   - Или в ярлык `Applications`

6. **Закройте окно DMG**
   - Нажмите красную кнопку закрытия
   - Образ автоматически отсмонтируется

### Способ 2: Через Terminal

```bash
# Смонтировать DMG
hdiutil attach Analitic-1.0.dmg

# Скопировать приложение в Applications
cp -r /Volumes/Analitic/Analitic.app /Applications/

# Отсмонтировать образ
hdiutil detach /Volumes/Analitic

# Проверить установку
ls -la /Applications/Analitic.app
```

---

## ✅ ЗАПУСК ПРИЛОЖЕНИЯ

### Способ 1: Через Finder (САМЫЙ ПРОСТОЙ)

1. **Откройте Finder**
2. **Перейдите в Applications**
3. **Найдите `Analitic.app`**
4. **Двойной клик**

### Способ 2: Через Spotlight

1. **Нажмите `Cmd + Space`**
2. **Введите `Analitic`**
3. **Нажмите Enter**

### Способ 3: Через Terminal

```bash
open /Applications/Analitic.app
```

### Способ 4: Через Launchpad

1. **Откройте Launchpad** (обычно в Dock)
2. **Найдите `Analitic`**
3. **Кликните на иконку**

---

## ⚠️ ПРЕДУПРЕЖДЕНИЕ БЕЗОПАСНОСТИ

При первом запуске может появиться:

```
"Analitic" cannot be opened because the developer 
cannot be verified.
```

### Решение 1: Через контекстное меню (быстро)

1. **Нажмите Control (Ctrl) + Click** на `Analitic.app`
2. **Выберите `Open`** из меню
3. **Нажмите `Open`** в диалоге
4. **Приложение запустится** ✅

### Решение 2: Через System Preferences

1. **Откройте System Preferences**
   - `Cmd + Space` → System Preferences

2. **Перейдите Security & Privacy**
   - Нажмите вкладку "Security"

3. **Найдите сообщение об Analitic**
   - Нажмите иконку замка внизу (введите пароль)

4. **Нажмите "Open Anyway"**
   - Рядом с сообщением об Analitic

### Решение 3: Через Terminal (для продвинутых)

```bash
# Удалить атрибут карантина
xattr -d com.apple.quarantine /Applications/Analitic.app

# Запустить
open /Applications/Analitic.app
```

---

## 🎯 ПЕРВЫЙ ЗАПУСК

Когда приложение откроется:

1. **Главное окно приложения**
   - Видны кнопки для анализа
   - Поле для логов

2. **Проверьте функциональность:**
   - ✅ Можно открывать файлы (Excel, TXT)
   - ✅ Работают кнопки анализа
   - ✅ Логи появляются в текстовой области
   - ✅ Результаты сохраняются

3. **Если всё работает - готово!** 🎉

---

## 🔧 РЕШЕНИЕ ПРОБЛЕМ

### ❌ "build-dmg.sh: No such file or directory"

```bash
# Проверьте что вы в правильной папке
pwd
# Должно показать: /Users/YourName/IdeaProjects/analitic-app

# Дайте права на выполнение
chmod +x build-dmg.sh

# Запустите
./build-dmg.sh
```

### ❌ "Java not found"

```bash
# Установить Java
brew install java21

# Проверить
java -version
```

### ❌ "Maven not found"

```bash
# Установить Maven
brew install maven

# Проверить
mvn --version
```

### ❌ "DMG file is corrupted"

```bash
# Удалить старый DMG
rm -f Analitic-1.0.dmg

# Пересобрать
./build-dmg.sh
```

### ❌ "Application won't start"

```bash
# Попробуйте запустить из Terminal
/Applications/Analitic.app/Contents/MacOS/analitic

# Это покажет ошибку в консоли
```

### ❌ "Out of memory"

Отредактируйте `build-dmg.sh`:

```bash
# Найдите строку
-Xmx1024m -Xms512m

# Измените на
-Xmx2048m -Xms1024m
```

Затем пересоберите: `./build-dmg.sh`

---

## 📊 ПАРАМЕТРЫ СБОРКИ

В скрипте `build-dmg.sh` можно изменить:

```bash
# Имя приложения
APP_NAME="Analitic App"

# Версия
VERSION="1.0"

# Памятб JVM (в Contents/MacOS/analitic)
-Xmx1024m -Xms512m
```

---

## 📦 РАСПРЕДЕЛЕНИЕ ПОЛЬЗОВАТЕЛЯМ

### Что отправить:

```
Analitic-1.0.dmg (28 MB)
```

### Инструкция для пользователя:

1. **Убедитесь что Java 21+ установлена**
   ```
   java -version
   ```

2. **Откройте Analitic-1.0.dmg**
   - Дважды кликните на файл

3. **Установите приложение**
   - Перетащите Analitic.app в Applications

4. **Запустите приложение**
   - Откройте Applications
   - Двойной клик на Analitic.app
   - Если спросит - нажмите Open

5. **Готово!** 🎉

---

## ✅ ФИНАЛЬНЫЙ CHECKLIST

- [ ] Java 21+ установлена
- [ ] Maven установлен
- [ ] Xcode Command Line Tools установлены
- [ ] Запустили `./build-dmg.sh`
- [ ] Создан файл `Analitic-1.0.dmg`
- [ ] DMG можно открыть в Finder
- [ ] Приложение установлено в Applications
- [ ] Приложение запускается
- [ ] Интерфейс загружается корректно
- [ ] Функции работают

---

## 🎉 ГОТОВО!

Вы успешно создали и установили DMG приложение!

Теперь можете поделиться `Analitic-1.0.dmg` с другими пользователями macOS.


