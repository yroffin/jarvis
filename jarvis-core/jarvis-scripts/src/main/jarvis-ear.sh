#!/bin/bash
#
# Perform jarvis ear launcher
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
findWindowsExecutable()
{
	# first find it locally (in design mode)
	for executable in `cd "${SCRIPT_WORKSPACE}" && find ../../../../jarvis-windows/WindowsJarvisMicrophone/bin/Debug -type f -name 'WindowsJarvisMicrophone.exe' 2>/dev/null`
	do
		echo ${SCRIPT_WORKSPACE}/`dirname $executable`
		return
	done
	# find it in subdirs
	for executable in `cd "${SCRIPT_WORKSPACE}" && find ../windows -type f -name 'WindowsJarvisMicrophone.exe' 2>/dev/null`
	do
		echo ${SCRIPT_WORKSPACE}/`dirname $executable`
		return
	done
	# find it in subdirs
	for executable in `cd "${SCRIPT_WORKSPACE}" && find ../../windows -type f -name 'WindowsJarvisMicrophone.exe' 2>/dev/null`
	do
		echo ${SCRIPT_WORKSPACE}/`dirname $executable`
		return
	done
}

# Description : find node executable
# Arguments: None
# Returns: None
findModel()
{
	# first find it locally (in design mode)
	[ -d "${SCRIPT_WORKSPACE}/../../../../jarvis-windows/model" ] && echo "${SCRIPT_WORKSPACE}/../../../../jarvis-windows"
	# find it in subdirs
	[ -d "${SCRIPT_WORKSPACE}/model" ] && echo "${SCRIPT_WORKSPACE}"
	[ -d "${SCRIPT_WORKSPACE}/../model" ] && echo "${SCRIPT_WORKSPACE}/.."
}

echo CWD: `pwd`
export SCRIPT_WORKSPACE="`pwd`"

export TARGET_HOME="`findWindowsExecutable`"
export MODELS="`findModel`"

set | grep SCRIPT_WORKSPACE
set | grep TARGET
set | grep MODELS

export JARVIS_LOGS="${TEMP}/logs"
echo LOGS: "${JARVIS_LOGS}"
mkdir -p "${JARVIS_LOGS}"

cd "${NODEJS_HOME}"
cd "${TARGET_HOME}" && WindowsJarvisMicrophone.exe -DJARVIS_LANGAGE_DIR="${MODELS}"
exit $?
