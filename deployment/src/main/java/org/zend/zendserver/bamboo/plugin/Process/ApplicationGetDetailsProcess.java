package org.zend.zendserver.bamboo.plugin.Process;

import java.util.Arrays;
import java.util.List;

import com.atlassian.bamboo.configuration.ConfigurationMap;
import com.atlassian.bamboo.task.TaskContext;

public class ApplicationGetDetailsProcess implements Process {
	
	public static final String OUTPUT_FILE_PREFIX = "applicationGetDetails-";
	public static final String OUTPUT_FILE_SUFFIX = ".xml";
	
	private final TaskContext taskContext;
	private final ExecutableHelper executableHelper;
	private String applicationId;
	
	private static int testIteration = 0;
	
	public ApplicationGetDetailsProcess(TaskContext taskContext, ExecutableHelper executableHelper)
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
				"applicationGetDetails",
				"--application=" + applicationId,
				"--zsurl=" + configMap.get("url"),
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
}
