set SCRIPT_WORKSPACE=`dirname ${0}`
cd ${SCRIPT_WORKSPACE}
PWD
echo '**' Running script "${SCRIPT_WORKSPACE}/jarvis-java-engine.sh" $*
bash "${SCRIPT_WORKSPACE}/jarvis-java-engine.sh" $*
