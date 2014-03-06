package com.zend.zendserver.bamboo.Process;

import java.util.Arrays;
import java.util.List;


import com.atlassian.bamboo.configuration.ConfigurationMap;
import com.atlassian.bamboo.task.TaskContext;
import com.zend.zendserver.bamboo.Env.Build;
import com.zend.zendserver.bamboo.Env.BuildEnv;

public class PackagingProcess implements Process {
	
	public static final String OUTPUT_FILE_PREFIX = "zwsa/pack-";
	public static final String OUTPUT_FILE_SUFFIX = ".log";
	
	private final ConfigurationMap configMap;
	private final ExecutableHelper executableHelper;
	private BuildEnv buildEnv;
	
	public PackagingProcess(ConfigurationMap configMap, ExecutableHelper executableHelper)
    {
		this.configMap = configMap;
		this.executableHelper = executableHelper;
    }
	
	public void setBuildEnv(BuildEnv buildEnv) {
		this.buildEnv = buildEnv;
	}
	
	public List<String> getCommandList() throws Exception {
		List<String> commandList = Arrays.asList(
				executableHelper.getExecutable(),
				"packZpk",
				"--folder=" + buildEnv.getWorkingDir(),
				"--destination=" + buildEnv.getZpkDir(),
				"--name=" + buildEnv.getZpkFileName(),
				"--version=" + buildEnv.getVersion());
		
		return commandList;
	}

	public String getOutputFilePrefix() {
		return OUTPUT_FILE_PREFIX;
	}

	public String getOutputFileSuffix() {
		return OUTPUT_FILE_SUFFIX;
	}
}
