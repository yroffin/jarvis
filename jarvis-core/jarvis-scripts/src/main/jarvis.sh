#!/bin/bash
#
# Perform jarvis operation
#
# Note : this script must run on any platform
# - linux
# - windows (shell.w32-ix86 based Cf. http://sourceforge.net/projects/win-bash)
#
# Prerequisite
# - node js installed and runnable
# - npm installed and runnable
#

# Description : clean environnement
# Arguments: None
# Returns: None
cleanup()
{
	return 0
}

# Description : find bootstrap jarvis script
# Arguments: None
# Returns: jarvis script directory
findServerJs()
{
	# first find it locally (in design mode)
	for executable in `cd "${SCRIPT_WORKSPACE}" && find ../../../jarvis-nodejs/src/main/nodejs -type f -name 'jarvis-bootstrap.js' 2>/dev/null`
	do
		echo ${SCRIPT_WORKSPACE}/`dirname $executable`
		return
	done
	# find it in subdirs
	for executable in `cd "${SCRIPT_WORKSPACE}" && find ../nodejs -type f -name 'jarvis-bootstrap.js' 2>/dev/null`
	do
		echo ${SCRIPT_WORKSPACE}/`dirname $executable`
		return
	done
}

# fix nodejs environnement
NODE_BINARY=`which node.exe`
NPM_BINARY=`which npm.cmd`

NODE_VERSION=`"${NODE_BINARY}" -v`
echo '***' ${NODE_BINARY} '***' ${NODE_VERSION}

NPM_VERSION=`"${NPM_BINARY}" -version`
echo '***' ${NPM_BINARY} '***' ${NPM_VERSION}

# fix jarvis environnement
echo CWD: `pwd`
export SCRIPT_WORKSPACE="`pwd`"
export JARVIS_BOOTSTRAP_HOME="`findServerJs`"
echo '***' ${JARVIS_BOOTSTRAP_HOME} '***' ${JARVIS_BOOTSTRAP_VERSION}


export JARVIS_LOGS="${TEMP}/logs"
mkdir -p "${JARVIS_LOGS}"
echo LOGS: "${JARVIS_LOGS}"

# install npm modules if needed
cd "${JARVIS_BOOTSTRAP_HOME}" && ls -lrt
cd "${JARVIS_BOOTSTRAP_HOME}" && "${NPM_BINARY}" install

# check if install is ok
[ "${?}" -ne 0 ] && {
	exit -1
}

export NODE_PATH="${JARVIS_BOOTSTRAP_HOME};${SCRIPT_WORKSPACE}"
echo Running "${NODE_HOME}/node.exe"
"${NODE_BINARY}" "${JARVIS_BOOTSTRAP_HOME}/jarvis-bootstrap.js"
exit $?
