package com.zend.zendserver.bamboo.Process;

import java.util.List;

import com.zend.zendserver.bamboo.Env.BuildEnv;

public interface Process {
	public String getOutputFilePrefix();
	public String getOutputFileSuffix();
	public void setBuildEnv(BuildEnv buildEnv);
	public BuildEnv getBuildEnv();
	public List<String> getCommandList() throws Exception;
}
