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
                    sh "sonar-scanner -Dsonar.host.url=http://${SONAR_URL}:9000/ -Dsonar.java.binaries=target/ -Dsonar.projectKey=${component} -Dsonar.login=${SONAR_CRED_USR} -Dsonar.password=${SONAR_CRED_PSW}"
                    sh "curl https://gitlab.com/thecloudcareers/opensource/-/raw/master/lab-tools/sonar-scanner/quality-gate > quality-gate.sh"
                    sh "bash quality-gate.sh ${SONAR_CRED_USR} ${SONAR_CRED_PSW} ${SONAR_URL} ${component}"               
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