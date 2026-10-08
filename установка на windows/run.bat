#!/bin/bash
# ============================================================
# Analitic App - Launcher for macOS/Linux
# Java 21+ required
# ============================================================

echo ""
echo "============================================================"
echo "  ANALITIC APP - Data Analysis Tool"
echo "============================================================"
echo ""

# Проверка Java
if ! command -v java &> /dev/null; then
    echo ""
    echo "ERROR: Java is not installed or not in PATH"
    echo ""
    echo "Please install Java 21 or higher:"
    echo "  macOS:   brew install openjdk@21"
    echo "  Ubuntu:  sudo apt install openjdk-21-jdk"
    echo ""
    exit 1
fi

# Получить версию Java
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
echo "Java version: $JAVA_VERSION"

# Параметры JVM
JVM_MEMORY="-Xms512m -Xmx2048m"
JAVAFX_OPTS="-Dprism.order=sw -Dprism.verbose=false"
MODULE_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.time=ALL-UNNAMED"
PERFORMANCE_OPTS="-XX:+UseG1GC -XX:+UseStringDeduplication"

# Поиск JAR
JAR_FILE=""
if [ -f "analitic-1.0-SNAPSHOT.jar" ]; then
    JAR_FILE="analitic-1.0-SNAPSHOT.jar"
elif [ -f "target/analitic-1.0-SNAPSHOT.jar" ]; then
    JAR_FILE="target/analitic-1.0-SNAPSHOT.jar"
else
    JAR_FILE=$(ls *.jar 2>/dev/null | head -n 1)
fi

if [ -z "$JAR_FILE" ]; then
    echo ""
    echo "ERROR: JAR file not found!"
    echo ""
    echo "Please build the application first:"
    echo "  mvn clean package"
    echo ""
    exit 1
fi

echo "Found JAR: $JAR_FILE"
echo ""
echo "Starting application..."
echo "============================================================"
echo ""

# Запуск
java $JVM_MEMORY $JAVAFX_OPTS $MODULE_OPTS $PERFORMANCE_OPTS -jar "$JAR_FILE"

EXIT_CODE=$?
if [ $EXIT_CODE -ne 0 ]; then
    echo ""
    echo "============================================================"
    echo "ERROR: Application exited with code $EXIT_CODE"
    echo "============================================================"
    echo ""
fi

exit $EXIT_CODE