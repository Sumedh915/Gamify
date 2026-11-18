@echo off
if not exist bin mkdir bin
dir /s /b src\*.java > sources.txt
javac -d bin @sources.txt || exit /b 1
del sources.txt
java -cp bin gamehub.Main
