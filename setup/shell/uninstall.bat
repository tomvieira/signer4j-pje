reg delete  "HKEY_CLASSES_ROOT\SystemFileAssociations\.mp4\Shell\PjeOffice" /f
reg delete  "HKEY_CLASSES_ROOT\SystemFileAssociations\.pdf\Shell\PjeOffice" /f
reg delete  "HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Control\Session Manager\Environment" /v PJEOFFICE_HOME /f
WMIC PROCESS WHERE "COMMANDLINE LIKE '%%PjeOfficePRO%%'" CALL TERMINATE