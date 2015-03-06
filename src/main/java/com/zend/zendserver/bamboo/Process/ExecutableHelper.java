package com.zend.zendserver.bamboo.Process;

import com.atlassian.bamboo.exception.StartupException;
import com.atlassian.bamboo.v2.build.agent.capability.CapabilityContext;
import com.zend.zendserver.bamboo.ZendServerWebApiClientCapability;

public class ExecutableHelper {
	private CapabilityContext capabilityContext;
	
	public ExecutableHelper (CapabilityContext capabilityContext) {
		this.capabilityContext = capabilityContext;
	}
	
	public String getExecutable() throws Exception {
		String executable = "";
		if (capabilityContext.getCapabilityValue(ZendServerWebApiClientCapability.EXECUTABLE_KEY_PHAR) != null &&
				!capabilityContext.getCapabilityValue(ZendServerWebApiClientCapability.EXECUTABLE_KEY_PHAR).isEmpty()) {
			executable = capabilityContext.getCapabilityValue(ZendServerWebApiClientCapability.EXECUTABLE_KEY_PHAR);
		}
		else if (capabilityContext.getCapabilityValue(ZendServerWebApiClientCapability.EXECUTABLE_KEY_PHP) != null &&
				!capabilityContext.getCapabilityValue(ZendServerWebApiClientCapability.EXECUTABLE_KEY_PHP).isEmpty()) {
			executable = capabilityContext.getCapabilityValue(ZendServerWebApiClientCapability.EXECUTABLE_KEY_PHP);
		}
		else
		{
			throw new StartupException("Could not find valid executable!");
		}
		
		return executable;
	}
}
