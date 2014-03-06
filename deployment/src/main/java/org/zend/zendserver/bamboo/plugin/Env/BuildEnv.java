package org.zend.zendserver.bamboo.plugin.Env;

import com.atlassian.bamboo.build.logger.BuildLogger;

public interface BuildEnv {
	public String getVersion() throws Exception;
	public String getWorkingDir();
	public String getZpkDir();
	public String getZpkPath() throws Exception;
	public String getZpkFileName() throws Exception;
}

