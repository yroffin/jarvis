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
      parallel(
            "NodeJS": {
                  stage('Prepare NodeJS Suite') {
                        // NodeJS
                        tool name: 'NodeJS', type: 'com.cloudbees.jenkins.plugins.customtools.CustomTool'
                        env.PATH='/usr/bin:/bin:/var/jenkins_home/tools/com.cloudbees.jenkins.plugins.customtools.CustomTool/NodeJS/node-v6.10.0-linux-x64/bin'
                  }
            },
            "Maven": {
                  stage('Prepare maven tools') {
                        // Maven
                        tool name: 'M3', type: 'maven'
                        mvnHome = tool 'M3'
                  }
            },
            "Logs": {
                  stage('Create logs') {
                        sh '''
                              mkdir -p ${WORKSPACE}/logs
                        '''
                  }
            }
      )

      stage('Setup angular/cli') {
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
}

/**
 * build gui
 */
def buildGUI() {
      print "GUI build"
      stage('GUI') {
            dir('jarvis-core/jarvis-core-ui-ng') {
            sh '''
                  npm install >> ${WORKSPACE}/logs/ui.stdout 2>> ${WORKSPACE}/logs/ui.stderr
                  npm update >> ${WORKSPACE}/logs/ui.stdout 2>> ${WORKSPACE}/logs/ui.stderr
                  ng build --aot --prod --base-href /nui/ --output-path=src/main/resources/public/nui  >>${WORKSPACE}/logs/ui.stdout 2>>${WORKSPACE}/logs/ui.stderr
                  ls -lrt src/main/resources/public/nui >>${WORKSPACE}/logs/ui.stdout 2>>${WORKSPACE}/logs/ui.stderr
            '''
            }
      }
}

/**
 * build SRV
 */
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

/**
 * archive
 */
def archive() {
  stage('archives') {
        archiveArtifacts artifacts: 'jarvis-core/jarvis-core-server/target/*.jar', fingerprint: true, onlyIfSuccessful: true
        archiveArtifacts artifacts: 'jarvis-core/jarvis-rest-module-sphinx4/target/*.jar', fingerprint: true, onlyIfSuccessful: true
        junit '**/target/surefire-reports/*.xml'
  }
}

/**
 * deploy
 */
def deploy() {
      parallel(
            "master": {
                  deployOnMaster()
            },
            "shirka": {
                  deployOnShirka()
            }
      )
}

/**
 * deployOnMaster
 */
def deployOnMaster() {
      input message: 'Déployer la version sur le master ?', ok: 'Oui'
      step ([
            $class: 'CopyArtifact',
            projectName: 'jarvis-build',
            filter: 'jarvis-core/jarvis-core-server/target/*.jar'
      ]);
      stage('Deploy on master') {
            sshagent (credentials: ['17e272eb-6f45-4dbf-97ae-06e9aba27806']) {
                  sh '''
                  ssh -o StrictHostKeyChecking=no -l pi ${MASTER_NODE} uname -a
                  scp -o StrictHostKeyChecking=no jarvis-scripts/jarvis-service pi@${MASTER_NODE}:/tmp/script
                  ssh -o StrictHostKeyChecking=no -l pi ${MASTER_NODE} sudo mv -f /tmp/script /etc/init.d/jarvis-service
                  ssh -o StrictHostKeyChecking=no -l pi ${MASTER_NODE} sudo chmod 755 -f /etc/init.d/jarvis-service
                  ssh -o StrictHostKeyChecking=no -l pi ${MASTER_NODE} sudo update-rc.d -f jarvis-service defaults
                  ssh -o StrictHostKeyChecking=no -l pi ${MASTER_NODE} sudo systemctl daemon-reload
                  scp -o StrictHostKeyChecking=no jarvis-core/jarvis-core-server/target/*.jar pi@${MASTER_NODE}:/tmp/bundle
                  ssh -o StrictHostKeyChecking=no -l pi ${MASTER_NODE} sudo mv -f /tmp/bundle /home/jarvis/jarvis-core-server-0.0.1-SNAPSHOT.jar.tmp
                  ssh -o StrictHostKeyChecking=no -l pi ${MASTER_NODE} sudo chown jarvis:jarvis /home/jarvis/jarvis-core-server-0.0.1-SNAPSHOT.jar.tmp
                  ssh -o StrictHostKeyChecking=no -l pi ${MASTER_NODE} sudo mv -f /home/jarvis/jarvis-core-server-0.0.1-SNAPSHOT.jar.tmp /home/jarvis/jarvis-core-server-0.0.1-SNAPSHOT.jar
                  ssh -o StrictHostKeyChecking=no -l pi ${MASTER_NODE} sudo pkill -e -f jarvis
                  ssh -o StrictHostKeyChecking=no -l pi ${MASTER_NODE} sudo service jarvis-service restart
                  '''
            }
      }
}

/**
 * deployOnShirka
 */
def deployOnShirka() {
      input message: 'Déployer la version sur shirka ?', ok: 'Oui'
      step ([
            $class: 'CopyArtifact',
            projectName: 'jarvis-build',
            filter: 'jarvis-core/jarvis-rest-module-sphinx4/target/*.jar'
      ]);
      stage('Deploy on shirka') {
            sshagent (credentials: ['17e272eb-6f45-4dbf-97ae-06e9aba27806']) {
                  sh '''
                  ssh -o StrictHostKeyChecking=no -l pi ${SHIRKA_NODE} uname -a
                  scp -o StrictHostKeyChecking=no jarvis-scripts/jarvis-sphinx4-service pi@${SHIRKA_NODE}:/tmp/script
                  ssh -o StrictHostKeyChecking=no -l pi ${SHIRKA_NODE} sudo mv -f /tmp/script /etc/init.d/jarvis-sphinx4-service
                  ssh -o StrictHostKeyChecking=no -l pi ${SHIRKA_NODE} sudo chmod 755 -f /etc/init.d/jarvis-sphinx4-service
                  ssh -o StrictHostKeyChecking=no -l pi ${SHIRKA_NODE} sudo update-rc.d -f jarvis-sphinx4-service defaults
                  ssh -o StrictHostKeyChecking=no -l pi ${SHIRKA_NODE} sudo systemctl daemon-reload
                  scp -o StrictHostKeyChecking=no jarvis-core/jarvis-rest-module-sphinx4/target/*.jar pi@${SHIRKA_NODE}:/tmp/bundle
                  ssh -o StrictHostKeyChecking=no -l pi ${SHIRKA_NODE} sudo mv -f /tmp/bundle /home/jarvis/jarvis-rest-module-sphinx4-0.0.1-SNAPSHOT.jar
                  ssh -o StrictHostKeyChecking=no -l pi ${SHIRKA_NODE} sudo pkill -e -f jarvis
                  ssh -o StrictHostKeyChecking=no -l pi ${SHIRKA_NODE} sudo service jarvis-sphinx4-service restart
                  '''
            }
      }
}

return this;