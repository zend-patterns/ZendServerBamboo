package org.zend.zendserver.bamboo.plugin.Process;

import java.util.Arrays;
import java.util.List;

import org.zend.zendserver.bamboo.plugin.Env.BuildEnv;

import com.atlassian.bamboo.configuration.ConfigurationMap;

public class DeploymentProcess implements Process {
	
	public static final String OUTPUT_FILE_PREFIX = "zwsa/installApp-";
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
	
	public List<String> getCommandList() throws Exception {
		List<String> commandList = Arrays.asList(
				executableHelper.getExecutable(),
				"installApp",
				"--zpk=" + buildEnv.getZpkPath(),
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
