package com.zend.zendserver.bamboo.Process;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.atlassian.bamboo.configuration.ConfigurationMap;
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
	
	public BuildEnv getBuildEnv() {
		return buildEnv;
	}
	
	public List<String> getCommandList() throws Exception {
		List<String> commandList = new LinkedList<String>(Arrays.asList(
				executableHelper.getExecutable(),
				"packZpk",
				"--folder=" + buildEnv.getWorkingDir(),
				"--destination=" + buildEnv.getZpkDir(),
				"--name=" + buildEnv.getZpkFileName(),
				"--version=" + buildEnv.getVersion()));
		
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
