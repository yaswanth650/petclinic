pipeline{
 agent any
  tools{
    maven 'Maven'
    }
  stages{
    stage('INITIALIZE'){
      steps{
        sh '''
                echo "PATH = ${PATH}"
                echo "M2_HOME = ${M2_HOME}"
            '''
        }
     }
   stage('BUILD'){
      steps{
        sh 'mvn clean install '
      }
    }
   stage('CHECKSTYLE'){
      steps{
        sh 'mvn checkstyle:checkstyle '
      }
    post {
        always {
                  recordIssues tools: [checkStyle(pattern: '**/target/checkstyle-result.xml')]
                }
            }
        }
   stage('PMD'){
      steps{
        sh 'mvn pmd:pmd '
      }
    }
   stage('SPOTBUGS'){
      steps{
        sh 'mvn spotbugs:spotbugs '
      }
    post {
         always {
                  recordIssues tools: [spotBugs(pattern: '**/target/spotbugsXml.xml')]
                }
            }
    }
  }
}
