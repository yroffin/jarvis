#!/bin/bash
#
# Perform jarvis engine
# - voice
# - aiml renderer
# - remote exec
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
findJarExecutable()
{
	# first find it locally (in design mode)
	for executable in `cd "${SCRIPT_WORKSPACE}" && find ../../../jarvis-java-engine/target -type f -name 'jarvis-java-engine-*.jar' 2>/dev/null`
	do
		echo ${SCRIPT_WORKSPACE}/`dirname $executable`
		return
	done
	# find it in subdirs
	for executable in `cd "${SCRIPT_WORKSPACE}" && find ../java -type f -name 'jarvis-java-engine-*.jar' 2>/dev/null`
	do
		echo ${SCRIPT_WORKSPACE}/`dirname $executable`
		return
	done
	# find it in subdirs
	for executable in `cd "${SCRIPT_WORKSPACE}" && find ../../java -type f -name 'jarvis-java-engine-*.jar' 2>/dev/null`
	do
		echo ${SCRIPT_WORKSPACE}/`dirname $executable`
		return
	done
}

echo CWD: `pwd`
export SCRIPT_WORKSPACE="`pwd`"

export TARGET_HOME="`findJarExecutable`"

set | grep SCRIPT_WORKSPACE
set | grep TARGET

export JARVIS_LOGS="${TEMP}/logs"
echo LOGS: "${JARVIS_LOGS}"
mkdir -p "${JARVIS_LOGS}"

cd "${NODEJS_HOME}"
[ -f "${TARGET_HOME}/jarvis-java-engine-0.0.1-SNAPSHOT-jar-with-dependencies.jar" ] && {
	export CLASSPATH="${TARGET_HOME}/jarvis-java-engine-0.0.1-SNAPSHOT-jar-with-dependencies.jar;${SCRIPT_WORKSPACE}"
	"${JAVA_HOME}/bin/java.exe" org.jarvis.main.core.main.JarvisClient $*
	exit $?
}
[ -f "${TARGET_HOME}/jarvis-java-engine-0.0.1-SNAPSHOT.jar" ] && {
	export CLASSPATH="${TARGET_HOME}/jarvis-java-engine-0.0.1-SNAPSHOT.jar;${SCRIPT_WORKSPACE}"
	"${JAVA_HOME}/bin/java.exe" org.jarvis.main.core.main.JarvisClient $*
	exit $?
}
exit $?
