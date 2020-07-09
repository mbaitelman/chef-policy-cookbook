FROM jenkins/jenkins:2.244

COPY plugins.txt /usr/share/jenkins/ref/plugins.txt

RUN mkdir -p /usr/share/jenkins/ref/secrets && \
    # Why is this not the default?
    echo false > /usr/share/jenkins/ref/secrets/slave-to-master-security-kill-switch && \
    # Install all our plugins so they are baked in to the image.
    /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt && \
    # Install a nicer default theme to make it look shiny for non-BlueOcean.
    mkdir /usr/share/jenkins/ref/userContent

#COPY jenkins.yaml /var/jenkins_home/jenkins.yaml

ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false"
