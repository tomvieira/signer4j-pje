@echo off
set input=%~1
set filename=%~n1
set filename=%filename: =_%
set output=%PJEOFFICE_HOME%\watch\pdf.split_by_parity.even.%filename%
set command="echo %input% > "%output%" && echo true >> "%output%""
cmd /U /C %command%
