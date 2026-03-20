@echo off
REM Build script for TA Recruitment System
REM Compiles all Java source files to WEB-INF/classes

setlocal enabledelayedexpansion

REM Set the output directory
set OUTPUT_DIR=WEB-INF\classes

REM Create output directory if it doesn't exist
if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"

REM Get Jakarta Servlet JAR from Tomcat lib
set TOMCAT_LIB=..\..\lib
set CLASSPATH=%TOMCAT_LIB%\jakarta.servlet-api-6.0.0.jar;%TOMCAT_LIB%\jakarta.jsp-api-3.1.0.jar;%TOMCAT_LIB%\jakarta.el-api-5.0.0.jar

echo Compiling Java source files...
echo Output directory: %OUTPUT_DIR%
echo Classpath: %CLASSPATH%

REM Compile all Java files
javac -d "%OUTPUT_DIR%" -cp "%CLASSPATH%" ^
  src\com\bupt\tarecruitment\model\*.java ^
  src\com\bupt\tarecruitment\dao\*.java ^
  src\com\bupt\tarecruitment\service\*.java ^
  src\com\bupt\tarecruitment\filter\*.java ^
  src\com\bupt\tarecruitment\servlet\*.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo Build completed successfully!
    echo Compiled classes are in: %OUTPUT_DIR%
) else (
    echo.
    echo Build failed with errors!
    exit /b 1
)

endlocal
