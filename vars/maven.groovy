def lintChecks() {
    sh "echo starting lint checks for ${Component}..."
    sh "mvn checkstyle:check || true"
    sh "echo lint checks for ${Component} are completed..!"
}

def call(){
    node{
        git branch: 'main', url: "https://github.com/VinodSjml/${Component}.git"
        common.lintChecks()
        env.ARGS="-Dsonar.java.binaries=target/"
        common.sonarChecks()
        common.testCases()
        env.NEXUS_URL="172.31.25.180"
        common.artifacts()
    }
}

/*
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
                    script{
                    env.ARGS="-Dsonar.java.binaries=target/"
                    common.sonarChecks()
                    }         
                }
            }
            stage('test cases'){
                parallel{
                stage('unit test'){
                    steps{
                        sh "echo running mvn clean test"
                        sh "echo success"
                    }
                }
                stage('integration test'){
                    steps{
                        sh "echo running mvn clean verify"
                        sh "echo success"
                    }
                }
                stage('functional test'){
                    steps{
                        sh "echo running functional test"
                        sh "echo success"
                    }
                }
                }
            }
            stage('generating artifacts'){
                steps{
                    sh "echo generating artifact - mvn clean package"
                }
            }
        }
    }
}
*/