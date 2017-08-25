// Methods in this file will end up as object methods on the object that load returns.
def lookAtThis(String whoAreYou) {
    echo "Look !!! at this, ${whoAreYou}! You loaded this from another file!"
}

/**
 * prepare tools
 */
def prepare() {
      echo "Prepare with ${body}"

      env.MASTER_NODE = '192.168.1.111';
      env.SLAVE_NODE  = '192.168.1.73';
      env.SHIRKA_NODE  = '192.168.1.38';
      env.PIZERO_NODE = '192.168.1.47';

      stage('node') {
            // NodeJS
            tool name: 'NodeJS', type: 'com.cloudbees.jenkins.plugins.customtools.CustomTool'
            env.PATH='/usr/bin:/bin:/var/jenkins_home/tools/com.cloudbees.jenkins.plugins.customtools.CustomTool/NodeJS/node-v6.10.0-linux-x64/bin'
      }

      stage('maven') {
            // Maven
            tool name: 'M3', type: 'maven'
            mvnHome = tool 'M3'
      }

      stage('git') {
            sh '''
                  mkdir -p ${WORKSPACE}/logs
            '''
      }
}

/**
 * check
 */
def check() {
      print env.MASTER_NODE
}

return this;