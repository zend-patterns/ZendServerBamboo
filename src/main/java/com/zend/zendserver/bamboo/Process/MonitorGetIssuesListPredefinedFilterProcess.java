package com.zend.zendserver.bamboo.Process;

import java.util.Arrays;
import java.util.List;

import com.atlassian.bamboo.configuration.ConfigurationMap;
import com.zend.zendserver.bamboo.Env.BuildEnv;

public class MonitorGetIssuesListPredefinedFilterProcess implements Process {
	
	public static final String OUTPUT_FILE_PREFIX = "zs-webapi-res/statistics/monitorGetIssuesListPredefinedFilter-";
	public static final String OUTPUT_FILE_SUFFIX = ".xml";
	
	private final ConfigurationMap configMap;
	private final ExecutableHelper executableHelper;
	private BuildEnv buildEnv;
	private String filterId;
	private String from;
	private String to;
	
	private boolean periodBeforeDeployment = false;
	
	public MonitorGetIssuesListPredefinedFilterProcess(ConfigurationMap configMap, ExecutableHelper executableHelper)
    {
		this.configMap = configMap;
		this.executableHelper = executableHelper;
    }
	
	public List<String> getCommandList() throws Exception {
		List<String> commandList = Arrays.asList(
				executableHelper.getExecutable(),
				"monitorGetIssuesListPredefinedFilter",
				"--filterId=" + filterId,
				"--filters=\"from=" + from + "&to=" + to + "\"",
				"--zsurl=" + configMap.get("zs_url"),
				"--zskey=" + configMap.get("api_key"),
        		"--zssecret=" + configMap.get("api_secret"),
        		"--zsversion=" + configMap.get("zsversion"));
		
		return commandList;
	}

	public String getOutputFilePrefix() {
		return OUTPUT_FILE_PREFIX;
	}

	public void setBuildEnv(BuildEnv buildEnv) {
		this.buildEnv = buildEnv;
	}
	
	public BuildEnv getBuildEnv() {
		return buildEnv;
	}

	public String getFilterId() {
		return filterId;
	}

	public void setFilterId(String filterId) {
		this.filterId = filterId;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getOutputFileSuffix() {
		String suffix = "";
		if (isPeriodBeforeDeployment()) {
			suffix += "-periodBeforeDeployment";
		}
		suffix += OUTPUT_FILE_SUFFIX;
		return suffix;
	}

	public boolean isPeriodBeforeDeployment() {
		return periodBeforeDeployment;
	}

	public void setPeriodBeforeDeployment(boolean periodBeforeDeployment) {
		this.periodBeforeDeployment = periodBeforeDeployment;
	}
}
