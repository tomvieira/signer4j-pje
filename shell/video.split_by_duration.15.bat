@echo off
set input=%~1
set filename=%~n1
set filename=%filename: =_%
set output=%PJEOFFICE_HOME%\watch\video.split_by_duration.15.%filename%
set command="echo %input% > "%output%" && echo 15 >> "%output%""
cmd /U /C %command%