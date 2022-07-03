@echo off
cls
echo *********************************************************************************
echo ** FERRAMENTAS E NOTAS NECESSARIAS PARA EXECUCAO DESTE SCRIPT
echo *********************************************************************************
echo .  Wget for Windows
echo .    - Motivo: Usado para download das versoes do JRE para embutir na instalacao
echo .    - Link: https://builtvisible.com/download-your-website-with-wget/
echo .    - Nota: Adicionar a pasta do binario ao PATH do sistema
echo .  7zip
echo .    - Motivo: Usado para descopmactacao de arquivos .tar.gz baixados pelo wget
echo .    - Link: https://www.7-zip.org/download.html
echo .    - Nota: Adicionar a pasta do binario 7z ao PATH do sistema
echo .  Inno Setup
echo .    - Motivo: Usado para geracao de um instalador para windows
echo .    - Link: https://jrsoftware.org/isinfo.php
echo .    - Nota: Adicionar a pasta do binario incc.exe ao PATH do sistema
echo .  Maven
echo .    - Motivo: Usado para compilacao e controle de dependencias
echo .    - Link: https://maven.apache.org/
echo .    - Nota 1: Adicionar a pasta 'bin' ao PATH do sistema
echo .    - Nota 2: Criar a variavel de ambiente MAVEN_HOME referenciando a pasta pai de 'bin'
echo .  JDK 1.8.301
echo .    - Motivo: Toolkit para compilacao para plataforma JAVA
echo .    - Link: https://www.oracle.com/br/java/technologies/javase/javase8u211-later-archive-downloads.html
echo .    - Nota: Criar a variavel de ambiente JAVA_HOME referenciando a pasta raiz do JDK
echo *********************************************************************************

pause

set project_path=%~dp0..\..\..\..\signer4j-pje

set jre32URL=https://cnj-pje-programs.s3.sa-east-1.amazonaws.com/jre-8u301-windows-i586.tar.gz

cd /D %project_path%
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

cd /D %project_path%\packages\windows\setup

del /Q /F *.jar

rmdir /Q /S .\web

xcopy %project_path%\..\cutplayer4jfx\target\*with-dependencies.jar .

xcopy %project_path%\target\*with-dependencies.jar .

xcopy /E /H /C /I ..\..\common\setup\ .

rename cutplayer4jfx* cutplayer4jfx.jar

rename signer4j-pje* signer4j-pje.jar

wget %jre32URL%

7z x "*.tar.gz" -so | 7z x -aoa -si -ttar -o"tmp"

del /Q /F *.tar.gz

cd tmp

for /d %%D in (*) do rename %%~D jre

move .\jre ..\

cd ..

rmdir /Q /S .\tmp\

del /Q /F ..\x32\pjeoffice-pro*.exe

del /Q /F ..\x64\pjeoffice-pro*.exe

iscc ../infra/inno-setup.iss

del /Q /F *.jar

rmdir /Q /S web

echo FIM
