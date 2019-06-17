FROM jenkins/jenkins:2.180
ARG DIR_CASC=/var/jenkins_home/casc_configs
ENV CASC_JENKINS_CONFIG=$DIR_CASC

COPY plugins.txt /plugins.txt

RUN mkdir -p /usr/share/jenkins/ref/secrets && \
    # Why is this not the default?
    echo false > /usr/share/jenkins/ref/secrets/slave-to-master-security-kill-switch && \
    # Install all our plugins so they are baked in to the image.
    /usr/local/bin/install-plugins.sh < /plugins.txt && \
    # Install a nicer default theme to make it look shiny for non-BlueOcean.
    mkdir /usr/share/jenkins/ref/userContent $DIR_CASC

COPY jenkins.yaml $DIR_CASC/
#ENV CASC_JENKINS_CONFIG=/var/jenkins_home/config/jenkins.yaml

ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"

#USER root
#RUN usermod -aG docker jenkins
