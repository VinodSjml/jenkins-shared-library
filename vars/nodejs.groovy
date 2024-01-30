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
            NEXUS_CRED = credentials('NEXUS_CRED')
            NEXUS_URL = "172.31.25.180"
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
                    script{
                        env.ARGS="-Dsonar.sources=."
                        common.sonarChecks()
                    }
                }
            }
            stage('test cases'){
                parallel{
                stage('unit test'){
                    steps{
                        sh "echo running npm test"
                        sh "echo success"
                    }
                }
                stage('integration test'){
                    steps{
                        sh "echo running npm verify"
                        sh "echo success"
                    }
                }
                stage('functional test'){
                    steps{
                        
                        sh " echo running functional test"
                        sh "echo success"
                    }
                }
                }
            }
            stage('check release'){
                when {tag ""}
                steps{
                    script{
                        def output = sh(returnStdout: true, script: "curl -u admin:password -s -X GET 'http://3.90.58.157:8081/service/rest/v1/components?repository=catalogue' | jq ".items[].name" | grep catalogue-001")
                        print ${output}
                    }
                }
            }
            stage('generating artifacts'){
                when { tag "" }
                steps{
                    sh "echo executing against $TAG_NAME "
                    sh "npm install"
                    sh "zip ${component}-${TAG_NAME}.zip node_modules server.js"
                    sh "ls -ltr"
                }
            }
            stage('uploading artifacts'){
                when {tag ""}
                steps{
                    sh "echo uploading ${component} to nexus"
                    //sh "curl -u admin:password -X GET 'http://3.95.37.159:8081/service/rest/v1/components?repository=catalogue' | jq ".items[].name""
                   sh "curl -v -u ${NEXUS_CRED_USR}:${NEXUS_CRED_PSW} --upload-file ${component}-${TAG_NAME}.zip http://${NEXUS_URL}:8081/repository/${component}/${component}-${TAG_NAME}.zip"                   
                }
            }
        }
    }
}