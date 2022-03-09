@echo off
set input=%~1
set filename=%~n1
set filename=%filename: =_%
set output=%PJEOFFICE_HOME%\watch\pdf.split_by_parity.odd.%filename%
set command="echo %input% > "%output%" && echo false >> "%output%""
cmd /U /C %command%