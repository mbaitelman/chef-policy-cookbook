# Chef Policy Cookbook

## This repo is a fully contained demo for running CI/CD for Chef

The repo uses several tools including Jenkins, AWS, Docker, Test Kitchen, Artifactory and others to enable testing, reviewing, deploying and verifying cookbooks.

### Setup

The setup process relies heavily on configuration as code for Jenkins and Artifactory to make it as smooth as possible

#### System Requirements

- Linux server (This has been tested with Ubuntu 18.04)
  - SSH access
  - Ability to reach ports :8080, :8081, :8082
- AWS Account
  - AWS secret & access key
- Chef Server (Tested with Hosted Chef)

#### Installation

- SSH into the server
- Clone the cookbook `git clone https://github.com/mbaitelman/chef-policy-cookbook.git`
- Move into the cookbook directory `cd chef-policy-cookbook`
- Run `chmod u+x scripts/*` to make the scripts executable
- Run `./scripts/setup1.sh` to install the prerequisites
- Logout and then back into the server
- Move back into the cookbook directory `cd chef-policy-cookbook`
- Run `./scripts/setup2.sh` to complete the setup

##### Manual Steps (For Now)

- Log in to the Jenkins server running on :8080 of your instance
- Navigate to JENKINSURL:8080/credentials/
- Update the values in the `aws-test-kitchen` credential with your own
- Update the file in

#### Notes

1 This is a demo and as such takes some leeway on security steps. If you are planning to use this for any extended amount of time please change the passwords for [Artifactory](https://www.jfrog.com/confluence/display/JFROG/User+Profile) and Jenkins (navigate to JENKINSURL:8080/user/admin/ and click configure to reset the password).
If doing so note that you will have to make these changes permanent so that JCASC/Artifacatiry does not reset it.
