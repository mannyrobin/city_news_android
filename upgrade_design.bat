@rem %1 - packet city name
@rem %2 - design city name
@rem %3 - download url
call gradlew.bat upgrade_design_city -Pcity_name=%1 -Pdesign_name=%2 -Purl=%3 -stacktrace