pipeline {
    agent {
        docker {
            image 'chef/chefworkstation:0.13.26'
        }
    }
    
    //ToDo Add branch params
    parameters {
        choice choices: ['','desktop', 'server'], description: '', name: 'POLICY_NAME'
        choice choices: ['mbaitelman/chef-policy-cookbook', 'mattray/managed_automate-cookbook'], description: '', name: 'COOKBOOK'
        string(defaultValue: 'master', description: '', name: 'BRANCH', trim: true)
    }

    options { 
        timestamps() 
        disableConcurrentBuilds()
        ansiColor('xterm')
    }

	environment {
		CHEF_LICENSE = 'accept'
	}

    stages {
        stage ('Notify Build Start'){
            steps {
                slackSend channel: 'chef', color: '#3344FF', message: "*${JOB_NAME}* has started. <${env.BUILD_URL}|View>"                
            }
        }
        stage('Checkout Cookbooks'){
            steps{
                checkout([$class: 'GitSCM', branches: [[name: params.BRANCH]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: "https://github.com/${params.COOKBOOK}.git"]]])
            }
        }    
        stage("Run Cookstyle"){
            steps {
                sh script: 'cookstyle .', returnStatus: (params.COOKBOOK == 'prodege-base') //prodege-base does not yet follow cookstyle rules
            }
        }
        stage("Remove old files"){
            parallel{
                stage("Remove .kitchen logs"){
                    steps {
                        sh script: 'rm -r .kitchen/', returnStatus: true
                    }
                }
                stage("Remove old reports"){
                    steps {
                        sh  script: 'rm -r junit/', returnStatus: true
                    }
                }
            }
        }
        stage("Run Tests"){
            steps {
                withAWS(credentials: 'aws-test-kitchen') {
                    sh script: "kitchen test ${params.POLICY_NAME} --destroy always --concurrency 2", returnStatus: true
                }
            }
        }
        stage('Gather Reports'){
            steps{
                archiveArtifacts "**/.kitchen/logs/*.log"
                junit "**/junit/*.xml"
            }
        }        
    }
}