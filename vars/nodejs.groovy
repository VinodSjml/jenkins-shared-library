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
        stages {
            stage('lint check'){
                steps{
                    script{
                        lintChecks()
                    }
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