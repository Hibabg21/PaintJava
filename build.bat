@echo off
if exist out rmdir /s /q out
mkdir out
javac -encoding UTF-8 -d out *.java || exit /b 1
copy /y *.properties out >nul
jar cfe Paint.jar PaintApp -C out .
echo OK : java -jar Paint.jar
