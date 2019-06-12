- Find an Ubuntu server (any linux server should do but I like Ubuntu)
- Install docker. Docs [here](https://docs.docker.com/install/linux/docker-ce/ubuntu/) 
- Start a Jenkins instance on Docker. [More info here](https://github.com/jenkinsci/docker/) 
    ```
    docker run \ --name=jenkins \ --volume="/data/docker/jenkins/jenkins_home:/var/jenkins_home" \ --network=host \ -u root \ -v /etc/timezone:/etc/timezone:ro \ -v /var/run/docker.sock:/var/run/docker.sock \ -v $(which docker):/usr/bin/docker \ -p 8080:8080 \ -p 50000:50000 \ --restart=always \ --detach=true \ jenkins/jenkins:2.177
    ```
    Then run 
    `$ sudo chown -R 1000 /data/docker/jenkins/jenkins_home/`
    `$ sudo SOMETHING FOR JENKINS DOCKER` #TODO
- When it runs it will show the initial password, copy that for later
- Set up Artifactory on Jenkins. [More info here](https://github.com/jfrog/artifactory-docker-examples/tree/master/docker-compose/artifactory)
    ```
    docker run --name artifactory -d \ -v /data/docker/jfrog/artifactory:/var/opt/jfrog/artifactory \ -p 8081:8081 \ docker.bintray.io/jfrog/artifactory-oss:latest
    ```
    Then run `$ sudo chown -R 1030:1030 /data/docker/jfrog/artifactory/`
- Now we set up Jenkins
  - If you forgot the initial password run `$  cat /data/docker/jenkins2/jenkins_home/secrets/initialAdminPassword`
- Open [http://YOURIP:8080](http://YOURIP:8080)
- Enter the password when prompted
- Choose the 'Install community plugins' option. 
- Create your account
  - Click Save and continue 
- Set the URL 
  - Click save
- You are done the inital setup of Jenkins. Click 'Start using Jenkins' 
- Click 'Manage Jenkins'
- Click into 'Script Console'
- Run the script from /jenkins/install_plugins.groovy
- Click 'Manage Jenkins' -> 'Configure System'
- Go to 'Cloud' -> 'Add a new Cloud'
  - Name = docker
  - Host URI = `tcp://YOURSERVERIP:2375`
  - Enabled = checked off
  - 