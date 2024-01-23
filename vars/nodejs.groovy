def lintChecks() {
    sh "echo installing jslint"
    sh "npm i jslint"
    sh "echo starting lint checks for ${component}..."
    sh "/home/centos/node_modules/jslint/bin/jslint.js server.js || true"
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
                    sh "sonar-scanner -Dsonar.host.url=http://${SONAR_URL}:9000/ -Dsonar.sources=. -Dsonar.projectkey=${component} -Dsonar.login=${SONAR_CRED_USR} -Dsonar.password=${SONAR_CRED_PSW}"
                }
            }
            stage('generating artifacts'){
                steps{
                    sh "npm install"
                }
            }
        }
    }
}