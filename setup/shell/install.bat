cd /D "%PJEOFFICE_HOME%/shell"
reg delete  HKEY_CLASSES_ROOT\SystemFileAssociations\.mp4\Shell\PjeOffice /f
reg delete  HKEY_CLASSES_ROOT\SystemFileAssociations\.pdf\Shell\PjeOffice /f
reg import pdf.reg
reg import mp4.reg
