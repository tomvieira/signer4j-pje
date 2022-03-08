@echo off
set filename=%~n1
set filename=%filename: =_%
echo %~1 > "%PJEOFFICE_HOME%\watch\cnj.assinador.%filename%"