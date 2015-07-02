#!/bin/bash
clear
find -name "*.java" > sources.txt
mkdir bin
eval javac -g -verbose -d ./bin @sources.txt
cp ./MANIFEST.mf ./bin
cd bin
eval jar cvfm TP2-Wilson.jar ./MANIFEST.mf ./br
cd ..

