def lintChecks() {
    sh "echo starting lint checks for ${component}..."
    sh "pip install pylint"
    sh "pylint *.py || true"
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
            
        }
    }
}