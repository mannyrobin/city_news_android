@echo off
setlocal
:PROMPT
SET /P AREYOUSURE=UNINSTALL! Are you sure (y/[n])?
IF /I "%AREYOUSURE%" NEQ "Y" GOTO END

@echo on
adb uninstall ru.moygorod.zelenograd


:END
endlocal




