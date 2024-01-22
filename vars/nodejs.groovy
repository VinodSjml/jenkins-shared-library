def call() {
        pipeline {
        agent any
        stages {
            stage('lint check'){
                steps{
                    sh "echo installing jslint"
                    sh "npm i jslint"
                    sh "echo starting lint checks..."
                    sh "/home/centos/node_modules/jslint/bin/jslint.js server.js || true"
                    sh "echo lint checks are completed..!"
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