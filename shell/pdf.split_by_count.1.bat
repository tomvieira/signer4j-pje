@echo off
set input=%~1
set filename=%~n1
set filename=%filename: =_%
set output=%PJEOFFICE_HOME%\watch\pdf.split_by_count.1.%filename%
set command="echo %input% > "%output%" && echo 1 >> "%output%""
cmd /U /C %command%

