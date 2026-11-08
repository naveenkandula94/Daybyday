@rem
@rem Gradle startup script for Windows
@rem
@echo off
set DIR=%~dp0
set APP_BASE_NAME=%~n0
set CLASSPATH=%DIR%\gradle\wrapper\gradle-wrapper.jar

if not exist "%CLASSPATH%" (
  echo ERROR: gradle\wrapper\gradle-wrapper.jar is missing.
  echo Run this project through Newly's APK builder, which can restore missing wrapper files.
  exit /b 1
)

if defined JAVA_HOME (
  set JAVA_EXE=%JAVA_HOME%\bin\java.exe
) else (
  set JAVA_EXE=java.exe
)

"%JAVA_EXE%" %JAVA_OPTS% %GRADLE_OPTS% -Dorg.gradle.appname=%APP_BASE_NAME% -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
