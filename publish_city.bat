@rem %1 - city name
SET SLAVE_AAPT_TIMEOUT=60
call gradlew.bat clean
call gradlew.bat publishApk%1Release
