def lintChecks() {
    sh "echo starting lint checks for ${component}..."
    sh "mvn checkstyle:check || true"
    sh "echo lint checks for ${component} are completed..!"
}

def call() {
        pipeline {
        agent any
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
            stage('generating artifacts'){
                steps{
                    sh "mvn clean package"
                }
            }
        }
    }
}