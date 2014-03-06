package com.zend.zendserver.bamboo.Process;

import java.util.Arrays;
import java.util.List;


import com.atlassian.bamboo.configuration.ConfigurationMap;
import com.atlassian.bamboo.task.TaskContext;
import com.zend.zendserver.bamboo.Env.BuildEnv;

public class ApplicationGetDetailsProcess implements Process {
	
	public static final String OUTPUT_FILE_PREFIX = "applicationGetDetails-";
	public static final String OUTPUT_FILE_SUFFIX = ".xml";
	
	private final ConfigurationMap configMap;
	private final ExecutableHelper executableHelper;
	private String applicationId;
	
	private static int testIteration = 0;
	
	public ApplicationGetDetailsProcess(ConfigurationMap configMap, ExecutableHelper executableHelper)
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
				"applicationGetDetails",
				"--application=" + applicationId,
				"--zsurl=" + configMap.get("zs_url"),
				"--zskey=" + configMap.get("api_key"),
        		"--zssecret=" + configMap.get("api_secret"),
        		"--zsversion=" + configMap.get("zsversion"));
		
		return commandList;
	}
	
	public void incTestIteration() {
		testIteration++;
	}

	public String getOutputFilePrefix() {
		return OUTPUT_FILE_PREFIX;
	}

	public String getOutputFileSuffix() {
		return "-" + String.valueOf(testIteration) + OUTPUT_FILE_SUFFIX;
	}

	public void setBuildEnv(BuildEnv buildEnv) {
	}
}
