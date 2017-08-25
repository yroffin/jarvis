/**
 * configure tools
 */
def configure() {
      /**
       * define ip target
       */
      env.MASTER_NODE = '192.168.1.111';
      env.SLAVE_NODE  = '192.168.1.73';
      env.SHIRKA_NODE  = '192.168.1.38';
      env.PIZERO_NODE = '192.168.1.47';
}

/**
 * prepare tools
 */
def prepare() {
      stage('Prepare NodeJS Suite') {
            // NodeJS
            tool name: 'NodeJS', type: 'com.cloudbees.jenkins.plugins.customtools.CustomTool'
            env.PATH='/usr/bin:/bin:/var/jenkins_home/tools/com.cloudbees.jenkins.plugins.customtools.CustomTool/NodeJS/node-v6.10.0-linux-x64/bin'
      }

      stage('Prepare maven tools') {
            // Maven
            tool name: 'M3', type: 'maven'
            mvnHome = tool 'M3'
      }

      stage('Create logs') {
            sh '''
                  mkdir -p ${WORKSPACE}/logs
            '''
      }

      stage('@angular/cli') {
            // NodeJS
            tool name: 'angular-cli', type: 'com.cloudbees.jenkins.plugins.customtools.CustomTool'
            sh '''
                  node --version
                  npm --version
                  npm config get cache
                  //npm install -g @angular/cli@latest
                  ng --version
            '''
      }
}

/**
 * buildBackOffice
 */
def buildGUI() {
      print "GUI build"
      stage('GUI') {
            dir('jarvis-core/jarvis-core-ui-ng') {
            sh '''
                  npm install > ${WORKSPACE}/logs/ui.stdout 2> ${WORKSPACE}/logs/ui.stderr
                  npm update > ${WORKSPACE}/logs/ui.stdout 2> ${WORKSPACE}/logs/ui.stderr
                  ng build --aot --prod --base-href /nui/ --output-path=src/main/resources/public/nui  > ${WORKSPACE}/logs/ui.stdout 2> ${WORKSPACE}/logs/ui.stderr
                  ls -lrt src/main/resources/public/nui > ${WORKSPACE}/logs/ui.stdout 2> ${WORKSPACE}/logs/ui.stderr
            '''
            }
      }
}

def buildSRV() {
      print "SRV build"
      stage('SRV') {
            dir('jarvis-core') {
                  if (isUnix()) {
                        sh "'${mvnHome}/bin/mvn' -Dmaven.test.failure.ignore clean install > ${WORKSPACE}/logs/server.stdout 2> ${WORKSPACE}/logs/server.stderr"
                  } else {
                        bat(/"${mvnHome}\bin\mvn" -Dmaven.test.failure.ignore clean install/)
                  }
            }
      }
}

return this;