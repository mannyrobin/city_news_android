@rem %1 - city name
call gradlew.bat clean
call gradlew.bat assemble%1Release
