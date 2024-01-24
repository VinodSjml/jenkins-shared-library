def lintChecks() {
    sh "echo starting lint checks for ${component}..."
    sh "pip install pylint"
    sh "pylint *.py || true"
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
            stage('sonar check'){
                steps{
                    env.ARGS="-Dsonar.sources=."
                    common.sonarChecks()  
                }
            }
            
        }
    }
}