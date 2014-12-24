@echo off
set SCRIPT_WORKSPACE_WITH_BACKSLASH=%~dp0
set SCRIPT_WORKSPACE=%SCRIPT_WORKSPACE_WITH_BACKSLASH:\=/%
cd /d "%SCRIPT_WORKSPACE%"
set PATH=%SCRIPT_WORKSPACE%shell.w32-ix86
echo ** Running script %SCRIPT_WORKSPACE%jarvis.sh %1 %2 %3 %4 %5 %6 %7 %8 %9
bash "%SCRIPT_WORKSPACE%jarvis.sh" %1 %2 %3 %4 %5 %6 %7 %8 %9
