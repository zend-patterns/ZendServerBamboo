package org.zend.zendserver.bamboo.plugin.Process;

import java.util.List;

public interface Process {
	public String getOutputFilePrefix();
	public String getOutputFileSuffix();
	public List<String> getCommandList() throws Exception;
}
