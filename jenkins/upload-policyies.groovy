pipeline {
    agent {
        docker {
            image 'chef/chefdk:latest'
        }
    }

    triggers {
        pollSCM 'H/5 * * * *'
    }

    parameters {
        string(defaultValue: 'master', description: '', name: 'BRANCH', trim: true)
        choice choices: ['mbaitelman/chef-policy-cookbook', 'mattray/managed_automate-cookbook'], description: '', name: 'COOKBOOK'
    }

    options {
      timeout(15)
      timestamps()
      ansiColor('xterm')
      disableConcurrentBuilds()
    }

    environment{
        CHEF_LICENSE = 'accept'
    }

    stages {
        stage('checkout') {
            steps {
                checkout([$class: 'GitSCM', branches: [[name: params.BRANCH]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: "https://github.com/${params.COOKBOOK}.git"]]])
            }
        }
        stage('install') {
            steps {
                script {
                    for (f in findFiles(glob: 'policyfiles/*.rb')) {
                        sh "chef install ${f}"
                    }
                }
                
            }
        }
        stage('export') {
            steps {
                sh 'mkdir -p exportdir'
                
                script {
                    for (f in findFiles(glob: 'policyfiles/*.rb')) {
                        sh "chef export ${f} exportdir --archive"
                    }
                }                
            }
        }
        stage('upload') {
            steps {
                rtUpload (
                    serverId: "artifactory",
                    spec:
                        """{
                        "files": [
                            {
                            "pattern": "exportdir/*.tgz",
                            "target": "chef/${params.COOKBOOK}/${BUILD_NUMBER}/"
                            }
                        ]
                        }"""
                )
            }
            // when {
            //     expression { env.BRANCH_NAME == "master"}
            // }
        }
        stage('archive'){
            steps{
                archiveArtifacts 'policyfiles/*lock.json'
            }
        }
    }
    post {
        always {
            cleanWs()
        }
    }

}