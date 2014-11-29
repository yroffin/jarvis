SET NODE_HOME=W:\git-luna\jarvis-core\jarvis-nodejs\node
SET WORKSPACE=W:\git-luna\jarvis-core\jarvis-nodejs\src\main

SET NODE_PATH=%WORKSPACE%\nodejs\node_modules\npm\node_modules;%WORKSPACE%\nodejs\node_modules\npm

cd /d "%WORKSPACE%\nodejs"
"%NODE_HOME%\node.exe" "%WORKSPACE%\nodejs\server.js"