@echo off
reg delete  "HKEY_CLASSES_ROOT\SystemFileAssociations\.mp4\Shell\PjeOffice" /f
reg delete  "HKEY_CLASSES_ROOT\SystemFileAssociations\.pdf\Shell\PjeOffice" /f
DEL /F/Q/S "%HOMEDRIVE%%HOMEPATH%\ffmpeg.exe".
WMIC PROCESS WHERE "NAME like 'javaw.exe' AND COMMANDLINE LIKE '%%PjeOfficePRO%%'" CALL TERMINATE
DEL /F/Q/S "%HOMEDRIVE%%HOMEPATH%\.pjeoffice-pro" > NUL
RMDIR /Q/S "%HOMEDRIVE%%HOMEPATH%\.pjeoffice-pro"
