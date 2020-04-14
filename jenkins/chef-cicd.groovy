pipeline {
    agent none

    options {
        disableConcurrentBuilds()
        timestamps()
        timeout(time: 1, unit: 'HOURS')
    }

    triggers {
        pollSCM 'H/5 * * * *'
    }

    stages {
        stage('Run TestKitchen') {
            steps {
                build job: 'TestKitchen', parameters: [string(name: 'POLICY_NAME', value: ''), string(name: 'COOKBOOK', value: 'mbaitelman/chef-policy-cookbook'), string(name: 'BRANCH', value: 'master')]
            }
        }
        stage('Create Policy Archives'){
            steps {
                script {
                    uploadJobResult = build job: 'UploadPolicies', parameters: [string(name: 'BRANCH', value: 'master'), string(name: 'COOKBOOK', value: 'mbaitelman/chef-policy-cookbook')]
                }
            }
        }
        stage('Push Archive Files') {
            steps {
                build job: 'PushPolicies', parameters: [string(name: 'POLICY_GROUP', value: 'qa'), string(name: 'POLICY_NAME', value: ''), string(name: 'BUILD_REVISION', value: uploadJobResult.getNumber().toString()), string(name: 'COOKBOOK', value: 'mbaitelman/chef-policy-cookbook')]
            }
        }
    }
}
