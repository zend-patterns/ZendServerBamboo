package org.zend.zendserver.bamboo.plugin.Process;

import java.util.List;

import org.zend.zendserver.bamboo.plugin.Env.BuildEnv;

public interface Process {
	public String getOutputFilePrefix();
	public String getOutputFileSuffix();
	public void setBuildEnv(BuildEnv buildEnv);
	public List<String> getCommandList() throws Exception;
}
