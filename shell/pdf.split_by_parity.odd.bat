@echo off
setlocal EnableDelayedExpansion
(set \n=^
%=Do not remove this line=%
)
echo %~1!\n!false > %PJEOFFICE_HOME%\watch\pdf.split_by_parity.odd.%~n1