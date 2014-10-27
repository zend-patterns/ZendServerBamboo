package com.zend.zendserver.bamboo.Process;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.atlassian.bamboo.configuration.ConfigurationMap;
import com.zend.zendserver.bamboo.Env.BuildEnv;

public class DeploymentProcess implements Process {
	
	public static final String OUTPUT_FILE_PREFIX = "installApp-";
	public static final String OUTPUT_FILE_SUFFIX = ".xml";
	
	private final ConfigurationMap configMap;
	private ExecutableHelper executableHelper;
	
	private BuildEnv buildEnv;
	
	public DeploymentProcess(ConfigurationMap configMap)
    {
		this.configMap = configMap;
    }
	
	public DeploymentProcess(ConfigurationMap configMap, ExecutableHelper executableHelper)
    {
		this.configMap = configMap;
		this.executableHelper = executableHelper;
    }
	
	public void setExecutableHelper(ExecutableHelper executableHelper) {
		this.executableHelper = executableHelper;
	}
	
	public void setBuildEnv(BuildEnv buildEnv) {
		this.buildEnv = buildEnv;
	}
	
	public BuildEnv getBuildEnv() {
		return buildEnv;
	}
	
	public List<String> getCommandList() throws Exception {
		List<String> commandList = new ArrayList<String>();
		
		commandList.add(executableHelper.getExecutable());
		commandList.add("installApp");
		commandList.add("--zpk=" + buildEnv.getZpkPath());
		commandList.add("--baseUri=" + configMap.get("base_url"));
		commandList.add("--userAppName=" + configMap.get("app_name"));
		commandList.add("--zsurl=" + configMap.get("zs_url"));
		commandList.add("--zskey=" + configMap.get("api_key"));
		commandList.add("--zssecret=" + configMap.get("api_secret"));
		commandList.add("--zsversion=" + configMap.get("zsversion"));
		
		if (!StringUtils.isEmpty(configMap.get("userparams"))) {
			commandList.add("--userParams=" + configMap.get("userparams"));
		}
		
		return commandList;
	}

	public String getOutputFilePrefix() {
		return OUTPUT_FILE_PREFIX;
	}

	public String getOutputFileSuffix() {
		return OUTPUT_FILE_SUFFIX;
	}
}
