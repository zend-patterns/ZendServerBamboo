package org.zend.zendserver.bamboo.plugin.Process;

import java.util.Arrays;
import java.util.List;

import org.zend.zendserver.bamboo.plugin.Helper.Build;
import org.zend.zendserver.bamboo.plugin.Helper.Zpk;

import com.atlassian.bamboo.configuration.ConfigurationMap;
import com.atlassian.bamboo.task.TaskContext;

public class DeploymentProcess implements Process {
	
	public static final String OUTPUT_FILE_PREFIX = "zwsa/installApp-";
	public static final String OUTPUT_FILE_SUFFIX = ".xml";
	
	private final TaskContext taskContext;
	private ExecutableHelper executableHelper;
	
	public DeploymentProcess(TaskContext taskContext)
    {
		this.taskContext = taskContext;
    }
	
	public DeploymentProcess(TaskContext taskContext, ExecutableHelper executableHelper)
    {
		this.taskContext = taskContext;
		this.executableHelper = executableHelper;
    }
	
	public void setExecutableHelper(ExecutableHelper executableHelper) {
		this.executableHelper = executableHelper;
	}
	
	public List<String> getCommandList() throws Exception {
		Build build = new Build(taskContext);
		Zpk zpk = new Zpk(taskContext, build);
		
		ConfigurationMap configMap = taskContext.getConfigurationMap();
		
		List<String> commandList = Arrays.asList(
				executableHelper.getExecutable(),
				"installApp",
				"--zpk=" + zpk.getPath(),
				"--baseUri=" + configMap.get("base_url"),
				"--userAppName=" + configMap.get("app_name"),
				"--zsurl=" + configMap.get("zs_url"),
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
