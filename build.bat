cd /D %~dp0
cd ..
cd utils4j
cmd /c "mvn clean"
cmd /c "mvn install"
cd ..
cd filehandler4j
cmd /c "mvn clean"
cmd /c "mvn install"
cd..
cd pdfhandler4j
cmd /c "mvn clean"
cmd /c "mvn install"
cd..
cd videohandler4j
cmd /c "mvn clean"
cmd /c "mvn install"
cd..
cd cutplayer4jfx
cmd /c "mvn clean"
cmd /c "mvn install"
cd..
cd progress4j
cmd /c "mvn clean"
cmd /c "mvn install"
cd..
cd taskresolver4j
cmd /c "mvn clean"
cmd /c "mvn install"
cd..
cd signer4j
cmd /c "mvn clean"
cmd /c "mvn install"
cd..
cd signer4j-pje
cmd /c "mvn clean"
cmd /c "mvn install"

cd setup
del /Q /F *.jar

xcopy ..\..\cutplayer4jfx\target\*with-dependencies.jar .
xcopy ..\target\*with-dependencies.jar .
rename cutplayer4jfx* cutplayer4jfx.jar
rename signer4j-pje* signer4j-pje.jar
del /Q /F ..\install\pjeoffice-pro*.exe
iscc ..\install\inno-setup.iss

echo FIM


