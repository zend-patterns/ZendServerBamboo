package org.zend.zendserver.plugins;

import com.atlassian.bamboo.build.logger.BuildLogger;
import com.atlassian.bamboo.configuration.ConfigurationMap;

public class ZendServerSDKCall {
	
	private BuildLogger buildLogger;

	public ZendServerSDKCall(BuildLogger buildLogger) {
		this.buildLogger = buildLogger;
	}
	
	public String getApplicationGetDetailsCmd(ConfigurationMap configMap, String applicationId, String applicationGetDetailsFilePath) {
        String placeholder = "%s applicationGetDetails --application=%s --zsurl=%s --zskey=%s --zssecret=%s --zsversion=%s > %s";
        String cmd = String.format(placeholder,
        		configMap.get("zs_client_location"),
        		applicationId,
        		configMap.get("url"),
        		configMap.get("api_key"),
        		configMap.get("api_secret"),
        		configMap.get("zsversion"),
        		applicationGetDetailsFilePath);
        
        return cmd;
	}
	
	public String getApplicationRollbackCmd(ConfigurationMap configMap, String applicationId, String applicationRollbackFilePath) {
		String placeholder = "%s applicationRollback --appId=%s --zsurl=%s --zskey=%s --zssecret=%s --zsversion=%s > %s";
		String cmd = String.format(placeholder,
				configMap.get("zs_client_location"),
				applicationId,
				configMap.get("url"),
				configMap.get("api_key"),
				configMap.get("api_secret"),
				configMap.get("zsversion"),
				applicationRollbackFilePath);
		
		return cmd;
	}
	
	public void execute(String cmd) {
		try {
			buildLogger.addBuildLogEntry("Executed cmd: " +  cmd);
			ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
			Process process = pb.start();
			process.waitFor();
		}
		catch (Exception e) {
			buildLogger.addErrorLogEntry(e.getMessage());
		}
	}
}
