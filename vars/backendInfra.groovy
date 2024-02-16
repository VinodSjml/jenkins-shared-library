def call(){
    properties([
        parameters([
            choice(choices: 'dev\nprod', description: "Select your environment", name: "ENV"),
            choice(choices: 'apply\ndestroy', description: "Chose an action", name: "ACTION"),
            string(choices: 'APP_VERSION', description: "Enter your backend version", name: "APP_VERSION")
        ])
    ])
    node{
        ansiColor('xtrem'){
        git branch: 'main', url: "https://github.com/VinodSjml/${REPONAME}.git"
        
        stage('terraform init'){
            sh '''
                cd ${TFDIR}
                terrafile -f env-${ENVI}/Terrafile
                terraform init env-${ENVI}/${ENVI}-backend.tfvars
            '''
        }
        stage('terraform plan'){
            sh'''
                cd ${TFDIR}
                terraform plan -var-file=env-${ENVI}/${ENVI}.tfvars
            '''
        }
        stage('terraform action'){
            sh'''
                cd ${TFDIR}
                terraform ${ACTION} -auto-approve -var-file=env-${ENVI}/${ENVI}.tfvars
            '''
        }
        }
    }
}