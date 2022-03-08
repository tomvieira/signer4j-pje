@echo off
setlocal EnableDelayedExpansion
(set \n=^
%=Do not remove this line=%
)
echo %~1!\n!true > %PJEOFFICE_HOME%\watch\pdf.split_by_parity.even.%~n1