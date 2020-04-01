@rem %1 - packet city name
@rem %2 - design city name
@rem %3 - download url
call gradlew.bat new_city -Pcity_name=%1 -Pdesign_name=%2 -Purl=%3 -stacktrace
call gradlew.bat clean
call gradlew.bat generate -Pprj=%1 --stacktrace
