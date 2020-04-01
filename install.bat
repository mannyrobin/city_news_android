@echo off
setlocal
:PROMPT
SET /P AREYOUSURE=INSTALL! Are you sure (y/[n])?
IF /I "%AREYOUSURE%" NEQ "Y" GOTO END

@echo on
adb install -r %1


:END
endlocal




