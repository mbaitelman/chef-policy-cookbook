# Chef Policy Cookbook

## This repo is a fully contained demo for running CI/CD for Chef

The repo uses several tools including Jenkins, AWS, Docker, Test Kitchen, Artifactory and others to enable testing, reviewing, deploying and verifying cookbooks.

### Setup

The setup process relies heavily on configuration as code for Jenkins and Artifactory to make it as smooth as possible

#### System Requirements

- Linux server (This has been tested with Ubuntu 18.04)
  - SSH access
  - Ability to reach ports :22 :8080, :8081, :8082
- AWS Account
  - AWS secret & access key
- Chef Server (Tested with Hosted Chef)
  - This will need the starter kit with the knife.rb and USERNAME.pem file

#### Installation

- SSH into the server
- Clone the cookbook `git clone https://github.com/mbaitelman/chef-policy-cookbook.git`
- Move into the cookbook directory `cd chef-policy-cookbook`
- Run `chmod u+x scripts/*` to make the scripts executable
- Run `./scripts/setup.sh` to install the prerequisites

##### Manual Steps (For Now)

- Log in to the Jenkins server running on :8080 of your instance
- Navigate to JENKINSURL:8080/credentials/
- Click into the `aws-test-kitchen` credentials and then click Update in the left nav. Replace values with your AWS access/secret key
- Click into the `private.pem` and update with your USERNAME.pem file
- Modify your `knife.rb` file to match the [example](/jcasc/files/knife.md)
- Click into the `knife.rb` and update it with your knife.rb file
- Open JENKINSURL:8080, log in and click into the Chef-CICD Jenkins job and click Build Now

### What is Happening?

#### What is Happening? : Installation

The installation steps are scripted to allow for anyone to start up this project.
In [setup1.sh](/scripts/setup1.sh) we run OS-level updates for security and install [Docker](https://www.docker.com/).
It also creates folders for the docker containers we are going to create.

The next script [setup2.sh](/scripts/setup2.sh) calls [docker-compose](https://docs.docker.com/compose/) to create the containers from the [docker-compose.yml](docker-compose.yml) file.
It creates an [Artifactory](https://jfrog.com/artifactory/) instance using the OSS version of the container.
The Jenkins container we are going to use is custom to allow us to add settings and files, it gets its instructions from a [dockerfile](https://docs.docker.com/engine/reference/builder/).
Our [Dockerfile](Dockerfile) extends the default Jenkins container with a list of required plugins and other settings.
The docker-compose file also maps the jcasc folder which includes Yaml files for configuring Jenkins using [Jenkins Configuration as Code](https://jenkins.io/projects/jcasc/).
The files set up an admin user, connections to Artifactory and the Docker host and the jobs we will need.
Once the docker-compose step is complete we call the Artifactory API with a [settings file](artifactory/configuration.yml) to create a repository for our Chef policy archives.
The rest of the manual steps handle secrets that can not be stored in this repository.

#### What is Happening? : Jobs

##### TestKitchen Job

The Test-Kitchen job is triggered by updates to the repository but can also be triggered manually.
When called it creates a docker container using [Chef Workstation](https://www.chef.sh/) and checks out the repository into it.
It then runs [cookstyle](https://github.com/chef/cookstyle) and prepares for the tests.
Using the AWS plugin we add our AWS credentials and call test-kitchen to test & verify our code.
Once complete we ingest the results and pass/fail the job depending on if all the checks pass.

##### UploadPolicies Job

This job also uses the Chef Workstation container.
It checks out the code then loops over all files in the policyfiles folder to run `chef install` on each to create a policy lock file.
It then runs `chef export` on each to create a policyfile archive which is a tarball of the policy, cookbook and all dependent cookbooks.
Then that tarball gets uploaded into Artifactory for use later.

##### PushPolicies Job

This job also uses the Chef Workstation container.
Using the parameters passed in it pulls down the requested policy archive files from Artifactory and calls `chef push-archive` for the appropriate policy group.
To authenticate to the Chef server we temporarily write the knife.rb and private.pem files to disk and store the paths as environment variables.
The path to the knife.rb is set with `--config` and the knife.rb file pulls the cert path from the environment variable.

##### Chef-CICD Job

This is a wrapper job that lets you trigger the above jobs together.
It calls the TestKitchen job to test the code, then uses UploadPolicies to save a copy of this policyfile in Artifactorey and then PushPolicies pushes it to the Chef Server for the qa policy group.

#### Notes

1 This is a demo and as such takes some leeway on security steps. If you are planning to use this for any extended amount of time please change the passwords for [Artifactory](https://www.jfrog.com/confluence/display/JFROG/User+Profile) and Jenkins (navigate to JENKINSURL:8080/user/admin/ and click configure to set the password).
If doing so note that you will have to make these changes permanent so that JCASC/Artifactory does not reset it.
