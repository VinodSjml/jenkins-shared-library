def call() {
    node() {
        sh "rm -rf *"
        git branch: 'main', url: "https://github.com/VinodSjml/${Component}.git"
        env.AppType=""
        common.lintChecks()
        if(env.TAG_NAME != null) {
            stage('Generating Artifacts...') {
                if(env.AppType == "nodejs") {
                    sh "echo generating artifacts..."
                    sh "npm install"
                }
                if(env.AppType == "maven") {
                    sh "echo generating artifacts..."
                    sh "mvn clean package"
                    sh "mv target/${Component}-1.0.jar ${Component}.jar"
                    sh "ls -ltr"
                }
                if(env.AppType == "python") {
                    sh "echo genarting artifacts..."
                    sh "zip -r ${Component}-${TAG_NAME}.zip *.py *.ini requirements.txt"
                }
                else {
                    sh '''
                        echo generating artifacts...
                        cd static/
                        zip -r ../${Component}-${TAG_NAME}.zip *
                    '''
                }
                sh "docker build -t 323468129901.dkr.ecr.us-east-1.amazonaws.com/${Component}:${TAG_NAME}"
                sh "aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 323468129901.dkr.ecr.us-east-1.amazonaws.com"
                sh "docker push 323468129901.dkr.ecr.us-east-1.amazonaws.com/${Component}:${TAG_NAME}"
            }
        }
    }
}