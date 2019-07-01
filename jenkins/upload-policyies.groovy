pipeline {
    agent {
        docker {
            image 'chef/chefdk:latest'
        }
    }

    triggers {
        pollSCM 'H/5 * * * *'
    }

    options {
      timeout(15)
      timestamps()
      ansiColor('xterm')
      disableConcurrentBuilds()
    }

    stages {
        stage('checkout') {
            steps {
                checkout([$class: 'GitSCM', branches: [[name: 'master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/mbaitelman/chef-policy-cookbook.git']]])
            }
        }
        stage('install') {
            steps {
                sh label: 'Delete lock files if exist', returnStatus: true, script: 'rm policyfiles/*.json'
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
                            "target": "/chef/chef-policy-cookbook/${BUILD_NUMBER}/"
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