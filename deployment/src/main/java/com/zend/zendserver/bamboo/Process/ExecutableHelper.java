package com.zend.zendserver.bamboo.Process;

import com.atlassian.bamboo.v2.build.agent.capability.CapabilityContext;

public class ExecutableHelper {
	public final static String EXEC_KEY_PHAR = "system.zswa-client.executable";
	public final static String EXEC_KEY_PHP = "system.zswa-client.executable.php";
	
	private CapabilityContext capabilityContext;
	
	public ExecutableHelper (CapabilityContext capabilityContext) {
		this.capabilityContext = capabilityContext;
	}
	
	public String getExecutable() throws Exception {
		String executable = "";
		
		if (capabilityContext.getCapabilityValue(EXEC_KEY_PHAR) != null &&
				!capabilityContext.getCapabilityValue(EXEC_KEY_PHAR).isEmpty()) {
			executable = capabilityContext.getCapabilityValue(EXEC_KEY_PHAR);
		}
		else if (capabilityContext.getCapabilityValue(EXEC_KEY_PHP) != null &&
				!capabilityContext.getCapabilityValue(EXEC_KEY_PHP).isEmpty()) {
			executable = capabilityContext.getCapabilityValue(EXEC_KEY_PHP);
		}
		else
		{
			throw new Exception("Could not find valid executable!");
		}
		
		return executable;
	}
}
