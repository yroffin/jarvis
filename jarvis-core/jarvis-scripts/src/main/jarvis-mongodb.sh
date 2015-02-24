#!/bin/bash
#
# Perform jarvis operation
#
# Note : this script must run on any platform
# - linux
# - windows (shell.w32-ix86 based Cf. http://sourceforge.net/projects/win-bash)
#

# Description : cleanup
# Arguments: None
# Returns: None
cleanup()
{
	return 0
}

# Description : find mongod executable
# Arguments: None
# Returns: None
findMongodbExecutable()
{
	# first find it locally (in design mode)
	for executable in `cd "${SCRIPT_WORKSPACE}" && find ../../../jarvis-mongodb/target -type f -name 'mongod.exe' 2>/dev/null`
	do
		echo ${SCRIPT_WORKSPACE}/`dirname $executable`
		return
	done
	# find it in subdirs
	for executable in `cd "${SCRIPT_WORKSPACE}" && find ../mongodb -type f -name 'mongod.exe' 2>/dev/null`
	do
		echo ${SCRIPT_WORKSPACE}/`dirname $executable`
		return
	done
	# find it in subdirs
	for executable in `cd "${SCRIPT_WORKSPACE}" && find ../../mongodb -type f -name 'mongod.exe' 2>/dev/null`
	do
		echo ${SCRIPT_WORKSPACE}/`dirname $executable`
		return
	done
}

echo CWD: `pwd`
export SCRIPT_WORKSPACE="`pwd`"

export MONGO_HOME="`findMongodbExecutable`"

set | grep SCRIPT_WORKSPACE
set | grep MONGO_HOME

export JARVIS_LOGS="${TEMP}/logs"
echo LOGS: "${JARVIS_LOGS}"
mkdir -p "${JARVIS_LOGS}"

export JARVIS_DATA="${TEMP}/data"
echo LOGS: "${JARVIS_DATA}"
mkdir -p "${JARVIS_DATA}/mongodb"

cd "${JARVIS_DATA}"
"${MONGO_HOME}/mongod.exe" --dbpath "${JARVIS_DATA}/mongodb"
#"${MONGO_HOME}/mongod.exe" --httpinterface --rest --dbpath "${JARVIS_DATA}/mongodb"
exit $?
