def sonarChecks(){
  stage('Sonar Checks'){    
    sh "echo starting sonar checks for ${Component}.."
    // sh "sonar-scanner -Dsonar.host.url=http://${SONAR_URL}:9000/ $ARGS -Dsonar.projectKey=${component} -Dsonar.login=${SONAR_CRED_USR} -Dsonar.password=${SONAR_CRED_PSW}"
    // sh "curl https://gitlab.com/thecloudcareers/opensource/-/raw/master/lab-tools/sonar-scanner/quality-gate > quality-gate.sh"
    // sh "bash quality-gate.sh ${SONAR_CRED_USR} ${SONAR_CRED_PSW} ${SONAR_URL} ${component}" 
    sh "echo sonar check for $ARGS ${Component} are completed!"   
  }
}

def lintChecks(){
    node{
        stage('Lint Checks'){
            if(env.AppType == "nodejs"){
                sh "echo installing jslint"
                sh "npm i jslint"
                sh "echo starting lint checks for ${Component}"
                sh "/home/centos/node_modules/jslint/bin/jslint.js server.js || true"
                sh "echo lint checks for ${Component} are completed"

            }
            else if(env.AppType == "maven"){
                sh "echo performing lint checks for ${Component}"
                sh "mvn checkstyle:check || true"
                sh "echo ;int checks for ${Component} are completed"
            }
            else if(env.AppType == "python"){
                sh "echo starting lint checks for ${Component}..."
                sh "pip install pylint"
                sh "pylint *.py || true"
                sh "echo lint checks for ${Component} are completed..!"
            }
            else{
                sh "echo performning lint checks for frontend"
            }

        }
    }
}

def testCases(){
stage('test cases'){
            def stages = [:]
            stages["unit test"]={   
                sh "echo running unit test"
                sh "echo success"
            }
            stages["integration test"]={     
                sh "echo running integration test"
                sh "echo success"
            }
            stages["functional test"]={        
                sh " echo running functional test"
                sh "echo success"  
            }
            parallel(stages)
    }
}

def artifacts(){
    stage('check release'){
     withCredentials([usernamePassword(credentialsId: 'NEXUS_CRED', passwordVariable: 'NEXUS_CRED_PSW', usernameVariable: 'NEXUS_CRED_USR')]) {
        env.Version_check = sh(returnStdout: true, script: "curl -v -u ${NEXUS_CRED_USR}:${NEXUS_CRED_PSW} -s -X GET 'http://${NEXUS_URL}:8081/service/rest/v1/components?repository=${Component}'| jq '.items[].name' | grep ${Component}-${TAG_NAME} | sed -e 's/\"//g' -e 's/.zip//g'")
        print Version_check
     }
   }
   if(env.Version_check == ""){
    stage('generate artfifacts'){
        if(env.AppType == "nodejs"){
           sh "echo generating artifacts..."
           sh "npm install"
           sh "zip -r ${Component}-${TAG_NAME}.zip -i node_modules server.js"
        }
        else if(env.AppType == "maven"){
           sh "echo generating artifacts..."
           sh "mvn clean package"
           sh "mv target/${Component}-1.0.jar ${Component}.jar"
           sh "zip -r ${Component}-${TAG_NAME}.zip ${Component}.jar"         
        }
        else if(env.AppType == "python"){
           sh "echo generating artifacts..."
           sh "zip -r ${Component}-${TAG_NAME}.zip *.py *.ini requirements.txt"         
        }
        else {
            sh "echo generating artifacts..."
            sh "cd static/"
            sh "zip -r ../${Ccomponent}-${TAG_NAME}.zip *"
        }
     }
     stage('uploading artifacts'){
      withCredentials([usernamePassword(credentialsId: 'NEXUS_CRED', passwordVariable: 'NEXUS_CRED_PSW', usernameVariable: 'NEXUS_CRED_USR')]) {
      sh "curl -v -u ${NEXUS_CRED_USR}:${NEXUS_CRED_PSW} --upload-file ${Component}-${TAG_NAME}.zip http://${NEXUS_URL}:8081/repository/${Component}/${Component}-${TAG_NAME}.zip" 
     }
   }
   }
}
