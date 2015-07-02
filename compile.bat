dir /s /B *.java > sources.txt
mkdir bin
javac -g -verbose -d ./bin @sources.txt
copy MANIFEST.MF bin /y
cd bin
jar cvfm TP2-Wilson.jar ./MANIFEST.mf ./br
cd ..
