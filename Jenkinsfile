pipeline {

    agent any

    stages {

        stage("build") {

          steps {
              echo 'building the application...'
              sh 'mvn clean compile'
          }
        }

        stage("test") {

          steps {
              echo 'testing the application...'
              sh "mvn test -Pcoverage"
          }
        }
        stage("deploy") {

          steps {
              echo 'deploying the application...'
              sh "mvn -DskipTests -Ddisable.checkstyle=true package"
          }
        }
    }
}