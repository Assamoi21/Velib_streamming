@echo off
set JAVA_HOME=%ProgramFiles%\Eclipse Adoptium\jdk-11.0.31.11-hotspot
set HADOOP_HOME=C:\hadoop
set PATH=%JAVA_HOME%\bin;%HADOOP_HOME%\bin;%PATH%
cd /d "%~dp0"
C:\temp\sbt\sbt\bin\sbt.bat --batch "runMain Main"
