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
    post {
       always {
                 recordIssues tools: [pmdParser(pattern: '**/target/pmd.xml')]
              }
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
   stage('FINDBUGS'){
      steps{
        sh 'mvn findbugs:findbugs '
      }
    post {
         always {
                  recordIssues tools: [findBugs(pattern: '**/target/findbugsXml.xml')]
                }
            }
   }
   stage('TEST') {
         steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }
  }
}
