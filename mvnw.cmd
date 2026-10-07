@REM ----------------------------------------------------------------------------
@REM Maven Wrapper startup batch script
@REM ----------------------------------------------------------------------------
@echo off

set MAVEN_OPTS=%MAVEN_OPTS% -Xmx512m

set WRAPPER_JAR="%~dp0.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

@REM set DOWNLOAD_URL="https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar"
set DOWNLOAD_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar

if not exist %WRAPPER_JAR% (
    echo Downloading Maven wrapper jar...
    powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object System.Net.WebClient).DownloadFile(%DOWNLOAD_URL%, %WRAPPER_JAR%)"
)

@REM java %MAVEN_OPTS% -cp %WRAPPER_JAR% %WRAPPER_LAUNCHER% %*
java -Dmaven.multiModuleProjectDirectory="%~dp0." %MAVEN_OPTS% -cp "%WRAPPER_JAR%" %WRAPPER_LAUNCHER% %*