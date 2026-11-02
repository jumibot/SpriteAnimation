@echo off
rem Compila la demo y las pruebas con javac (Java 21). No hace falta Gradle ni Maven.
setlocal
cd /d "%~dp0"
if exist out rmdir /s /q out
javac -encoding UTF-8 -d out\main src\main\java\*.java || exit /b 1
javac -encoding UTF-8 -cp out\main -d out\test src\test\java\*.java || exit /b 1
echo Compilado en out\
