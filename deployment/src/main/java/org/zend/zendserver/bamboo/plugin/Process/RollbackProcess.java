package org.zend.zendserver.bamboo.plugin.Process;

import java.util.Arrays;
import java.util.List;

import com.atlassian.bamboo.configuration.ConfigurationMap;
import com.atlassian.bamboo.task.TaskContext;

public class RollbackProcess implements Process {
	
	public static final String OUTPUT_FILE_PREFIX = "zwsa/rollback-";
	public static final String OUTPUT_FILE_SUFFIX = ".xml";
	
	private final TaskContext taskContext;
	private final ExecutableHelper executableHelper;
	private String applicationId;
	
	public RollbackProcess(TaskContext taskContext, ExecutableHelper executableHelper)
    {
		this.taskContext = taskContext;
		this.executableHelper = executableHelper;
    }
	
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
	
	public List<String> getCommandList() throws Exception {
		ConfigurationMap configMap = taskContext.getConfigurationMap();
		
		List<String> commandList = Arrays.asList(
				executableHelper.getExecutable(),
				"applicationRollback",
				"--appId=" + applicationId,
				"--zsurl=" + configMap.get("url"),
				"--zskey=" + configMap.get("api_key"),
        		"--zssecret=" + configMap.get("api_secret"),
        		"--zsversion=" + configMap.get("zsversion"));
		
		return commandList;
	}

	public String getOutputFilePrefix() {
		return OUTPUT_FILE_PREFIX;
	}

	public String getOutputFileSuffix() {
		return OUTPUT_FILE_SUFFIX;
	}
}
