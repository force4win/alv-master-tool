@echo off
echo ==========================================
echo Generando ejecutable AlvMasterTool...
echo ==========================================

echo.
echo [PASO 1/2] Limpiando y construyendo imagen base (Maven + JLink)...
call mvn clean javafx:jlink
if %errorlevel% neq 0 (
    echo [ERROR] Fallo en la construccion con Maven. Revisa los errores arriba.
    pause
    exit /b %errorlevel%
)

echo.
echo [PASO 2/2] Generando ejecutable nativo (JPackage)...
:: Limpiar carpeta de destino anterior para evitar conflictos
if exist "target\installer" rmdir /s /q "target\installer"

:: Comando JPackage
"C:\Program Files\Java\jdk-21\bin\jpackage.exe" --type app-image --dest target/installer --name AlvMasterTool --runtime-image target/alv-master-tool --module com.alv.mastertools/com.alv.mastertools.App

if %errorlevel% neq 0 (
    echo [ERROR] Fallo al ejecutar JPackage.
    pause
    exit /b %errorlevel%
)

echo.
echo ==========================================
echo [EXITO] Aplicacion generada correctamente.
echo Ubicacion: target\installer\AlvMasterTool\AlvMasterTool.exe
echo ==========================================
echo.
pause
