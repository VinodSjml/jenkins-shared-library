def lintChecks() {
    sh "echo starting lint checks for ${component}..."
    sh "mvn checkstyle:check || true"
    sh "echo lint checks for ${component} are completed..!"
}

def call() {
        pipeline {
        agent any
        environment {
            SONAR_URL = "172.31.38.57"
            SONAR_CRED = credentials('SONAR_CRED')
        }
        stages {
            stage('lint check'){
                steps{
                    script{
                        lintChecks()
                    }
                }
            }
            stage('performing code compilation'){
                steps{
                    sh "mvn clean compile"
                }
            }
            stage('sonar checks'){
                steps{
                    env.ARGS="-Dsonar.java.binaries=target/"
                    common.sonarChecks()          
                }
            }
            stage('generating artifacts'){
                steps{
                    sh "mvn clean package"
                }
            }
        }
    }
}