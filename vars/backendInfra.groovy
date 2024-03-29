def call(){
    properties([
        parameters([
            choice(choices: 'dev\nprod', description: "Select your environment", name: "ENVI"),
            choice(choices: 'apply\ndestroy', description: "Chose an action", name: "ACTION"),
            string(choices: 'APP_VERSION', description: "Enter your backend version", name: "APP_VERSION")
        ]),
    ])
    node{
        ansiColor('xtrem'){
        git branch: 'main', url: "https://github.com/VinodSjml/${REPONAME}.git"
        
        stage('terraform init'){
            sh '''
                cd ${TFDIR}
                terrafile -f env-${ENVI}/Terrafile
                terraform init -backend-config=env-${ENVI}/${ENVI}-backend.tfvars

            '''
        }
        stage('terraform plan'){
            sh'''
                cd ${TFDIR}
                terraform plan -var-file=env-${ENVI}/${ENVI}.tfvars -var APP_VERSION=${APP_VERSION}
            '''
        }
        stage('terraform action'){
            sh'''
                cd ${TFDIR}
                terraform ${ACTION} -auto-approve -var-file=env-${ENVI}/${ENVI}.tfvars -var APP_VERSION=${APP_VERSION}
            '''
        }
        }
    }
}