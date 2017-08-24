// Methods in this file will end up as object methods on the object that load returns.
def lookAtThis(String whoAreYou) {
    echo "Look at this, ${whoAreYou}! You loaded this from another file!"
}

def prepare(String body) {
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
            // Get some code from a GitHub repository
            git credentialsId: 'github-yroffin', url: 'https://github.com/yroffin/jarvis.git'
            sh '''
            mkdir -p ${WORKSPACE}/logs
            '''
      }
}
