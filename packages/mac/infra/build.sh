#!/bin/bash
clear
echo "*********************************************************************************"
echo "** FERRAMENTAS E NOTAS NECESSARIAS PARA EXECUCAO DESTE SCRIPT"
echo "*********************************************************************************"
echo ".  CURL for MAC"
echo ".    - Motivo: Usado para download das versoes do JRE para embutir na instalacao"
echo ".    - Link: "
echo ".    - Nota: É comum já vir instalado no sistema operacional"
echo ".  Tar"
echo ".    - Motivo: Usado para descopmactacao de arquivos .tar.gz baixados pelo wget"
echo ".    - Link: "
echo ".    - Nota: É comum já vir instalado no sistema operacional"
echo ".  Inno Setup"
echo ".    - Motivo: Usado para geracao de um instalador para windows"
echo ".    - Link: https://jrsoftware.org/isinfo.php"
echo ".    - Nota: Adicionar a pasta do binario incc.exe ao PATH do sistema"
echo ".  Maven"
echo ".    - Motivo: Usado para compilacao e controle de dependencias"
echo ".    - Link: https://maven.apache.org/"
echo ".    - Nota 1: Adicionar a pasta 'bin' ao PATH do sistema (editar /etc/paths)"
echo ".    - Nota 2: Criar a variavel de ambiente MAVEN_HOME referenciando a pasta pai de 'bin'"
echo ".  JDK 1.8.301"
echo ".    - Motivo: Toolkit para compilacao para plataforma JAVA"
echo ".    - Link: https://www.oracle.com/br/java/technologies/javase/javase8u211-later-archive-downloads.html"
echo ".    - Nota: Criar a variavel de ambiente JAVA_HOME referenciando a pasta raiz do JDK"
echo "*********************************************************************************"

version=2.1.0

infra_path="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_321.jdk/Contents/Home
export MAVEN_HOME=/Users/leonardo/Documents/maven

workspace_path=$infra_path/../../../..

jreURL=https://cnj-pje-programs.s3.sa-east-1.amazonaws.com/jre-8u301-macosx-x64.tar.gz

read -p "Pressione qualquer tecla para continuar..."

echo $workspace_path


function buildProject() {
	cd $workspace_path/utils4j
	mvn clean
	mvn install
	cd $workspace_path/filehandler4j
	mvn clean
	mvn install
	cd $workspace_path/pdfhandler4j
	mvn clean
	mvn install
	cd $workspace_path/videohandler4j
	mvn clean
	mvn install
	cd $workspace_path/cutplayer4jfx
	mvn clean
	mvn install
	cd $workspace_path/progress4j
	mvn clean
	mvn install
	cd $workspace_path/taskresolver4j
	mvn clean
	mvn install
	cd $workspace_path/signer4j
	mvn clean
	mvn install
	cd $workspace_path/signer4j-pje
	mvn clean
	mvn install
}


function downloadJRE() {
    cd $infra_path/../setup/Contents/MacOS
	rm -rf ./jre
	rm -rf tmp
	mkdir tmp
	cd tmp
	curl -o jre.tar.gz $jreURL
	tar -zxf *.tar.gz
	rm *.tar.gz
	mv * jre_root
	mv jre_root/Contents/Home ../jre
	cd ..
	rm -rf tmp
	chmod 755 ./jre/bin/java
}

function setupJARs() {
    cd $infra_path/../setup/Contents/MacOS
	rm -rf *.jar
	rm -rf web
	cp $workspace_path/cutplayer4jfx/target/*with-dependencies.jar .
	cp $workspace_path/signer4j-pje/target/*with-dependencies.jar .
	cp -R $workspace_path/signer4j-pje/packages/common/setup/* .
	mv cutplayer4jfx* cutplayer4jfx.jar
	mv signer4j-pje* signer4j-pje.jar
	chmod 775 pjeoffice-pro.sh
}

function buildApp() {
	cd $workspace_path/signer4j-pje/packages/mac/x64
	rm -rf pjeoffice-pro-v$version.app
	mkdir pjeoffice-pro-v$version.app
	cp -R $workspace_path/signer4j-pje/packages/mac/setup/* pjeoffice-pro-v$version.app
}

buildProject

setupJARs

downloadJRE

buildApp

