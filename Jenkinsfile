node {
  env.MASTER_NODE = '192.168.1.111';
  env.SLAVE_NODE  = '192.168.1.73';
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
  
  stage('@angular/cli') {
        // NodeJS
        tool name: 'angular-cli', type: 'com.cloudbees.jenkins.plugins.customtools.CustomTool'
        sh '''
            node --version
            npm --version
            npm config get cache
            npm install -g @angular/cli@latest
            ng --version
        '''
  }
  
  stage('ui and server') {
        dir('jarvis-core/jarvis-core-ui-ng') {
            sh '''
                npm install
                npm update
                ng build --aot --prod --base-href /nui/ --output-path=src/main/resources/public/nui  > ${WORKSPACE}/logs/ui.stdout 2> ${WORKSPACE}/logs/ui.stderr
                ls -lrt src/main/resources/public/nui
            '''
        }
        dir('jarvis-core') {
            // Run the maven build
            if (isUnix()) {
                sh "'${mvnHome}/bin/mvn' -Dmaven.test.failure.ignore clean install > ${WORKSPACE}/logs/server.stdout 2> ${WORKSPACE}/logs/server.stderr"
            } else {
                bat(/"${mvnHome}\bin\mvn" -Dmaven.test.failure.ignore clean install/)
            }
        }
  }

  stage('archives') {
        stash includes: 'jarvis-core/jarvis-core-server/target/*.jar', name: 'server'
        archiveArtifacts artifacts: 'jarvis-core/jarvis-core-server/target/*.jar', fingerprint: true, onlyIfSuccessful: true
        junit '**/target/surefire-reports/*.xml'
  }

  input message: 'Déployer la version ?', ok: 'Oui'
  stage('master') {
      sshagent (credentials: ['17e272eb-6f45-4dbf-97ae-06e9aba27806']) {
       sh '''
            ssh -o StrictHostKeyChecking=no -l pi ${MASTER_NODE} uname -a
            scp -o StrictHostKeyChecking=no jarvis-core/jarvis-core-server/target/*.jar pi@${MASTER_NODE}:/tmp/bundle
            ssh -o StrictHostKeyChecking=no -l pi ${MASTER_NODE} sudo mv -f /tmp/bundle /home/jarvis/jarvis-core-server-0.0.1-SNAPSHOT.jar.tmp
            ssh -o StrictHostKeyChecking=no -l pi ${MASTER_NODE} sudo chown jarvis:jarvis /home/jarvis/jarvis-core-server-0.0.1-SNAPSHOT.jar.tmp
            ssh -o StrictHostKeyChecking=no -l pi ${MASTER_NODE} sudo mv -f /home/jarvis/jarvis-core-server-0.0.1-SNAPSHOT.jar.tmp /home/jarvis/jarvis-core-server-0.0.1-SNAPSHOT.jar
            ssh -o StrictHostKeyChecking=no -l pi ${MASTER_NODE} sudo service jarvis-service restart
        '''
      }
  }
  stage('slave') {
      sshagent (credentials: ['17e272eb-6f45-4dbf-97ae-06e9aba27806']) {
        sh '''
            ssh -o StrictHostKeyChecking=no -l pi ${SLAVE_NODE} uname -a
        '''
      }
  }
  stage('pizero') {
      sshagent (credentials: ['17e272eb-6f45-4dbf-97ae-06e9aba27806']) {
        sh '''
            ssh -o StrictHostKeyChecking=no -l pi ${PIZERO_NODE} uname -a
        '''
      }
  }
}
