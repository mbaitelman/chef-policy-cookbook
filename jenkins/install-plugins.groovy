import jenkins.model.* 
import java.util.logging.Logger

def logger = Logger.getLogger("") 
def installed = false 
def initialized = false

def pluginParameter="jackson2-api,workflow-support,pipeline-model-definition,docker-java-api,docker-commons,workflow-scm-step,blueocean,workflow-job,artifactory,credentials-binding,github,blueocean-jwt,junit,scm-api,blueocean-events,pipeline-input-step,blueocean-i18n,configuration-as-code,blueocean-pipeline-editor,ansicolor,workflow-cps-global-lib,build-timeout,blueocean-display-url,github-branch-source,blueocean-commons,config-file-provider,docker-workflow,pipeline-model-extensions,git-client,workflow-cps,workflow-api,workflow-multibranch,blueocean-dashboard,workflow-basic-steps,docker-plugin,blueocean-core-js,pipeline-utility-steps,pipeline-stage-step,git,pipeline-milestone-step,blueocean-git-pipeline,blueocean-config,pipeline-github-lib,plain-credentials,bouncycastle-api,workflow-step-api,powershell,blueocean-personalization,credentials,pipeline-model-api,durable-task,matrix-project,matrix-auth,javadoc,windows-slaves,blueocean-web,ssh-credentials,workflow-durable-task-step,timestamper,branch-api,blueocean-github-pipeline,blueocean-rest,pipeline-model-declarative-agent,github-api,pipeline-build-step,blueocean-pipeline-scm-api,blueocean-autofavorite,workflow-aggregator,jenkins-design-language,pipeline-stage-view,ws-cleanup" 
def plugins =pluginParameter.split(",") 
println("" + plugins) 
def instance =Jenkins.getInstance() 
def pm = instance.getPluginManager() 
def uc =instance.getUpdateCenter() 
uc.updateAllSites()

plugins.each {   
println("Checking " + it)   
if(!pm.getPlugin(it)) {
    println("Looking UpdateCenter for " + it)
    if (!initialized) {
      uc.updateAllSites()
      initialized = true
    }
    def plugin = uc.getPlugin(it)
    if (plugin) {
      println("Installing " + it)
        plugin.deploy()
      installed = true
    }   } }

if (installed) 
   {  
      println("Plugins installed, initializing a   restart!")   
       instance.save()  
       instance.doSafeRestart()
 }
