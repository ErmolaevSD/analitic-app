# 🚀 ПОЛНОЕ РУКОВОДСТВО: JAR ДЛЯ WINDOWS

## 📦 ЧТО СОЗДАНО

Для вас подготовлено всё необходимое для запуска Analitic App на Windows:

```
analitic-app/
├── run.bat                    ← ЗАПУСК ПРИЛОЖЕНИЯ (дважды клик!)
├── WINDOWS_JAR_SETUP.md       ← Инструкция по установке
├── pom.xml                    ← Конфигурация для сборки
└── target/
    └── analitic-1.0-SNAPSHOT.jar  ← FAT JAR с всеми зависимостями
```

---

## ⚡ БЫСТРЫЙ СТАРТ (3 ШАГА)

### Шаг 1️⃣: Установите Java

Если Java еще не установлена:

1. Скачайте Java 21: [oracle.com](https://www.oracle.com/java/technologies/downloads/#java21)
2. Установите (выбрав опцию добавления в PATH)
3. Проверьте:
   ```cmd
   java -version
   ```

### Шаг 2️⃣: Соберите JAR (выполните ОДИН РАЗ)

```cmd
# Откройте CMD в папке проекта
mvn clean package -DskipTests

# Ждите 2-3 минуты...
# После завершения должен появиться файл:
# target/analitic-1.0-SNAPSHOT.jar
```

### Шаг 3️⃣: Запустите приложение

**Способ A: Дважды кликните на `run.bat`** ← САМЫЙ ПРОСТОЙ

**Способ B: Через CMD**
```cmd
java -jar target/analitic-1.0-SNAPSHOT.jar
```

---

## 📋 ЧТО ТАКОЕ JAR FILE?

- **JAR** = Java Archive
- **Fat JAR** = "толстый" JAR, содержит все зависимости внутри
- **Размер:** ~50-80 MB
- **Содержит:**
  - ✅ Весь код Analitic App
  - ✅ JavaFX (графический интерфейс)
  - ✅ Logback (логирование)
  - ✅ EasyExcel (работа с Excel)
  - ✅ Все остальные библиотеки

---

## 🖥️ СПОСОБЫ ЗАПУСКА НА WINDOWS

### Способ 1️⃣: run.bat (РЕКОМЕНДУЕТСЯ)

```
1. Дважды кликните на файл: run.bat
2. Приложение запустится в новом окне
3. Готово!
```

**Преимущества:**
- ✅ Самый простой способ
- ✅ Автоматическая проверка Java
- ✅ Оптимальные параметры памяти

### Способ 2️⃣: Command Prompt

```cmd
# Способ A: Если находитесь в папке проекта
mvn javafx:run

# Способ B: Если JAR уже собран
java -jar target/analitic-1.0-SNAPSHOT.jar

# Способ C: С установкой памяти
java -Xmx1024m -Xms512m -jar target/analitic-1.0-SNAPSHOT.jar
```

### Способ 3️⃣: Ярлык на рабочий стол

1. Найдите `run.bat`
2. Правый клик → "Отправить" → "Рабочий стол (создать ярлык)"
3. (Опционально) Отредактируйте ярлык, добавьте значок

---

## 🔧 ТРЕБОВАНИЯ ДЛЯ WINDOWS

| Компонент | Требование | Проверка |
|-----------|-----------|----------|
| **Java** | 21 или выше | `java -version` |
| **Память RAM** | Минимум 2 GB | - |
| **Диск** | Минимум 200 MB | - |
| **Windows** | Windows 7 SP1 или выше | - |

---

## 📝 ПРОЦЕСС СБОРКИ (ЧТО ПРОИСХОДИТ)

Когда вы запускаете `mvn clean package -DskipTests`:

```
1. clean - удаляет старые сборки
2. compile - компилирует Java код
3. package - собирает JAR файл
4. shade - добавляет все зависимости в JAR (делает его "толстым")
5. Создается файл: target/analitic-1.0-SNAPSHOT.jar

Результат: Один JAR файл (~60-80 MB) со всем необходимым
```

---

## 🐛 РЕШЕНИЕ ПРОБЛЕМ

### ❌ "Не найдена Java"

```
Решение:
1. Установите Java 21: https://oracle.com/java/
2. Добавьте в PATH:
   - Win+R → sysdm.cpl
   - Advanced tab → Environment Variables
   - Добавьте: C:\Program Files\Java\jdk-21\bin
3. Перезагрузитесь
```

### ❌ "JAR file not found"

```
Решение:
1. Откройте CMD
2. cd C:\путь\к\analitic-app
3. Выполните: mvn clean package -DskipTests
4. Ждите завершения (2-3 минуты)
5. Проверьте: dir target\*.jar
```

### ❌ "Out of memory"

```
Решение 1 - Отредактируйте run.bat:
Замените строку:
set JVM_MEMORY=-Xmx1024m -Xms512m

На:
set JVM_MEMORY=-Xmx2048m -Xms1024m

Решение 2 - Используйте команду запуска:
java -Xmx2048m -Xms1024m -jar analitic-1.0-SNAPSHOT.jar
```

### ❌ "Application takes too long to start"

```
Это нормально! JavaFX приложения могут загружаться 10-30 секунд
на первый запуск. На последующих запусках будет быстрее.
```

---

## 📊 СТРУКТУРА ГОТОВОЙ СБОРКИ

```
analitic-app/
│
├── 📄 run.bat                           ← Запуск (двойной клик)
├── 📄 WINDOWS_JAR_SETUP.md             ← Инструкция
├── 📄 pom.xml                          ← Maven конфиг
│
├── 📁 src/                             ← Исходный код
│   ├── main/
│   │   ├── java/ru/project/analitic/
│   │   └── resources/
│   └── test/
│
└── 📁 target/
    └── 📦 analitic-1.0-SNAPSHOT.jar   ← ЭТО ГЛАВНЫЙ ФАЙЛ!
                                         (содержит всё)
```

---

## 🚀 КОМАНДНАЯ СТРОКА (ШПАРГАЛКА)

```batch
REM Сборка проекта
mvn clean package -DskipTests

REM Запуск из CMD
java -jar target/analitic-1.0-SNAPSHOT.jar

REM Запуск с большей памятью
java -Xmx2048m -jar target/analitic-1.0-SNAPSHOT.jar

REM Проверка Java
java -version

REM Проверка Maven
mvn --version

REM Перейти в папку проекта
cd C:\путь\к\analitic-app

REM Посмотреть содержимое папки
dir

REM Очистить сборку
mvn clean
```

---

## ✅ CHECKLIST ГОТОВНОСТИ

Перед распределением приложения убедитесь:

- [ ] Java 21+ установлена на целевом компьютере
- [ ] Проект собран успешно (`mvn clean package`)
- [ ] JAR файл существует в `target/`
- [ ] `run.bat` находится в корне проекта
- [ ] Приложение запускается двойным кликом на `run.bat`
- [ ] Интерфейс загружается корректно
- [ ] Можно открывать файлы и работать с данными

---

## 📦 РАСПРЕДЕЛЕНИЕ ПРИЛОЖЕНИЯ

Чтобы поделиться приложением с другими пользователями:

### Вариант 1: Поделиться проектом

```
Отправьте всю папку:
- analitic-app/
  - run.bat
  - pom.xml
  - src/
  - target/analitic-1.0-SNAPSHOT.jar
  
Пользователь запустит: двойной клик на run.bat
```

### Вариант 2: Поделиться только JAR

```
Отправьте только:
1. analitic-1.0-SNAPSHOT.jar (из target/)
2. run.bat

Больше ничего не нужно!

Пользователь просто кликнет на run.bat
```

### Вариант 3: Упакуйте в ZIP

```
1. Создайте папку: Analitic-App/
2. Поместите:
   - run.bat
   - target/analitic-1.0-SNAPSHOT.jar
3. Отправьте ZIP архив пользователю
4. Пользователь распакует и кликнет run.bat
```

---

## 🎉 ГОТОВО!

Ваше приложение теперь готово для запуска на **любом Windows компьютере с Java 21+**!

```
Что нужно пользователю:
✅ Java 21+ (скачать один раз)
✅ Файл: run.bat или analitic-1.0-SNAPSHOT.jar

Что не нужно:
❌ Maven
❌ IDE (IntelliJ, Eclipse и т.д.)
❌ Исходный код
❌ Компилятор Java
```

---

**Дата создания:** 21 марта 2026  
**Статус:** ✅ READY FOR WINDOWS DEPLOYMENT  
**Версия приложения:** 1.0-SNAPSHOT  


