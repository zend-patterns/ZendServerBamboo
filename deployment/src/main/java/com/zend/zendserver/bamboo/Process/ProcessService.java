package com.zend.zendserver.bamboo.Process;

import com.atlassian.bamboo.configuration.ConfigurationMap;
import com.atlassian.bamboo.v2.build.agent.capability.CapabilityContext;
import com.zend.zendserver.bamboo.Env.BuildEnv;

public class ProcessService {
	private CapabilityContext capabilityContext;
	private ConfigurationMap configMap;
	private BuildEnv buildEnv;
	private ExecutableHelper eh = null; 
	
	private ApplicationGetDetailsProcess applicationGetDetails = null;
	private ApplicationGetStatusProcess applicationGetStatus = null;
	private DeploymentProcess deployment = null;
	private PackagingProcess packaging = null;
	private RollbackProcess rollback = null;
	
	public CapabilityContext getCapabilityContext() {
		return capabilityContext;
	}

	public void setCapabilityContext(CapabilityContext capabilityContext) {
		this.capabilityContext = capabilityContext;
	}
	
	public ConfigurationMap getConfigMap() {
		return configMap;
	}

	public void setConfigMap(ConfigurationMap configMap) {
		this.configMap = configMap;
	}
	
	public BuildEnv getBuildEnv() {
		return buildEnv;
	}

	public void setBuildEnv(BuildEnv buildEnv) {
		this.buildEnv = buildEnv;
	}
	
	private ExecutableHelper getExecutableHelper() {
		if (eh == null) {
			eh = new ExecutableHelper(capabilityContext);
		}
		
		return eh;
	}
	
	public ApplicationGetDetailsProcess applicationGetDetails() {
		applicationGetDetails = new ApplicationGetDetailsProcess(configMap, getExecutableHelper());
		applicationGetDetails.setBuildEnv(buildEnv);
		return applicationGetDetails;
	}
	
	public DeploymentProcess deployment() {
		deployment = new DeploymentProcess(configMap, getExecutableHelper());
		deployment.setBuildEnv(buildEnv);
		return deployment;
	}
	
	public PackagingProcess packaging() {
		packaging = new PackagingProcess(getExecutableHelper());
		packaging.setBuildEnv(buildEnv);
		
		return packaging;
	}

	public ApplicationGetStatusProcess applicationGetStatus() {
		applicationGetStatus = new ApplicationGetStatusProcess(configMap, getExecutableHelper());
		applicationGetStatus.setBuildEnv(buildEnv);
		
		return applicationGetStatus;
	}

	public RollbackProcess rollback() {
		rollback = new RollbackProcess(configMap, getExecutableHelper());
		rollback.setBuildEnv(buildEnv);
		
		return rollback;
	}
}
