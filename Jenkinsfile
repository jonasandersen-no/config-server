pipeline {

    agent any

    stages {

        stage("Build") {

          steps {
              echo 'building the application...'
              sh 'mvn clean compile'
          }
        }
        stage("Test") {

          steps {
              echo 'testing the application...'
              sh "mvn test -Pcoverage"
          }
        }
        stage("Package") {
          when {
              branch 'master'
          }
          steps {
              echo 'deploying the application...'
              sh "mvn -DskipTests -Ddisable.checkstyle=true package"
              archiveArtifacts artifacts: '**/*.jar'
          }
        }
        stage('Upload'){
          when {
             branch 'master'
          }
        steps{
            sh "mvn -DskipTests -Ddisable.checkstyle=true deploy"
            sh 'docker-compose -v'
            sh 'docker-compose build'
            sh 'docker-compose push'
        }
      }
    }
}
