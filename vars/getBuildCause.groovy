def call(){
	def isUser = currentBuild.rawBuild.getCause(hudson.model.Cause$UserIdCause)
	if(isUser){
			return isUser.getShortDescription() 
	}
	def isTimer = currentBuild.rawBuild.getCause(hudson.triggers.TimerTrigger$TimerTriggerCause)
	if(isTimer){
			return isTimer.getShortDescription()
	}
	def isSCM = currentBuild.rawBuild.getCause(hudson.triggers.SCMTrigger$SCMTriggerCause)
	if(isSCM){
			return isSCM.getShortDescription()
	}
	def isUpstream = currentBuild.rawBuild.getCause(hudson.model.Cause$UpstreamCause)
	if(isUpstream){
		def upstreamJob
		try { 
			upstreamJob = Jenkins.instance.getItemByFullName(isUpstream.getUpstreamProject()).getBuildByNumber(isUpstream.getUpstreamBuild()).getCause(hudson.model.Cause$UserIdCause)
			if(upstreamJob){
				return upstreamJob.getShortDescription()
			}
		}catch(Exception ex) {}
		return isUpstream.getShortDescription() 
	}
	def isDeepUpstream = currentBuild.rawBuild.getCause(hudson.model.Cause.UpstreamCause$DeeplyNestedUpstreamCause)
	if(isDeepUpstream){
			return isDeepUpstream.getShortDescription() 
	}
	def isRemote = currentBuild.rawBuild.getCause(hudson.model.Cause$RemoteCause)
	if(isRemote){
			return isRemote.getShortDescription() 
	}
	def isUserName = currentBuild.rawBuild.getCause(Cause.UserIdCause).getUserId
	if(isUserName){
			return isUserName()
	}
	//else
	return 'Started by Unknown Cause'
}
