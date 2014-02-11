package org.zend.zendserver.bamboo.plugin.ZendServerSDK;

import com.atlassian.bamboo.build.logger.BuildLogger;

public class Call {
	private BuildLogger buildLogger;
	private Process process;
	public Call(BuildLogger buildLogger) {
		this.buildLogger = buildLogger;
	}
	
	public void execute(String cmd) {
		try {
			buildLogger.addBuildLogEntry("Executed cmd: " +  cmd);
			ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
			process = pb.start();
			process.waitFor();
		}
		catch (Exception e) {
			buildLogger.addErrorLogEntry(e.getMessage());
		}
	}
	
	public Boolean isFailed() {
		return !(process.exitValue() == 0);
	}
}
