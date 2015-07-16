package com.zend.zendserver.bamboo.Process;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.atlassian.bamboo.configuration.ConfigurationMap;
import com.zend.zendserver.bamboo.Env.BuildEnv;

public class RollbackProcess implements Process {
	
	public static final String OUTPUT_FILE_PREFIX = "zwsa/rollback-";
	public static final String OUTPUT_FILE_SUFFIX = ".xml";
	
	private final ConfigurationMap configMap;
	private final ExecutableHelper executableHelper;
	private String applicationId;
	
	private BuildEnv buildEnv;
	
	public RollbackProcess(ConfigurationMap configMap, ExecutableHelper executableHelper)
    {
		this.configMap = configMap;
		this.executableHelper = executableHelper;
    }
	
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
	
	public List<String> getCommandList() throws Exception {
		List<String> commandList = Arrays.asList(
				executableHelper.getExecutable(),
				"applicationRollback",
				"--appId=" + applicationId,
				"--zsurl=" + configMap.get("zs_url"),
				"--zskey=" + configMap.get("api_key"),
        		"--zssecret=" + configMap.get("api_secret"),
        		"--zsversion=" + configMap.get("zsversion"));
		
		if (!StringUtils.isEmpty(configMap.get("custom_options"))) {
			commandList.add(" " + configMap.get("custom_options"));
		}
		
		return commandList;
	}

	public String getOutputFilePrefix() {
		return OUTPUT_FILE_PREFIX;
	}

	public String getOutputFileSuffix() {
		return OUTPUT_FILE_SUFFIX;
	}

	public void setBuildEnv(BuildEnv buildEnv) {
		this.buildEnv = buildEnv;
	}
	
	public BuildEnv getBuildEnv() {
		return buildEnv;
	}
}
