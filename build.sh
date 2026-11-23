#!/bin/sh
rm -rf out && mkdir out
javac -encoding UTF-8 -d out *.java && cp *.properties out/ && jar cfe Paint.jar PaintApp -C out . && echo "OK : java -jar Paint.jar"
