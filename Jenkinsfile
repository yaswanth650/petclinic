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
        sh 'mvn checkstyle:check'
      }
    }
  }
}
