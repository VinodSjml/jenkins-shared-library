def call(){
    properties([
        parameters([
            choice(name: "ENVI"; choices: ['dev', 'prod']; description: "choose environment")
            choice(name: "ACTION"; choices: ['apply', 'destroy']; description: "choose terraform action")
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