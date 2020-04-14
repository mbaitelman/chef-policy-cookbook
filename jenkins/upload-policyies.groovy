pipeline {
    agent {
        docker {
            image 'chef/chefdk:latest'
        }
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
                dir('chef-policy-cookbook'){
                    checkout([$class: 'GitSCM', branches: [[name: params.BRANCH]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: "https://github.com/${params.COOKBOOK}.git"]]])
                }
            }
        }
        stage('install') {
            steps {
                script {
                    dir('chef-policy-cookbook'){
                        for (f in findFiles(glob: 'policyfiles/*.rb')) {
                            sh "chef install ${f}"
                        }
                    }
                }
                
            }
        }
        stage('export') {
            steps {
                sh 'mkdir -p exportdir'
                
                script {
                    dir('chef-policy-cookbook'){
                        for (f in findFiles(glob: 'policyfiles/*.rb')) {
                            sh "chef export ${f} exportdir --archive"
                        }
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
                            "pattern": "chef-policy-cookbook/exportdir/*.tgz",
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
                archiveArtifacts 'chef-policy-cookbook/policyfiles/*lock.json'
            }
        }
    }
    post {
        always {
            cleanWs()
        }
    }

}
