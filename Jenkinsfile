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
    }
  }
}
