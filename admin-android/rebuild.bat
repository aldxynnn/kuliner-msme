@echo off
echo Cleaning Android build...
call gradlew.bat clean

echo Building debug APK...
call gradlew.bat assembleDebug --console=plain

echo Build complete!
pause
