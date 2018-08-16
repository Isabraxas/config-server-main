node {
    stage('scm') {
        dir('server-config') {
            git branch: 'master',
            credentialsId: 'mycredentials',
            url: 'http://feronti@bitbucket.viridian.cc/scm/stat/server-config.git'
        }
        sh('du -hcs *')
    }
   stage('Build') {
       echo "building ${env.BUILD_ID}"
        dir('server-config') {
            //try {
                sh "mvn -Dbuild.number=${BUILD_NUMBER} -Dmaven.test.failure.ignore -DskipTests clean package"
            //} catch (any) {
            //    currentBuild.result = 'SUCCESS'
            //    echo any
            //}
            archive "target/configServer-0.1.${BUILD_NUMBER}.jar"
        }
   }
   stage('test') {
        dir('server-config') {
                sh "mvn -Dbuild.number=${BUILD_NUMBER} -Dmaven.test.failure.ignore package"
        }
      //junit '**/target/surefire-reports/TEST-*.xml'
   }
   stage("deploy") {
       sh '/var/lib/jenkins/viridian/deploy-server-config.sh'
   }
}