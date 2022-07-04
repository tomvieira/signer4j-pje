#!/bin/bash

currentDIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

cd $currentDIR

chmod 755 ffmpeg.exe

chmod 755 ./jre/bin/java

rm -rf *.log*

./jre/bin/java -Dpjeoffice_home=$currentDIR -Dffmpeg_home=$currentDIR -jar signer4j-pje.jar