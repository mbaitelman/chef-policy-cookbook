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
        stage('Checkout Cookbooks'){
            steps{
                dir(params.COOKBOOK)[{
                    checkout([$class: 'GitSCM', branches: [[name: params.BRANCH]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: "https://github.com/${params.COOKBOOK}.git"]]])
                }
            }
        }    
        stage("Run Cookstyle"){
            steps {
                dir(params.COOKBOOK)[{ 
                    sh script: 'cookstyle .', returnStatus: true //Dont yet fail on cookstyle
                }
            }
        }
        stage("Remove old files"){
            parallel{
                stage("Remove .kitchen logs"){
                    steps {
                        sh script: "rm -r ${params.COOKBOOK}/.kitchen/", returnStatus: true
                    }
                }
                stage("Remove old reports"){
                    steps {
                        sh  script: "rm -r ${params.COOKBOOK}/junit/", returnStatus: true
                    }
                }
            }
        }
        stage("Run Tests"){
            steps {
                dir(params.COOKBOOK)[{ 
                    withAWS(credentials: 'aws-test-kitchen') {
                        sh script: "kitchen test ${params.POLICY_NAME} --destroy always --concurrency 2", returnStatus: true
                    }
                }
            }
        }
        stage('Gather Reports'){
            steps{
                archiveArtifacts "**/${params.COOKBOOK}/.kitchen/logs/*.log"
                junit "**/${params.COOKBOOK}/junit/*.xml"
            }
        }        
    }
}
