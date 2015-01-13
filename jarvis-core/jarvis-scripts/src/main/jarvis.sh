#!/bin/bash
#
# Perform jarvis operation
#
# Note : this script must run on any platform
# - linux
# - windows (shell.w32-ix86 based Cf. http://sourceforge.net/projects/win-bash)
#

# Description : find node executable
# Arguments: None
# Returns: None
cleanup()
{
	return 0
}

# Description : find node executable
# Arguments: None
# Returns: None
findNodeExecutable()
{
	# first find it locally (in design mode)
	for executable in `cd "${SCRIPT_WORKSPACE}" && find ../../../jarvis-nodejs/node -type f -name 'node.exe' 2>/dev/null`
	do
		echo ${SCRIPT_WORKSPACE}/`dirname $executable`
		return
	done
	# find it in subdirs
	for executable in `cd "${SCRIPT_WORKSPACE}" && find ../node -type f -name 'node.exe' 2>/dev/null`
	do
		echo ${SCRIPT_WORKSPACE}/`dirname $executable`
		return
	done
	# find it in subdirs
	for executable in `cd "${SCRIPT_WORKSPACE}" && find ../../node -type f -name 'node.exe' 2>/dev/null`
	do
		echo ${SCRIPT_WORKSPACE}/`dirname $executable`
		return
	done
}

# Description : find node executable
# Arguments: None
# Returns: None
findPythonExecutable()
{
	# first find it locally (in design mode)
	for executable in `cd "${SCRIPT_WORKSPACE}" && find ../../../jarvis-nodejs/python -type f -name 'python.exe' 2>/dev/null`
	do
		echo ${SCRIPT_WORKSPACE}/`dirname $executable`
		return
	done
	# find it in subdirs
	for executable in `cd "${SCRIPT_WORKSPACE}" && find ../python -type f -name 'python.exe' 2>/dev/null`
	do
		echo ${SCRIPT_WORKSPACE}/`dirname $executable`
		return
	done
	# find it in subdirs
	for executable in `cd "${SCRIPT_WORKSPACE}" && find ../../python -type f -name 'python.exe' 2>/dev/null`
	do
		echo ${SCRIPT_WORKSPACE}/`dirname $executable`
		return
	done
}

# Description : find node executable
# Arguments: None
# Returns: None
findNpmExecutable()
{
	# first find it locally (in design mode)
	for executable in `cd "${SCRIPT_WORKSPACE}" && find ../../../jarvis-nodejs/node -type f -name 'npm.cmd' 2>/dev/null`
	do
		echo ${SCRIPT_WORKSPACE}/`dirname $executable`
		return
	done
	# find it in subdirs
	for executable in `cd "${SCRIPT_WORKSPACE}" && find ../node -type f -name 'npm.cmd' 2>/dev/null`
	do
		echo ${SCRIPT_WORKSPACE}/`dirname $executable`
		return
	done
	# find it in subdirs
	for executable in `cd "${SCRIPT_WORKSPACE}" && find ../../node -type f -name 'npm.cmd' 2>/dev/null`
	do
		echo ${SCRIPT_WORKSPACE}/`dirname $executable`
		return
	done
}

# Description : find node executable
# Arguments: None
# Returns: None
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

echo CWD: `pwd`
export SCRIPT_WORKSPACE="`pwd`"

export NODE_HOME="`findNodeExecutable`"
export PYTHON_HOME="`findPythonExecutable`"
export NPM_HOME="`findNpmExecutable`"
export NODEJS_HOME="`findServerJs`"
export NODE_PATH="${NODE_HOME}/node_modules/npm/node_modules":"${NODE_HOME}/node_modules"
export PATH="${PYTHON_HOME}:${NODE_HOME}:${NPM_HOME}:${NODE_HOME}/node_modules/npm/bin/node-gyp-bin:${PATH}"
echo "PATH: ${PATH}"

set | grep SCRIPT_WORKSPACE
set | grep NODE
set | grep NPM

export JARVIS_LOGS="${TEMP}/logs"
echo LOGS: "${JARVIS_LOGS}"
mkdir -p "${JARVIS_LOGS}"

cd "${NODEJS_HOME}" && ls -lrt

"${NODE_HOME}/node.exe" "${NPM_HOME}/npm-cli.js" install
[ ! -f "${NODE_HOME}/node.exe" ] && {
	exit -1
}

export NODE_PATH="${NODEJS_HOME}"
echo Running "${NODE_HOME}/node.exe"
"${NODE_HOME}/node.exe" "${NODEJS_HOME}/jarvis-bootstrap.js"
exit $?
