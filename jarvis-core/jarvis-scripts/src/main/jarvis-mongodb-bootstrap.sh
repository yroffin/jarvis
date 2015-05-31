@echo off
export SCRIPT_WORKSPACE=`pwd`/
cd "%SCRIPT_WORKSPACE%"
echo ** Running script ${SCRIPT_WORKSPACE}jarvis-mongodb.sh %1 %2 %3 %4 %5 %6 %7 %8 %9
bash "${SCRIPT_WORKSPACE}jarvis-mongodb.sh" %1 %2 %3 %4 %5 %6 %7 %8 %9
