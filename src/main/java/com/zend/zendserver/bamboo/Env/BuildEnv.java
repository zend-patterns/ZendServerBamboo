package com.zend.zendserver.bamboo.Env;

public interface BuildEnv {
	public String getVersion() throws Exception;
	public String getBuildNr();
	public String getWorkingDir();
	public String getZpkDir() throws Exception;
	public String getZpkPath() throws Exception;
	public String getZpkFileName() throws Exception;
}

