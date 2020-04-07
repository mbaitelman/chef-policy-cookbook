pipeline {
    agent {
        docker {
            image 'chef/chefdk:latest'
        }
    }

    parameters {
        choice (
            choices: [
                'qa', 
                'stage',
                'prod', 
            ], 
            description: '', 
            name: 'POLICY_GROUP')
        choice (
            choices: [
                '',
                'desktop',
                'server' 
            ], 
            description: '', 
            name: 'POLICY_NAME'
        )
        string defaultValue: '-1', description: '', name: 'BUILD_REVISION', trim: true
        choice choices: ['mbaitelman/chef-policy-cookbook', 'mattray/managed_automate-cookbook'], description: '', name: 'COOKBOOK'
    }

    options {
      timeout(15)
      timestamps()
      ansiColor('xterm')
      disableConcurrentBuilds()
    }

    stages {
        stage('validate'){
            steps{
                script{
                    if(params.BUILD_REVISION == '-1'){
                        error('Invalid BUILD_REVISION')
                    }
                }
            }
        }
        stage('pull artifact'){
            steps {
                rtDownload (
                    serverId: "artifactory-01",
                    spec:
                        """{
                            "files": [
                                {
                                    "pattern": "chef_cookbooks/${params.COOKBOOK}/${params.BUILD_REVISION}/${params.POLICY_NAME}*.tgz",
                                    "target": "archives/"
                                }
                            ]
                        }"""
                )
            }
        }
        stage('Push Archive') {
            steps {
                withCredentials([file(credentialsId: 'opsworks.pem', variable: 'OPSWORKSPEM'), file(credentialsId: 'chef-prodege.pem', variable: 'PRIVATEPEM'), file(credentialsId: 'knife.rb', variable: 'KNIFERB')]) {
                    script {
                        for (f in findFiles(glob: "archives/${params.COOKBOOK}/${params.BUILD_REVISION}/${params.POLICY_NAME}*.tgz")) {
                            sh "chef push-archive ${POLICY_GROUP} ${f} --config ${KNIFERB}"
                        }
                    }
                }
            }       
        }
    }
    post {
        always {
            cleanWs()
        }
        unsuccessful {
            slackSend channel: "#chef", color: 'danger', message: "Chef pushing policy for ${params.COOKBOOK} ${params.POLICY_GROUP} ${params.POLICY_NAME} with version ${params.BUILD_REVISION} was unsuccessful. ${getBuildCause()} <${env.BUILD_URL}|View>"
        }
        success {
            slackSend channel: "#chef", color: 'good', message: "Chef pushing policy for ${params.COOKBOOK} ${params.POLICY_GROUP} ${params.POLICY_NAME} with version ${params.BUILD_REVISION} has completed. ${getBuildCause()} <${env.BUILD_URL}|View>"
        }
    }

}