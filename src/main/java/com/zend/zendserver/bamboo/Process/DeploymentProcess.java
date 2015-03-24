package com.zend.zendserver.bamboo.Process;

import java.util.Arrays;
import java.util.LinkedList;
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
		List<String> commandList = new LinkedList<String>(Arrays.asList(
				executableHelper.getExecutable(),
				"installApp",
				"--zpk=" + buildEnv.getZpkPath(),
				"--baseUri=" + configMap.get("base_url"),
				"--userAppName=" + configMap.get("app_name"),
				"--zsurl=" + configMap.get("zs_url"),
				"--zskey=" + configMap.get("api_key"),
				"--zssecret=" + configMap.get("api_secret"),
				"--zsversion=" + configMap.get("zsversion")));
		
		if (!StringUtils.isEmpty(configMap.get("userparams"))) {
			commandList.add("--userParams=" + configMap.get("userparams"));
		}
		
		if (!StringUtils.isEmpty(configMap.get("custom_options"))) {
			commandList.add(configMap.get("custom_options"));
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
