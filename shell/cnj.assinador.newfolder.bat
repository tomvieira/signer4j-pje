@echo off
set input=%~1
set filename=%~n1
set filename=%filename: =_%
set output=%PJEOFFICE_HOME%\watch\cnj.assinador.%filename%
set command="echo %input% > "%output%" && echo newfolder >> "%output%""
cmd /U /C %command%

