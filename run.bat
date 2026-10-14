@echo off
REM Connect X - Compilation and Launch Script (Windows)

if not exist bin mkdir bin
echo Compiling Connect X Java files...
javac -d bin -sourcepath src src\com\connectx\Main.java

if %ERRORLEVEL% equ 0 (
    echo Launching Connect X...
    java -cp bin com.connectx.Main
) else (
    echo Compilation failed. Please ensure JDK 11+ is installed and 'javac' is on your PATH.
    pause
)
